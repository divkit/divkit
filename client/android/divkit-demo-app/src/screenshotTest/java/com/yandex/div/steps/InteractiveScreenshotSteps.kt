package com.yandex.div.steps

import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
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
                    delay = step.optLong("delay"),
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
        steps.forEachIndexed { i, step ->
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                handleStepActions(activity.divView, step)
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

    private fun waitForConditions(activity: DivScreenshotActivity, delay: Long) {
        waitForImages { activity }
        Espresso.onIdle()
        Thread.sleep(delay)
    }
}

private class TestStep(
    val actions: List<DivAction>,
    val expectedScreenshot: String,
    val delay: Long,
)
