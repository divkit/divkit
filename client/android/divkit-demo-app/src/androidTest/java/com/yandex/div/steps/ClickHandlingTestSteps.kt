package com.yandex.div.steps

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.ViewActions
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.junit.Assert

private const val MAX_GESTURE_HANDLING_TIME_NS = 48_000_000L

internal fun testClicks(f: ClickHandlingTestSteps.() -> Unit) = f(ClickHandlingTestSteps())

@StepsDsl
internal open class ClickHandlingTestSteps : DivTestAssetSteps() {
    init {
        testAsset = "regression_test_data/button_actions.json"
    }

    fun ActivityTestRule<*>.buildContainer(): Unit = step("Build container") {
        buildContainer(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    fun click(text: String): Unit =
        step("Click on view with text='$text'") {
            findView(text).perform(controlledSingleTap())
        }

    fun doubleClick(text: String): Unit =
        step("Double click on view with text='$text'") {
            findView(text).perform(controlledDoubleTap())
        }

    fun longClick(text: String): Unit =
        step("Long click on view with text='$text'") {
            findView(text).perform(longClick())
        }

    private fun findView(text: String) = onView(withText(text))

    fun assert(f: ClickHandlingAssertions.() -> Unit) = f(ClickHandlingAssertions())
}

@StepsDsl
internal class ClickHandlingAssertions {

    fun checkShown(text: String, timeout: Long? = null): Unit =
        step("View with text='$text' is shown") {
            if (timeout == null) {
                onView(withText(text))
            } else {
                ViewActions.waitForView(withText(text), timeout)
            }.check(matches(isDisplayed()))
    }
}

private fun controlledDoubleTap() = object : ViewAction {

    override fun getConstraints() = isDisplayingAtLeast(90)

    override fun getDescription() = "double tap with controlled event timing"

    override fun perform(uiController: UiController, view: View) {
        val firstDownTime = SystemClock.uptimeMillis()
        val tapDuration = ViewConfiguration.getTapTimeout().toLong() / 2
        val intervalBetweenTaps = ViewConfiguration.getDoubleTapTimeout().toLong() / 2
        val firstUpTime = firstDownTime + tapDuration
        val secondDownTime = firstUpTime + intervalBetweenTaps
        val secondUpTime = secondDownTime + tapDuration
        val centerX = view.width / 2f
        val centerY = view.height / 2f

        // Espresso's Tap.DOUBLE waits on the main looper and can oversleep the double-tap window.
        val events = listOf(
            MotionEvent.obtain(firstDownTime, firstDownTime, MotionEvent.ACTION_DOWN, centerX, centerY, 0),
            MotionEvent.obtain(firstDownTime, firstUpTime, MotionEvent.ACTION_UP, centerX, centerY, 0),
            MotionEvent.obtain(secondDownTime, secondDownTime, MotionEvent.ACTION_DOWN, centerX, centerY, 0),
            MotionEvent.obtain(secondDownTime, secondUpTime, MotionEvent.ACTION_UP, centerX, centerY, 0),
        )

        dispatchWithinFrameBudget(view, "double tap", events)
        uiController.loopMainThreadUntilIdle()
    }
}

private fun controlledSingleTap() = object : ViewAction {

    override fun getConstraints() = isDisplayingAtLeast(90)

    override fun getDescription() = "single tap with controlled event timing"

    override fun perform(uiController: UiController, view: View) {
        val downTime = SystemClock.uptimeMillis()
        val upTime = downTime + ViewConfiguration.getTapTimeout().toLong() / 2
        val centerX = view.width / 2f
        val centerY = view.height / 2f
        val events = listOf(
            MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, centerX, centerY, 0),
            MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_UP, centerX, centerY, 0),
        )

        dispatchWithinFrameBudget(view, "single tap", events)
        uiController.loopMainThreadUntilIdle()
    }
}

private fun dispatchWithinFrameBudget(
    view: View,
    gestureName: String,
    events: List<MotionEvent>,
) {
    val elapsedNs = try {
        val startedAtNs = SystemClock.elapsedRealtimeNanos()
        events.forEach { event ->
            view.dispatchTouchEvent(event)
        }
        SystemClock.elapsedRealtimeNanos() - startedAtNs
    } finally {
        events.forEach { event ->
            event.recycle()
        }
    }

    Assert.assertTrue(
        "$gestureName took $elapsedNs ns; expected < $MAX_GESTURE_HANDLING_TIME_NS ns",
        elapsedNs < MAX_GESTURE_HANDLING_TIME_NS,
    )
}
