package com.yandex.div.compose.screenshot

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.asList
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div2.DivAction
import org.json.JSONException
import org.json.JSONObject

class InteractiveScreenshotTestConfiguration(
    name: String,
    private val json: JSONObject
) {
    val baseConfiguration = ScreenshotTestConfiguration(
        name = name,
        json = json.getJSONObject("div_data"),
        configurationJson = json.optJSONObject("configuration"),
    )

    val steps: List<Step> by lazy {
        val environment = DivParsingEnvironment(logger = throwingErrorLogger)
        json.getJSONArray("steps").asList<JSONObject>().map { step ->
            when (val type = step.getString("type")) {
                "div_action" -> Step.Action(
                    action = DivAction(environment, step.getJSONObject("action"))
                )

                "verify_snapshot" -> Step.VerifySnapshot(
                    name = step.getString("name")
                )

                "wait" -> Step.Wait(
                    durationMs = step.getLong("duration_ms")
                )

                else -> throw JSONException("Unknown interactive step type: $type")
            }
        }
    }

    sealed class Step {
        data class Action(val action: DivAction) : Step()
        data class VerifySnapshot(val name: String) : Step()
        data class Wait(val durationMs: Long) : Step()
    }

    override fun toString() = baseConfiguration.name
}
