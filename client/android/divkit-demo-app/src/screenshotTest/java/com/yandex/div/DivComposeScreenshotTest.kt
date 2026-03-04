package com.yandex.div

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
import java.io.File

@RunWith(Parameterized::class)
class DivComposeScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivComposeScreenshotActivity::class.java,
        DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @Rule
    @JvmField
    val rule = screenshotRule(
        relativePath = case
            .substringAfter("snapshot_test_data${File.separator}")
            .substringBeforeLast(File.separator),
        name = case
            .substringAfterLast(File.separator)
            .substringBeforeLast(".json"),
        casePath = case
    ) {
        activityRule
    }

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
