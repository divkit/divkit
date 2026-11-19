package com.yandex.div.steps

import android.graphics.PointF
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
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Target
import com.yandex.div.view.ViewActions.doubleTapWithRetries
import com.yandex.div.view.ViewActions.longTapWithRetries
import com.yandex.div.view.ViewActions.tapWithRetries
import com.yandex.div.view.actions.SwipeWithinVisibleBounds
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

                    is Step.Tap -> performTap(step.target, step.point)

                    is Step.DoubleTap -> performDoubleTap(step.target, step.point)

                    is Step.LongTap -> performLongTap(step.target, step.point)

                    is Step.Swipe -> performSwipe(step)

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

    private fun performTap(
        target: Target,
        point: PointF?
    ) = step("Tap $target${point.atPointSuffix()}") {
        onView(target).perform(tapWithRetries(point))
    }

    private fun performDoubleTap(
        target: Target,
        point: PointF?
    ) = step("Double tap $target${point.atPointSuffix()}") {
        onView(target).perform(doubleTapWithRetries(point))
    }

    private fun performLongTap(
        target: Target,
        point: PointF?
    ) = step("Long tap $target${point.atPointSuffix()}") {
        onView(target).perform(longTapWithRetries(point))
    }

    private fun PointF?.atPointSuffix() = this?.let { " at (${it.x}, ${it.y})" }.orEmpty()

    private fun performSwipe(swipe: Step.Swipe) = step("Swipe ${swipe.target}") {
        onView(swipe.target).perform(SwipeWithinVisibleBounds(swipe.from, swipe.to, swipe.holdDurationMs))
    }

    private fun waitForStep(wait: Step.Wait) = step("Wait ${wait.delay} ms") {
        Thread.sleep(wait.delay)
    }

    private fun verifyText(
        verification: Step.VerifyText
    ) = step("Verify text '${verification.text}' in ${verification.target}") {
        onView(verification.target).waitUntilVisible(withText(verification.text))
    }

    private fun verifyFocus(
        verification: Step.VerifyFocus
    ) = step("Verify focused=${verification.focused} in ${verification.target}") {
        val focused = if (verification.focused) hasFocus() else doesNotHaveFocus()
        onView(verification.target).waitUntilVisible(focused)
    }

    private fun onView(target: Target) = onView(
        when (target) {
            is Target.DivId -> withTagValue(equalTo(target.id))
            is Target.Text -> withText(target.text)
        }
    )

    private fun verifySnapshot(
        scenario: ActivityScenario<DivScreenshotActivity>,
        verification: Step.VerifySnapshot,
        snapshotIndex: Int,
        casePath: String,
        artifactsRelativePath: String
    ) = step("Verify screenshot step$snapshotIndex") {
        var screenshotView: View? = null
        when (val target = verification.target) {
            is Target.Text -> onView(target).check { view, noViewFoundException ->
                noViewFoundException?.let { throw it }
                screenshotView = view
            }

            is Target.DivId -> scenario.onActivity { activity ->
                screenshotView = activity.divView.findViewWithTag(target.id)
                    ?: throw AssertionError("Failed to find screenshot target with div_id '${target.id}'")
            }

            null -> scenario.onActivity { activity -> screenshotView = activity.divView }
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
