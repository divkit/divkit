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

        class Tap(val id: String) : Step()

        class DoubleTap(val id: String) : Step()

        class LongTap(val id: String) : Step()

        class Swipe(val id: String, val from: PointF, val to: PointF) : Step()

        class Wait(val delay: Long) : Step()

        class VerifySnapshot(val name: String, val id: String? = null) : Step()

        class VerifyText(val id: String, val text: String) : Step()

        class VerifyFocus(val id: String, val focused: Boolean) : Step()
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

                "tap" -> Step.Tap(parseTargetId(step))

                "double_tap" -> Step.DoubleTap(parseTargetId(step))

                "long_tap" -> Step.LongTap(parseTargetId(step))

                "swipe" -> parseSwipe(step)

                "wait" -> {
                    val delay = step.getLong("duration_ms")
                    if (delay <= 0) {
                        throw JSONException("wait duration_ms must be positive, got: $delay")
                    }
                    Step.Wait(delay)
                }

                "verify_snapshot" -> {
                    val name = step.getString("name")
                    if (name.isEmpty()) {
                        throw JSONException("verify_snapshot name must not be empty")
                    }
                    Step.VerifySnapshot(name, if (step.has("target")) parseTargetId(step) else null)
                }

                "verify_text" -> Step.VerifyText(parseTargetId(step), step.getString("text"))

                "verify_focus" -> Step.VerifyFocus(parseTargetId(step), step.getBoolean("focused"))

                else -> throw JSONException("Unknown interactive step type: $type")
            }
        }

        private fun parseTargetId(step: JSONObject): String {
            val target = step.getJSONObject("target")
            val targetType = target.getString("type")
            if (targetType != "div_id") {
                throw JSONException("Unsupported ${step.getString("type")} target type: $targetType")
            }
            return target.getString("id")
        }

        private const val SWIPE_DIRECTION = "direction"
        private const val SWIPE_FROM = "from"
        private const val SWIPE_TO = "to"

        private fun parseSwipe(step: JSONObject): Step.Swipe {
            val id = parseTargetId(step)
            return if (step.has(SWIPE_DIRECTION)) {
                if (step.has(SWIPE_FROM) || step.has(SWIPE_TO)) {
                    throw JSONException(
                        "Swipe must specify either direction or from/to coordinates, not both"
                    )
                }
                val from = when (val value = step.getString(SWIPE_DIRECTION)) {
                    "left" -> PointF(0.75f, 0.5f)
                    "right" -> PointF(0.25f, 0.5f)
                    "up" -> PointF(0.5f, 0.75f)
                    "down" -> PointF(0.5f, 0.25f)
                    else -> throw JSONException("Unsupported swipe direction: $value")
                }
                val to = PointF(1f - from.x, 1f - from.y)
                Step.Swipe(id, from, to)
            } else {
                val from = parsePoint(step.getJSONObject(SWIPE_FROM))
                val to = parsePoint(step.getJSONObject(SWIPE_TO))
                Step.Swipe(id, from, to)
            }
        }

        private fun parsePoint(point: JSONObject): PointF {
            val x = point.getDouble("x").toFloat()
            val y = point.getDouble("y").toFloat()
            if (x !in 0f..1f || y !in 0f..1f) {
                throw JSONException("Swipe coordinates must be between 0 and 1, got: ($x, $y)")
            }
            return PointF(x, y)
        }
    }
}
