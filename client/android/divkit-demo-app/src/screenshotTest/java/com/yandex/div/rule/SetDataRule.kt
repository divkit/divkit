package com.yandex.div.rule

import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.divkit.demo.screenshot.DivDataScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import org.json.JSONObject
import org.junit.rules.ExternalResource

class SetDataRule(
    private val testCase: JSONObject,
    private val activityRule: ActivityParamsTestRule<out DivDataScreenshotActivity>,
) : ExternalResource() {

    override fun before() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activityRule.activity.setDivData(testCase)
        }
    }
}
