package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.rule.SetDataRule
import com.yandex.div.rule.baseRule
import com.yandex.div.steps.interactiveScreenshot
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
import com.yandex.test.util.chain
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class Div2InteractiveScreenshotTest(private val casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)
    private val activityRule = ActivityParamsTestRule(DivScreenshotActivity::class.java)

    @Rule
    @JvmField
    val rule = baseRule(casePath, TEST_CASES_PATH, testCase, activityRule)
        .chain(SetDataRule(testCase, activityRule))

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() {
        interactiveScreenshot {
            runSteps(activityRule.activity, casePath, testCase, artifactsDir(casePath))
        }
    }

    companion object {
        private const val TEST_CASES_PATH = "interactive_snapshot_test_data"

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)

        /**
         * Transforms "interactive_snapshot_test_data/div-text/smoke.json" into
         * "com.yandex.div.Div2InteractiveScreenshotTest/div-text/smoke"
         */
        fun artifactsDir(case: String) = Div2InteractiveScreenshotTest::class.qualifiedName +
                case.removePrefix(TEST_CASES_PATH).substringBeforeLast(File.separator)

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val enumerator = AssetEnumerator()
            return enumerator.enumerate(TEST_CASES_PATH)
                .let(enumerator::requireSelectedCase)
                .withEscapedParameter()
        }
    }
}
