package com.yandex.div.steps

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KLog
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.view.ViewActions.doubleTapWithRetries
import com.yandex.div.view.ViewActions.longTapWithRetries
import com.yandex.div.view.ViewActions.tapWithRetries
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.StepsDsl
import io.qameta.allure.kotlin.Allure.step
import org.hamcrest.Matchers.equalTo
import org.json.JSONObject

private const val TAG = "InteractiveTestStepsPerformer"

internal fun interactiveScreenshot(f: InteractiveScreenshotSteps.() -> Unit) = f(InteractiveScreenshotSteps())

@StepsDsl
internal class InteractiveScreenshotSteps {

    fun runSteps(
        scenario: ActivityScenario<DivScreenshotActivity>,
        casePath: String,
        testCase: JSONObject,
        artifactsRelativePath: String
    ) = step("Run interactive screenshot steps") {
        val testData = InteractiveScreenshotTestData.parse(testCase)
        var snapshotIndex = 0
        testData.steps.forEachIndexed { index, step ->
            when (step) {
                is Step.Action -> step("Step $index: Action ${step.action.writeToJSON()}") {
                    scenario.onActivity { handleAction(it.divView, step.action) }
                }

                is Step.Tap -> step("Tap div '${step.id}'") {
                    tap(step.id, tapWithRetries())
                }

                is Step.DoubleTap -> step("Double tap div '${step.id}'") {
                    tap(step.id, doubleTapWithRetries())
                }

                is Step.LongTap -> step("Long tap div '${step.id}'") {
                    tap(step.id, longTapWithRetries())
                }

                is Step.Wait -> step("Step $index: Wait ${step.delay} ms") {
                    Thread.sleep(step.delay)
                }

                is Step.VerifyText -> step("Step $index: Verify text '${step.text}' in div '${step.id}'") {
                    verifyText(step)
                }

                is Step.VerifySnapshot -> step("Step $index: Verify screenshot step$snapshotIndex") {
                    var divView: View? = null
                    scenario.onActivity { divView = it.divView }
                    val view = divView ?: return@step

                    waitForLoadings(view)
                    Espresso.onIdle()
                    Thread.sleep(1000)

                    captureScreenshots(
                        view,
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

    private fun tap(id: String, action: ViewAction) = onView(withTagValue(equalTo(id))).perform(action)

    private fun verifyText(verification: Step.VerifyText) {
        onView(withTagValue(equalTo(verification.id)))
            .check(matches(withText(verification.text)))
            .checkIsDisplayed()
    }
}
