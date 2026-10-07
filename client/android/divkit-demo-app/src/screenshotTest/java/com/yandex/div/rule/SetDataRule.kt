package com.yandex.div.rule

import com.yandex.divkit.demo.screenshot.DivDataScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.util.runOnMainSync
import org.json.JSONObject
import org.junit.rules.ExternalResource

class SetDataRule(
    private val testCase: JSONObject,
    private val activityRule: ActivityParamsTestRule<out DivDataScreenshotActivity>,
) : ExternalResource() {
    override fun before() = runOnMainSync {
        activityRule.activity.prepare()
        activityRule.activity.setDivData(testCase)
    }
}
