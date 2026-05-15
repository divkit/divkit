package com.yandex.div

import android.os.Build
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yandex.div.rule.baseRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.screenshot.Screenshot
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.Report.step
import org.junit.Assume.assumeTrue
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

    @Test
    @Screenshot(viewId = R.id.screenshot_view)
    fun test() {
        assumeTrue(
            "Skipping test on API 24",
            Build.VERSION.SDK_INT > Build.VERSION_CODES.N
        )

        val json = DivAssetReader(activity).read(fileName)
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
                view = activity.findViewById(R.id.screenshot_view),
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
                "interactive_snapshot_test_data/div-input/fixed_length_input_mask.json",
                "interactive_snapshot_test_data/div-text/text-properties.json",
            ).withEscapedParameter()
        }
    }
}
