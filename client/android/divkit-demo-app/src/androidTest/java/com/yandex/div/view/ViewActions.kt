@file:Suppress("HasPlatformType")

package com.yandex.div.view

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.Tapper
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import com.yandex.div.view.ViewMatchers.isDisplayedForClicking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import java.lang.Thread.sleep

object ViewActions {

    private const val TAP_RETRIES = 3

    fun tapWithRetries(): ViewAction = tapWithRetries(Tap.SINGLE, "tap")

    fun doubleTapWithRetries(): ViewAction = tapWithRetries(Tap.DOUBLE, "double tap")

    fun longTapWithRetries(): ViewAction = tapWithRetries(Tap.LONG, "long tap")

    private fun tapWithRetries(tap: Tap, gesture: String): ViewAction = actionWithAssertions(object : ViewAction {

        override fun getConstraints(): Matcher<View> = isDisplayingAtLeast(90)

        override fun getDescription() = "$gesture with up to $TAP_RETRIES retries"

        override fun perform(uiController: UiController, view: View) {
            var status = Tapper.Status.FAILURE
            repeat(TAP_RETRIES + 1) {
                status = tap.sendTap(
                    uiController,
                    GeneralLocation.VISIBLE_CENTER.calculateCoordinates(view),
                    Press.FINGER.describePrecision(),
                    InputDevice.SOURCE_UNKNOWN,
                    MotionEvent.BUTTON_PRIMARY
                )
                uiController.loopMainThreadForAtLeast(ViewConfiguration.getDoubleTapTimeout().toLong())
                if (status == Tapper.Status.SUCCESS) return
            }
            throw PerformException.Builder()
                .withActionDescription(description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(AssertionError("$gesture failed after ${TAP_RETRIES + 1} attempts: $status"))
                .build()
        }
    })

    /**
     * Performs click ignoring 90% visible area constraint
     */
    fun clickOnPartlyVisibleView(): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = allOf(isDisplayedForClicking(), isClickable())

            override fun getDescription() = "click ignoring 90% constraint"

            override fun perform(uiController: UiController, view: View) {
                view.performClick()
            }
        }
    }

    const val WAITING_TIMEOUT = 15_000L
    const val WAITING_TIMEOUT_PER_TRY = 100L

    fun waitForView(
        viewMatcher: Matcher<View>,
        waitingTimeout: Long = WAITING_TIMEOUT,
        waitTimeoutPerTry: Long = WAITING_TIMEOUT_PER_TRY
    ): ViewInteraction {
        val maxTries = waitingTimeout / waitTimeoutPerTry
        var lastException: Exception? = null
        for (i in 0..maxTries) {
            try {
                onView(isRoot()).perform(searchFor(viewMatcher))
                return onView(viewMatcher)
            } catch (e: Exception) {
                lastException = e
                sleep(waitTimeoutPerTry)
            }
        }
        throw lastException ?: Exception("Error finding a view matching $viewMatcher")
    }

    private fun searchFor(matcher: Matcher<View>) = object : ViewAction {

        override fun getConstraints() = isRoot()

        override fun getDescription() = "Searching for view $matcher in the root view"

        override fun perform(uiController: UiController, view: View) {
            val childViews: Iterable<View> = TreeIterables.breadthFirstViewTraversal(view)
            childViews.forEach {
                if (matcher.matches(it)) return
            }
            throw NoMatchingViewException.Builder()
                .withRootView(view)
                .withViewMatcher(matcher)
                .build()
        }
    }
}
