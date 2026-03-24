package com.yandex.div.view

import android.view.View
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Overrides espresso's default requirement for the view to be displayed at 90%
 */
object ViewMatchers {

    fun isDisplayedForClicking(): Matcher<View> = isDisplayingAtLeast(50)

    fun positionMatch(shouldMatch: Boolean, lastPosition: FloatArray) : Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Check last view position")
                if (!shouldMatch) description.appendText(" does not")
                description.appendText(" matches current")
            }

            override fun matchesSafely(item: View): Boolean {
                val equals = GeneralLocation.CENTER.calculateCoordinates(item).contentEquals(lastPosition)

                return shouldMatch == equals
            }
        }
    }
}
