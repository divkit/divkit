package com.yandex.div.test.crossplatform

import android.graphics.PointF
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.asList
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div2.DivAction
import org.json.JSONException
import org.json.JSONObject

class InteractiveScreenshotTestData(
    val steps: List<Step>
) {
    sealed class Step {
        class Action(val action: DivAction) : Step()

        class Tap(val target: Target, val point: PointF?) : Step()

        class DoubleTap(val target: Target, val point: PointF?) : Step()

        class LongTap(val target: Target, val point: PointF?) : Step()

        class Swipe(
            val target: Target,
            val from: PointF,
            val to: PointF,
            val holdDurationMs: Long,
        ) : Step()

        class EditText(val target: Target, val mode: Mode, val text: String) : Step() {
            /**
             * Texts entered one by one: graphemes of [text], or an empty text that deletes the
             * selection.
             */
            val inputs: List<String> = when {
                mode == Mode.REPLACE && text.isEmpty() -> listOf("")
                else -> Regex("\\X").findAll(text).map { it.value }.toList()
            }

            enum class Mode { REPLACE, APPEND }
        }

        class Wait(val delay: Long) : Step()

        class ScrollTo(
            val container: Target,
            val destination: Target,
            val direction: String,
            val from: PointF,
            val to: PointF,
        ) : Step() {
            companion object {
                const val MAX_SWIPES = 10

                /** A slow swipe keeps the inertia short, so it does not skip over items. */
                const val SWIPE_DURATION_MS = 1500L
            }
        }

        class VerifySnapshot(val name: String, val target: Target? = null) : Step()

        class VerifyText(val target: Target, val text: String) : Step()

        class VerifyFocus(val target: Target, val focused: Boolean) : Step()

        class VerifyLog(val source: Source, val id: String, val count: Int?) : Step() {
            enum class Source {
                VISIBILITY, DISAPPEAR;

                override fun toString() = name.lowercase()
            }
        }
    }

    sealed class Target {
        class DivId(val id: String) : Target() {
            override fun toString() = "div '$id'"
        }

        class Text(val text: String) : Target() {
            override fun toString() = "text '$text'"
        }
    }

    companion object {

        private val parsingEnvironment = DivParsingEnvironment(throwingErrorLogger)

        fun parse(json: JSONObject): InteractiveScreenshotTestData {
            return InteractiveScreenshotTestData(
                json.optJSONArray("steps")
                    ?.asList<JSONObject>()
                    .orEmpty()
                    .map { parseStep(it) }
            )
        }

        private fun parseStep(step: JSONObject): Step {
            return when (val type = step.getString("type")) {
                "div_action" -> {
                    Step.Action(
                        DivAction(parsingEnvironment, step.getJSONObject("action"))
                    )
                }

                "tap" -> Step.Tap(parseTarget(step), parseTapPoint(step))

                "double_tap" -> Step.DoubleTap(parseTarget(step), parseTapPoint(step))

                "long_tap" -> Step.LongTap(parseTarget(step), parseTapPoint(step))

                "swipe" -> parseSwipe(step)

                "edit_text" -> Step.EditText(
                    parseTarget(step),
                    parseEditMode(step),
                    step.getString("text"),
                )

                "wait" -> {
                    val delay = step.getLong("duration_ms")
                    if (delay <= 0) {
                        throw JSONException("wait duration_ms must be positive, got: $delay")
                    }
                    Step.Wait(delay)
                }

                "scroll_to" -> parseScrollTo(step)

                "verify_snapshot" -> {
                    val name = step.getString("name")
                    if (name.isEmpty()) {
                        throw JSONException("verify_snapshot name must not be empty")
                    }
                    Step.VerifySnapshot(name, if (step.has("target")) parseTarget(step) else null)
                }

                "verify_text" -> Step.VerifyText(parseTarget(step), step.getString("text"))

                "verify_focus" -> Step.VerifyFocus(parseTarget(step), step.getBoolean("focused"))

                "verify_log" -> parseVerifyLog(step)

                else -> throw JSONException("Unknown interactive step type: $type")
            }
        }

        private fun parseTarget(step: JSONObject, key: String = "target"): Target {
            val target = step.getJSONObject(key)
            return when (val targetType = target.getString("type")) {
                "div_id" -> Target.DivId(target.getNonBlankString("id"))
                "text" -> Target.Text(target.getNonBlankString("text"))
                else -> throw JSONException("Unsupported ${step.getString("type")} target type: $targetType")
            }
        }

        private fun JSONObject.getNonBlankString(name: String): String {
            val value = getString(name)
            if (value.isBlank()) {
                throw JSONException("target $name must not be empty")
            }
            return value
        }

        private const val TAP_POINT = "point"

        private fun parseEditMode(step: JSONObject): Step.EditText.Mode {
            return when (val mode = step.getString("mode")) {
                "replace" -> Step.EditText.Mode.REPLACE
                "append" -> Step.EditText.Mode.APPEND
                else -> throw JSONException("Unsupported edit_text mode: $mode")
            }
        }

        private fun parseVerifyLog(step: JSONObject): Step.VerifyLog {
            val source = parseLogSource(step)
            val id = step.getString("id")
            if (id.isEmpty()) {
                throw JSONException("verify_log id must not be empty")
            }
            val count = if (step.has("count")) step.getInt("count") else null
            if (count != null && count < 0) {
                throw JSONException("verify_log count must be nonnegative, got: $count")
            }
            return Step.VerifyLog(source, id, count)
        }

        private fun parseLogSource(step: JSONObject): Step.VerifyLog.Source {
            return when (val source = step.getString("source")) {
                "visibility" -> Step.VerifyLog.Source.VISIBILITY
                "disappear" -> Step.VerifyLog.Source.DISAPPEAR
                else -> throw JSONException("Unsupported verify_log source: $source")
            }
        }

        private const val SWIPE_DIRECTION = "direction"
        private const val SWIPE_FROM = "from"
        private const val SWIPE_TO = "to"
        private const val SWIPE_HOLD_DURATION_MS = "hold_duration_ms"
        private const val DEFAULT_SWIPE_HOLD_DURATION_MS = 0L

        private fun parseSwipe(step: JSONObject): Step.Swipe {
            val target = parseTarget(step)
            val holdDurationMs = parseHoldDurationMs(step)
            return if (step.has(SWIPE_DIRECTION)) {
                if (step.has(SWIPE_FROM) || step.has(SWIPE_TO)) {
                    throw JSONException(
                        "Swipe must specify either direction or from/to coordinates, not both"
                    )
                }
                val from = when (val value = step.getString(SWIPE_DIRECTION)) {
                    "left" -> PointF(0.9f, 0.5f)
                    "right" -> PointF(0.1f, 0.5f)
                    "up" -> PointF(0.5f, 0.9f)
                    "down" -> PointF(0.5f, 0.1f)
                    else -> throw JSONException("Unsupported swipe direction: $value")
                }
                val to = PointF(1f - from.x, 1f - from.y)
                Step.Swipe(target, from, to, holdDurationMs)
            } else {
                val from = parsePoint(step.getJSONObject(SWIPE_FROM), "Swipe")
                val to = parsePoint(step.getJSONObject(SWIPE_TO), "Swipe")
                Step.Swipe(target, from, to, holdDurationMs)
            }
        }

        private fun parseHoldDurationMs(step: JSONObject): Long {
            val holdDurationMs = step.optLong(SWIPE_HOLD_DURATION_MS, DEFAULT_SWIPE_HOLD_DURATION_MS)
            if (holdDurationMs < 0) {
                throw JSONException("swipe hold_duration_ms must not be negative, got: $holdDurationMs")
            }
            return holdDurationMs
        }

        private fun parseTapPoint(step: JSONObject): PointF? {
            return if (step.isNull(TAP_POINT)) {
                null
            } else {
                parsePoint(step.getJSONObject(TAP_POINT), "${step.getString("type")} point")
            }
        }

        private fun parsePoint(point: JSONObject, name: String): PointF {
            val x = point.getDouble("x").toFloat()
            val y = point.getDouble("y").toFloat()
            if (x !in 0f..1f || y !in 0f..1f) {
                throw JSONException("$name coordinates must be between 0 and 1, got: ($x, $y)")
            }
            return PointF(x, y)
        }

        private fun parseScrollTo(step: JSONObject): Step.ScrollTo {
            val direction = step.getString("scroll_direction")
            // scroll_direction is where the destination is, so the finger moves the opposite way.
            // iOS drags a quarter of the container from its center, but a paging gallery in
            // Compose snaps such a short swipe back to the same item, so drag as swipe does.
            val from = when (direction) {
                "up" -> PointF(0.5f, 0.1f)
                "down" -> PointF(0.5f, 0.9f)
                "left" -> PointF(0.1f, 0.5f)
                "right" -> PointF(0.9f, 0.5f)
                else -> throw JSONException("Unsupported scroll_to scroll_direction: $direction")
            }
            return Step.ScrollTo(
                container = parseTarget(step, "container"),
                destination = parseTarget(step, "destination"),
                direction = direction,
                from = from,
                to = PointF(1f - from.x, 1f - from.y),
            )
        }
    }
}
