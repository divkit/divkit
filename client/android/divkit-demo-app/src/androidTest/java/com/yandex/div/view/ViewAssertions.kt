package com.yandex.div.view

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.yandex.div.view.ViewMatchers.positionMatch
import com.yandex.test.util.Report.step

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
}
