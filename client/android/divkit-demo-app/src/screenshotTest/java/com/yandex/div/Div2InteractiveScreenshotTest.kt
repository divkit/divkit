package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.div.rule.SetDataRule
import com.yandex.div.rule.baseScreenshotTestRule
import com.yandex.div.rule.classRule
import com.yandex.div.steps.interactiveScreenshot
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.screenshot.Screenshot
import com.yandex.test.util.chain
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class Div2InteractiveScreenshotTest(private val casePath: String, escapedCase: String) {

    private val testCase = assetReader.readJson(casePath)

    @get:Rule
    val rule = baseScreenshotTestRule(casePath, TEST_CASES_PATH, testCase)
        .chain(SetDataRule(testCase, activityRule))

    @Screenshot(viewTag = DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() {
        interactiveScreenshot {
            runSteps(activityRule.scenario, casePath, testCase, artifactsDir(casePath))
        }
    }

    companion object {
        private const val TEST_CASES_PATH = "interactive_snapshot_test_data"

        private val context: Context = ApplicationProvider.getApplicationContext()
        private val assetReader = AssetReader(context)
        private val activityRule = ActivityScenarioRule(DivScreenshotActivity::class.java)

        @JvmField
        @ClassRule
        val classRule = classRule(activityRule)

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
