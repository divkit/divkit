package com.yandex.div.steps

import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.InteractiveScreenshotTestData
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KLog
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl

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
        val testData = InteractiveScreenshotTestData.parse(activity.getTestCaseJson())
        testData.steps.forEachIndexed { i, step ->
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                handleActions(activity.divView, step.actions)
            }

            waitForConditions(activity, step.delay)

            captureScreenshots(
                activity.divView,
                artifactsRelativePath,
                casePath,
                stepId = i,
                expectedScreenshot = step.expectedScreenshot
            )
        }
    }

    private fun handleActions(view: Div2View, actions: List<DivAction>) {
        actions.forEach {
            val actionHandled = view.handleActionWithResult(it)
            if (!actionHandled) {
                KLog.e(TAG) {
                    "Failed to handle action: ${it.writeToJSON()}"
                }
            }
        }
    }

    private fun waitForConditions(activity: DivScreenshotActivity, delay: Long) {
        waitForImages { activity }
        Espresso.onIdle()
        Thread.sleep(delay)
    }
}
