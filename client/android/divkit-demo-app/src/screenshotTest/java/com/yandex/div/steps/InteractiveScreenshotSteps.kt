package com.yandex.div.steps

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.doesNotHaveFocus
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.internal.KLog
import com.yandex.div.rule.UiReadyChecker
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.view.ViewActions.doubleTapWithRetries
import com.yandex.div.view.ViewActions.longTapWithRetries
import com.yandex.div.view.ViewActions.tapWithRetries
import com.yandex.div.view.waitUntilVisible
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
        val uiReadyChecker = UiReadyChecker()
        testData.steps.forEachIndexed { index, step ->
            step("Step $index") {
                waitForUiReady(scenario, uiReadyChecker)
                when (step) {
                    is Step.Action -> performAction(scenario, step.action)

                    is Step.Tap -> performTap(step.id)

                    is Step.DoubleTap -> performDoubleTap(step.id)

                    is Step.LongTap -> performLongTap(step.id)

                    is Step.Wait -> waitForStep(step)

                    is Step.VerifyText -> verifyText(step)

                    is Step.VerifyFocus -> verifyFocus(step)

                    is Step.VerifySnapshot ->
                        verifySnapshot(scenario, step, snapshotIndex++, casePath, artifactsRelativePath)
                }
            }
        }
    }

    private fun performAction(
        scenario: ActivityScenario<DivScreenshotActivity>,
        divAction: DivAction
    ) = step("Action ${divAction.writeToJSON()}") {
        var actionHandled = false
        scenario.onActivity { actionHandled = it.divView.handleActionWithResult(divAction) }
        if (!actionHandled) {
            KLog.e(TAG) { "Failed to handle action: ${divAction.writeToJSON()}" }
        }
    }

    private fun performTap(id: String) = step("Tap div '$id'") {
        onView(id).perform(tapWithRetries())
    }

    private fun performDoubleTap(id: String) = step("Double tap div '$id'") {
        onView(id).perform(doubleTapWithRetries())
    }

    private fun performLongTap(id: String) = step("Long tap div '$id'") {
        onView(id).perform(longTapWithRetries())
    }

    private fun waitForStep(wait: Step.Wait) = step("Wait ${wait.delay} ms") {
        Thread.sleep(wait.delay)
    }

    private fun verifyText(
        verification: Step.VerifyText
    ) = step("Verify text '${verification.text}' in div '${verification.id}'") {
        onView(verification.id).waitUntilVisible(withText(verification.text))
    }

    private fun verifyFocus(
        verification: Step.VerifyFocus
    ) = step("Verify focused=${verification.focused} in div '${verification.id}'") {
        val focused = if (verification.focused) hasFocus() else doesNotHaveFocus()
        onView(verification.id).waitUntilVisible(focused)
    }

    private fun onView(id: String) = onView(withTagValue(equalTo(id)))

    private fun verifySnapshot(
        scenario: ActivityScenario<DivScreenshotActivity>,
        verification: Step.VerifySnapshot,
        snapshotIndex: Int,
        casePath: String,
        artifactsRelativePath: String
    ) = step("Verify screenshot step$snapshotIndex") {
        var screenshotView: View? = null
        scenario.onActivity { activity ->
            screenshotView = verification.id?.let {
                activity.divView.findViewWithTag(it)
                    ?: throw AssertionError("Failed to find screenshot target with div_id '$it'")
            } ?: activity.divView
        }
        val view = screenshotView ?: throw AssertionError("Screenshot view is unavailable")

        captureScreenshots(
            view,
            artifactsRelativePath,
            casePath,
            stepId = snapshotIndex,
            expectedScreenshot = verification.name
        )
    }
}
