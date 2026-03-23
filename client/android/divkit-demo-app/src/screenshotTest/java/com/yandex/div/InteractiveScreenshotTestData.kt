package com.yandex.div

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.asList
import com.yandex.div2.DivAction
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
                steps = json.optJSONArray("steps")
                    ?.asList<JSONObject>()
                    .orEmpty()
                    .map { parseStep(it) }
            )
        }

        private fun parseStep(json: JSONObject): Step {
            val actions = json.optJSONArray("div_actions")
                ?.asList<JSONObject>()
                .orEmpty()
                .map { DivAction(parsingEnvironment, it) }
            return Step(
                actions = actions,
                expectedScreenshot = json.optString("expected_screenshot"),
                delay = json.optLong("delay"),
            )
        }
    }
}

private val parsingEnvironment = DivParsingEnvironment(logger = { fail(it.message) })
