package com.yandex.div.view

import android.view.View
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.util.HumanReadables
import com.yandex.div.view.ViewMatchers.positionMatch
import com.yandex.test.util.Report.step
import org.hamcrest.MatcherAssert.assertThat

/**
 * Provides assertions for all types of views.
 * */
object ViewAssertions {

    fun doesNotExistOrGone(): ViewAssertion {
        return ViewAssertion { view, _ ->
            view?.let {
                assertThat(
                    "View is visible or exists: ${HumanReadables.describe(view)}",
                    view.visibility == View.GONE || view.visibility == View.INVISIBLE
                )
            }
        }
    }

    fun doesExistAndVisible(): ViewAssertion {
        return ViewAssertion { view, ex ->
            view?.let {
                assertThat(
                    "View is invisible: ${HumanReadables.describe(view)}",
                    view.visibility == View.VISIBLE
                )
            } ?: throw ViewDoesNotExistsException("View does not exist: ${ex.viewMatcherDescription}")
        }
    }

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
}

class ViewDoesNotExistsException(message: String) : RuntimeException(message)
