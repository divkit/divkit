package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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

    private val caseRelativePath = case
        .substringAfter("$TEST_CASES_PATH${File.separator}")
        .substringAfter("$PRODUCTION_CASES_PATH${File.separator}")
        .substringBeforeLast(File.separator)

    private val caseName = case
        .substringAfterLast(File.separator)
        .substringBeforeLast(CASE_EXTENSION)

    private val activityRule = ActivityParamsTestRule(
        DivScreenshotActivity::class.java,
        DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @Rule
    @JvmField
    val rule = screenshotRule(relativePath = caseRelativePath, name = caseName, casePath = case) {
        activityRule
    }

    @Screenshot(viewId = R.id.morda_screenshot_div)
    @Test
    fun divScreenshot() = Unit

    companion object {

        private const val TEST_CASES_PATH = "snapshot_test_data"
        private const val PRODUCTION_CASES_PATH = "production_data"
        private const val CASE_EXTENSION = ".json"

        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val filter = { filename: String -> filename.endsWith(CASE_EXTENSION) }
            val testCases = AssetEnumerator(context).enumerate(TEST_CASES_PATH, filter)
            val productionCases = AssetEnumerator(context).enumerate(PRODUCTION_CASES_PATH, filter)
            return (testCases + productionCases).withEscapedParameter()
        }
    }
}
