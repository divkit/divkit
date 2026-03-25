@file:Suppress("HasPlatformType")

package com.yandex.div.view

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import com.yandex.div.view.ViewMatchers.isDisplayedForClicking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import java.lang.Thread.sleep

object ViewActions {

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
