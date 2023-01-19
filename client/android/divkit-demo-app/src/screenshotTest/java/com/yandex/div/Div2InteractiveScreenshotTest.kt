package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.screenshotRule
import com.yandex.div.steps.interactiveScreenshot
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class Div2InteractiveScreenshotTest(private val case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivScreenshotActivity::class.java,
        DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to case,
    )

    @Rule
    @JvmField
    val rule = screenshotRule(skipScreenshotCapture = true, casePath = case) { activityRule }

    @Screenshot(viewId = R.id.morda_screenshot_div)
    @Test
    fun divScreenshot() {
        interactiveScreenshot {
            runSteps(activityRule.activity, case, artifactsDir(case))
        }
    }

    companion object {
        private const val TEST_CASES_PATH = "interactive_snapshot_test_data"
        private const val CASE_EXTENSION = ".json"

        private val context: Context = ApplicationProvider.getApplicationContext()

        /**
         * Transforms "interactive_snapshot_test_data/div-text/smoke.json" into
         * "com.yandex.div.Div2InteractiveScreenshotTest/div-text/smoke"
         */
        fun artifactsDir(case: String) = Div2InteractiveScreenshotTest::class.qualifiedName +
                case.removePrefix(TEST_CASES_PATH).removeSuffix(CASE_EXTENSION)

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val filter = { filename: String ->
                filename.endsWith(CASE_EXTENSION) && !filename.contains("templates")
            }

            val testCases = AssetEnumerator(context).enumerate(TEST_CASES_PATH, filter)
            if (testCases.isEmpty()) {
                Assert.fail("No testcases found at '$TEST_CASES_PATH'")
            }
            return testCases.withEscapedParameter()
        }
    }
}
