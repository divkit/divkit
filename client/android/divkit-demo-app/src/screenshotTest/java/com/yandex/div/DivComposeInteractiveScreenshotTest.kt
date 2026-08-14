package com.yandex.div

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.yandex.div.rule.baseRule
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.divkit.regression.utils.AssetReader
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.screenshot.Screenshot
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.Report.step
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class DivComposeInteractiveScreenshotTest(
    private val fileName: String,
    escapedFileName: String
) {

    private val composeRule = createAndroidComposeRule<DivComposeScreenshotActivity>()

    private val activity: DivComposeScreenshotActivity
        get() = composeRule.activity

    private val artifactsPath: String
        get() {
            return DivComposeInteractiveScreenshotTest::class.qualifiedName +
                    fileName.removePrefix("interactive_snapshot_test_data")
                        .substringBeforeLast(File.separator)
        }

    @get:Rule
    val rule = baseRule(fileName, composeRule)

    @Screenshot(viewTag = DivComposeScreenshotActivity.SCREENSHOT_VIEW_TAG)
    @Test
    fun test() {
        val json = AssetReader(activity).readJson(fileName)
        val testData = InteractiveScreenshotTestData.parse(json)

        composeRule.runOnUiThread {
            activity.setDivData(testData.divJson)
        }

        testData.steps.forEachIndexed { id, step ->
            step(id = id, data = step)
        }
    }

    private fun step(id: Int, data: InteractiveScreenshotTestData.Step) {
        step("Step $id") {
            activity.performActions(data.actions)

            waitForIdle()

            captureScreenshots(
                view = activity.window.decorView
                    .findViewWithTag(DivComposeScreenshotActivity.SCREENSHOT_VIEW_TAG),
                artifactsRelativePath = artifactsPath,
                casePath = fileName,
                stepId = id,
                expectedScreenshot = data.expectedScreenshot
            )
        }
    }

    private fun waitForIdle() {
        waitForIdlingResource(activity.imageLoadingTracker)
        composeRule.waitForIdle()
    }

    companion object {

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return listOf(
                "interactive_snapshot_test_data/div-action/base.json",
                "interactive_snapshot_test_data/div-action/set-variable.json",
                "interactive_snapshot_test_data/div-container/base-properties.json",
                "interactive_snapshot_test_data/div-container/visibility.json",
                "interactive_snapshot_test_data/div-extension/is-enabled.json",
                "interactive_snapshot_test_data/div-input/fixed_length_input_mask.json",
                "interactive_snapshot_test_data/div-input/phone_input_mask.json",
                "interactive_snapshot_test_data/div-input/currency_input_mask.json",
                "interactive_snapshot_test_data/div-text/text-properties.json",
            ).withEscapedParameter()
        }
    }
}
