package com.yandex.div

import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DivComposeScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivComposeScreenshotActivity::class.java,
        DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @Rule
    @JvmField
    val rule = screenshotRule(case, activityRule, case.relativePath)

    @Screenshot(viewId = R.id.morda_screenshot_div)
    @Test
    fun divScreenshot() = Unit

    companion object {

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return listOf(
                "snapshot_test_data/div-text/all_attributes.json"
            ).withEscapedParameter()
        }
    }
}
