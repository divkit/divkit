package com.yandex.div.rule

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.divkit.demo.screenshot.DivDataScreenshotActivity
import org.json.JSONObject
import org.junit.rules.ExternalResource

class SetDataRule(
    private val testCase: JSONObject,
    private val activityRule: ActivityScenarioRule<out DivDataScreenshotActivity>,
) : ExternalResource() {

    override fun before() {
        activityRule.scenario.onActivity {
            it.prepare()
            it.setDivData(testCase)
        }
    }

    override fun after() {
        activityRule.scenario.onActivity { it.cleanup() }
    }
}
