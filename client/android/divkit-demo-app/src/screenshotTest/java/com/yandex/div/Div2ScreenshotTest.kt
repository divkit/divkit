package com.yandex.div

import com.yandex.div.rule.screenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class Div2ScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivScreenshotActivity::class.java,
        DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @Rule
    @JvmField
    val rule = screenshotRule(case, activityRule, case.relativePath)

    @Screenshot(viewId = R.id.screenshot_view)
    @Test
    fun divScreenshot() = Unit

    companion object {

        const val TEST_CASES_PATH = "snapshot_test_data"

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return AssetEnumerator().enumerate(TEST_CASES_PATH).withEscapedParameter()
        }

        val String.relativePath: String
            get() {
                return substringAfter("$TEST_CASES_PATH${File.separator}")
                    .substringBeforeLast(File.separator)
            }
    }
}
