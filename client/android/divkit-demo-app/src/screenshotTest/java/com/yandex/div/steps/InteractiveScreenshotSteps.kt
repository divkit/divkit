package com.yandex.div.steps

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KLog
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers.equalTo

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
        var snapshotIndex = 0
        testData.steps.forEach { step ->
            when (step) {
                is Step.Action -> {
                    InstrumentationRegistry.getInstrumentation().runOnMainSync {
                        handleAction(activity.divView, step.action)
                    }
                }

                is Step.Wait -> Thread.sleep(step.delay)

                is Step.VerifyText -> verifyText(step)

                is Step.VerifySnapshot -> {
                    waitForLoadings()
                    Espresso.onIdle()
                    Thread.sleep(1000)

                    captureScreenshots(
                        activity.divView,
                        artifactsRelativePath,
                        casePath,
                        stepId = snapshotIndex++,
                        expectedScreenshot = step.name
                    )
                }
            }
        }
    }

    private fun handleAction(view: Div2View, divAction: DivAction) {
        val actionHandled = view.handleActionWithResult(divAction)
        if (!actionHandled) {
            KLog.e(TAG) { "Failed to handle action: ${divAction.writeToJSON()}" }
        }
    }

    private fun verifyText(verification: Step.VerifyText): Unit =
        step("Verify text '${verification.text}' in div '${verification.id}'") {
            onView(withTagValue(equalTo(verification.id)))
                .check(matches(withText(verification.text)))
                .checkIsDisplayed()
        }
}
