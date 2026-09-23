package com.yandex.div.test.crossplatform

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

        class Wait(val delay: Long) : Step()

        class VerifySnapshot(val name: String) : Step()

        class VerifyText(val id: String, val text: String) : Step()
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
                    Step.VerifySnapshot(name)
                }

                "verify_text" -> {
                    val target = step.getJSONObject("target")
                    val targetType = target.getString("type")
                    if (targetType != "div_id") {
                        throw JSONException("Unsupported verify_text target type: $targetType")
                    }
                    Step.VerifyText(target.getString("id"), step.getString("text"))
                }
                else -> throw JSONException("Unknown interactive step type: $type")
            }
        }
    }
}
