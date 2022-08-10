package com.yandex.divkit.demo.screenshot

import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.util.KLog
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.forEach
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.utils.coroutineScope
import com.yandex.test.screenshot.ReferenceFileWriter
import com.yandex.test.screenshot.ScreenshotCaptor
import com.yandex.test.screenshot.ScreenshotTestState
import com.yandex.test.screenshot.TestCaseReferencesFileWriter
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "InteractiveTestStepsPerformer"

class InteractiveTestStepsPerformer(
    private val artifactsRelativePath: String,
    private val testCaseJson: JSONObject,
    private val casePath: String,
    divContext: Div2Context,
    activity: AppCompatActivity,
) {
    private val artifactsCaptor = ScreenshotCaptor()
    private val divJson = testCaseJson.getJSONObject("div_data")
    private val cardJson = divJson.optJSONObject("card") ?: divJson
    private val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
        divJson.optJSONObject("templates")?.let {
            parseTemplates(it)
        }
    }

    private val activityScope: CoroutineScope = activity.coroutineScope
    private val workerScope = activityScope + Dispatchers.IO
    private val referencesFile = ReferenceFileWriter(fileDir = artifactsCaptor.screenshotRootDir)
    private val testCaseReferencesFile = TestCaseReferencesFileWriter(
        fileDir = artifactsCaptor.screenshotRootDir
    )

    val view = Div2View(divContext).apply {
        setData(DivData(environment, cardJson), DivDataTag("div2"))
    }

    init {
        ScreenshotTestState.notifyTestStarted(artifactsRelativePath)
        workerScope.launch {
            runSteps(parseSteps(testCaseJson.getJSONArray("steps")))
        }
    }

    private suspend fun runSteps(steps: List<TestStep>) {
        steps.forEachIndexed { i, step ->
            withContext(Dispatchers.Main) {
                step.actions.forEach {
                    val actionHandled = view.handleActionWithResult(it)
                    if (!actionHandled) {
                        KLog.e(TAG) {
                            "Failed to handle action: ${it.writeToJSON()}"
                        }
                    }
                }

                waitIdle()
                val screenshots = captureScreenshots(i, step.expectedScreenshot)
                testCaseReferencesFile.append(casePath, screenshots)
            }
        }

        artifactsCaptor.saveDeviceProperties(view.context)
        ScreenshotTestState.notifyTestCompleted(artifactsRelativePath)
    }

    private suspend fun waitIdle() = suspendCancellableCoroutine<Unit> { continuation ->
        activityScope.launch {
            Looper.myQueue().addIdleHandler {
                continuation.resumeWith(Result.success(Unit))
                false // removes task from queue
            }
        }
    }

    /**
     * @return list of paths to screenshot files
     */
    private fun captureScreenshots(stepId: Int, expectedScreenshot: String): List<String> {
        val result = mutableListOf<String>()
        val actualScreenshot = "step${stepId}.png"

        val instrumentation = InstrumentationRegistry.getInstrumentation()
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

    private class ScreenshotCategory(
        val name: String,
        val captureScreenshot: (File) -> Unit
    )

    private fun parseSteps(stepsJson: JSONArray): List<TestStep> {
        val results = mutableListOf<TestStep>()
        stepsJson.forEach { _, step: JSONObject ->
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
}

private class TestStep(
    val actions: List<DivAction>,
    val expectedScreenshot: String,
)
