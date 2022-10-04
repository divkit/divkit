package com.yandex.div.steps

import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.yandex.div.core.util.KLog
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.forEach
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.ReferenceFileWriter
import com.yandex.test.screenshot.ScreenshotCaptor
import com.yandex.test.screenshot.TestCaseReferencesFileWriter
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "InteractiveTestStepsPerformer"

internal fun interactiveScreenshot(f: InteractiveScreenshotSteps.() -> Unit) =
        f(InteractiveScreenshotSteps())

@StepsDsl
internal class InteractiveScreenshotSteps {

    fun runSteps(
            activity: DivScreenshotActivity,
            casePath: String,
            artifactsRelativePath: String
    ) = step("Run interactive screenshot steps") {
        val steps = parseSteps(activity.getTestCaseJson().optJSONArray("steps"))
        runSteps(activity, artifactsRelativePath, casePath, steps)
    }

    private fun parseSteps(stepsJson: JSONArray?): List<TestStep> {
        val results = mutableListOf<TestStep>()
        val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT)
        stepsJson?.forEach { _, step: JSONObject ->
            val actions = mutableListOf<DivAction>()
            step.optJSONArray("div_actions")?.forEach { _, actionJson: JSONObject ->
                actions.add(DivAction(environment, actionJson))
            }

            results.add(TestStep(
                    actions = actions,
                    expectedScreenshot = step.optString("expected_screenshot"),
            ))
        }

        return results
    }

    private fun runSteps(
            activity: DivScreenshotActivity,
            artifactsRelativePath: String,
            casePath: String,
            steps: List<TestStep>
    ) {
        val artifactsCaptor = ScreenshotCaptor()
        val referencesFile = ReferenceFileWriter(artifactsCaptor.screenshotRootDir)
        val testCaseReferencesFile = TestCaseReferencesFileWriter(artifactsCaptor.screenshotRootDir)
        steps.forEachIndexed { i, step ->
            instrumentation.runOnMainSync {
                handleStepActions(activity.divView, step)
            }
            Espresso.onIdle()

            instrumentation.runOnMainSync {
                val screenshots = captureScreenshots(
                        activity.divView,
                        artifactsCaptor,
                        artifactsRelativePath,
                        referencesFile,
                        i,
                        step.expectedScreenshot
                )
                testCaseReferencesFile.append(casePath, screenshots)
            }
        }

        artifactsCaptor.saveDeviceProperties(activity)
    }

    private fun handleStepActions(view: Div2View, step: TestStep) {
        step.actions.forEach {
            val actionHandled = view.handleActionWithResult(it)
            if (!actionHandled) {
                KLog.e(TAG) {
                    "Failed to handle action: ${it.writeToJSON()}"
                }
            }
        }
    }

    /**
     * @return list of paths to screenshot files
     */
    private fun captureScreenshots(
            view: Div2View,
            artifactsCaptor: ScreenshotCaptor,
            artifactsRelativePath: String,
            referencesFile: ReferenceFileWriter,
            stepId: Int,
            expectedScreenshot: String): List<String> {
        val result = mutableListOf<String>()
        val actualScreenshot = "step${stepId}.png"

        val device = UiDevice.getInstance(instrumentation)

        val categories = listOf(
            ScreenshotCategory("device") { artifactsCaptor.takeDeviceScreenshot(device, it) },
            ScreenshotCategory("viewPixelCopy") { artifactsCaptor.takeViewPixelCopy(view, it) },
            ScreenshotCategory("viewRender") { artifactsCaptor.takeViewRender(view, it) },
        )

        categories.forEach { category ->
            val actualScreenshotRelativePath = "${category.name}/$artifactsRelativePath/$actualScreenshot"

            val actualScreenshotFile = File(artifactsCaptor.screenshotRootDir, actualScreenshotRelativePath)
            val screenshotDir = actualScreenshotFile.parentFile!!

            if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
                throw IllegalAccessException("Cannot prepare screenshots directory: $screenshotDir")
            }

            category.captureScreenshot(actualScreenshotFile)
            result.add(actualScreenshotRelativePath)

            if (expectedScreenshot.isNotEmpty() && expectedScreenshot != actualScreenshot) {
                val expectedScreenshotRelativePath = "${category.name}/$artifactsRelativePath/$expectedScreenshot"
                referencesFile.append(targetFile = actualScreenshotRelativePath,
                    compareWith = expectedScreenshotRelativePath)
            }
        }

        return result
    }

    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
}

private class TestStep(
    val actions: List<DivAction>,
    val expectedScreenshot: String,
)

private class ScreenshotCategory(
        val name: String,
        val captureScreenshot: (File) -> Unit
)
