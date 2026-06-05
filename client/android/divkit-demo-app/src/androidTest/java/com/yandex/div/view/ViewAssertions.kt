package com.yandex.div.view

import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import com.yandex.div.view.ViewMatchers.positionMatch
import com.yandex.test.util.Report.step
import org.hamcrest.Matchers.not

/**
 * Provides assertions for all types of views.
 * */
object ViewAssertions {

    fun hasChangedPosition(view: ViewInteraction, lastPosition: FloatArray): Unit =
        step("Assert view has changed position") {
            checkPosition(shouldMatch = false, view, lastPosition)
        }

    fun hasNotChangedPosition(view: ViewInteraction, lastPosition: FloatArray): Unit =
        step("Assert view has not changed position") {
            checkPosition(shouldMatch = true, view, lastPosition)
        }

    private fun checkPosition(
        shouldMatch: Boolean,
        view: ViewInteraction,
        lastPosition: FloatArray
    ) = view.check(matches(positionMatch(shouldMatch, lastPosition)))

    fun notExistOrDisplayed(): ViewAssertion {
        return ViewAssertion { view, _ ->
            view?.let {
                ViewMatchers.assertThat("View is visible ${HumanReadables.describe(view)}", view, not(isDisplayed()))
            }
        }
    }
}
