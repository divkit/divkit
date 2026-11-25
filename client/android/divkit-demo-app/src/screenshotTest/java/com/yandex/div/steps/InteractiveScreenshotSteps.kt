package com.yandex.div.steps

import android.graphics.PointF
import android.os.SystemClock
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.doesNotHaveFocus
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.util.TreeIterables
import com.yandex.div.internal.KLog
import com.yandex.div.rule.UiReadyChecker
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Target
import com.yandex.div.test.crossplatform.SightActionRecorder
import com.yandex.div.view.ViewActions.doubleTapWithRetries
import com.yandex.div.view.ViewActions.longTapWithRetries
import com.yandex.div.view.ViewActions.tapWithRetries
import com.yandex.div.view.actions.EditTextThroughInputConnection
import com.yandex.div.view.actions.SwipeWithinVisibleBounds
import com.yandex.div.view.actions.isScrollDestinationVisible
import com.yandex.div.view.actions.scrollToSwipe
import com.yandex.div.view.waitUntilVisible
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.screenshot.captureScreenshots
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.activeRoots
import com.yandex.test.util.performOnMain
import io.qameta.allure.kotlin.Allure.step
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.sameInstance
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

                    is Step.EditText -> performEditText(step)

                    is Step.Wait -> waitForStep(step)

                    is Step.ScrollTo -> performScrollTo(scenario, step, uiReadyChecker)

                    is Step.VerifyText -> verifyText(step)

                    is Step.VerifyFocus -> verifyFocus(step)

                    is Step.VerifyLog -> verifyLog(scenario, step)

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

    private fun performEditText(
        edit: Step.EditText
    ) = step("Edit text ${edit.mode} '${edit.text}' in ${edit.target}") {
        val replace = edit.mode == Step.EditText.Mode.REPLACE
        onView(edit.target).perform(EditTextThroughInputConnection(replace, edit.inputs))
    }

    private fun waitForStep(wait: Step.Wait) = step("Wait ${wait.delay} ms") {
        Thread.sleep(wait.delay)
    }

    private fun performScrollTo(
        scenario: ActivityScenario<DivScreenshotActivity>,
        scroll: Step.ScrollTo,
        uiReadyChecker: UiReadyChecker,
    ) = step("Scroll ${scroll.container} ${scroll.direction} to ${scroll.destination}") {
        for (swipes in 0..Step.ScrollTo.MAX_SWIPES) {
            if (swipes > 0) waitForUiReady(scenario, uiReadyChecker)
            var visible = false
            onView(scroll.container).check { container, noViewFoundException ->
                noViewFoundException?.let { throw it }
                visible = container.isScrollDestinationVisible(scroll.destination)
            }
            if (visible) return@step
            if (swipes < Step.ScrollTo.MAX_SWIPES) {
                onView(scroll.container).perform(scrollToSwipe(scroll))
            }
        }
        throw AssertionError(
            "Destination did not become visible after ${Step.ScrollTo.MAX_SWIPES} swipes: " +
                "${scroll.destination}"
        )
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

    private fun verifyLog(
        scenario: ActivityScenario<DivScreenshotActivity>,
        verification: Step.VerifyLog
    ) = step(
        "Verify log ${verification.source} '${verification.id}' " +
            (verification.count?.let { "count=$it" } ?: "at least once")
    ) {
        // Actions are recorded on the main thread; delayed ones are awaited like verify_text.
        val deadline = SystemClock.uptimeMillis() + SightActionRecorder.TIMEOUT_MS
        while (true) {
            var settled = false
            scenario.onActivity { settled = it.sightActionRecorder.isSettled(verification) }
            val remainingMillis = deadline - SystemClock.uptimeMillis()
            if (settled || remainingMillis <= 0) break
            Thread.sleep(minOf(SightActionRecorder.POLL_INTERVAL_MS, remainingMillis))
        }
        scenario.onActivity { it.sightActionRecorder.verify(verification) }
    }

    private fun onView(target: Target): ViewInteraction {
        val matcher = when (target) {
            is Target.DivId -> withTagValue(equalTo(target.id))
            is Target.Text -> withText(target.text)
        }
        if (popupRoots().isEmpty()) return onView(matcher)
        // Espresso picks only the topmost focusable window, so the window is chosen by the target.
        val roots = performOnMain {
            activeRoots.filter { root ->
                TreeIterables.breadthFirstViewTraversal(root.decorView).any(matcher::matches)
            }
        }
        if (roots.size > 1) throw ambiguousTarget(target, roots.size)
        val root = roots.singleOrNull() ?: return onView(matcher)
        return onView(matcher).inRoot(withDecorView(sameInstance(root.decorView)))
    }

    private fun popupRoots(): List<Root> = activeRoots.filter(isPlatformPopup()::matches)

    private fun ambiguousTarget(target: Target, windows: Int) =
        AssertionError("$target is found in $windows windows")

    private fun verifySnapshot(
        scenario: ActivityScenario<DivScreenshotActivity>,
        verification: Step.VerifySnapshot,
        snapshotIndex: Int,
        casePath: String,
        artifactsRelativePath: String
    ) = step("Verify screenshot step$snapshotIndex") {
        val popups = popupRoots()
        var screenshotView: View? = null
        when (val target = verification.target) {
            is Target.Text -> onView(target).check { view, noViewFoundException ->
                noViewFoundException?.let { throw it }
                screenshotView = view
            }

            is Target.DivId -> scenario.onActivity { activity ->
                val views = (listOf(activity.divView) + popups.map(Root::getDecorView))
                    .mapNotNull { it.findViewWithTag<View>(target.id) }
                if (views.size > 1) throw ambiguousTarget(target, views.size)
                screenshotView = views.singleOrNull()
                    ?: throw AssertionError("Failed to find screenshot target with div_id '${target.id}'")
            }

            null -> scenario.onActivity { activity ->
                screenshotView = if (popups.isEmpty()) {
                    activity.divView
                } else {
                    activity.findViewById<View>(android.R.id.content)
                }
            }
        }
        val view = screenshotView ?: throw AssertionError("Screenshot view is unavailable")

        captureScreenshots(
            view,
            artifactsRelativePath,
            casePath,
            stepId = snapshotIndex,
            expectedScreenshot = verification.name,
            fromScreen = popups.isNotEmpty(),
        )
    }
}
