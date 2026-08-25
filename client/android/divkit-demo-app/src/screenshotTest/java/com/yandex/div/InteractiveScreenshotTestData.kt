package com.yandex.div

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.asList
import com.yandex.div2.DivAction
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.fail

class InteractiveScreenshotTestData(
    val divJson: JSONObject,
    val steps: List<Step>
) {
    class Step(
        val actions: List<DivAction>,
        val expectedScreenshot: String,
        val delay: Long,
    )

    companion object {

        fun parse(json: JSONObject): InteractiveScreenshotTestData {
            return InteractiveScreenshotTestData(
                divJson = json.getJSONObject("div_data"),
                steps = parseSteps(json)
            )
        }

        private fun parseSteps(json: JSONObject): List<Step> {
            val result = mutableListOf<Step>()
            val actions = mutableListOf<DivAction>()

            json.getJSONArray("steps").asList<JSONObject>().forEach { step ->
                when (val type = step.getString("type")) {
                    "div_action" -> actions.add(
                        DivAction(parsingEnvironment, step.getJSONObject("action"))
                    )
                    "wait" -> {
                        result.add(
                            Step(
                                actions = actions.toList(),
                                expectedScreenshot = "",
                                delay = step.getLong("duration_ms"),
                            )
                        )
                        actions.clear()
                    }
                    "verify_snapshot" -> {
                        result.add(
                            Step(
                                actions = actions.toList(),
                                expectedScreenshot = step.getString("name"),
                                delay = 0,
                            )
                        )
                        actions.clear()
                    }
                    else -> throw JSONException("Unknown interactive step type: $type")
                }
            }

            if (actions.isNotEmpty()) {
                result.add(Step(actions.toList(), expectedScreenshot = "", delay = 0))
            }
            return result
        }
    }
}

private val parsingEnvironment = DivParsingEnvironment(logger = { fail(it.message) })
