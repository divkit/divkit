package com.yandex.div.view

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers
import com.yandex.div.view.ViewActions.clickOnPartlyVisibleView
import com.yandex.div.view.actions.ExtendedScrollTo

fun ViewInteraction.checkIsDisplayed() = check(matches(ViewMatchers.isDisplayed()))

fun ViewInteraction.whenDisplayed(): ViewInteraction? {
    return try {
        checkIsDisplayed()
    } catch (e: NoMatchingViewException) {
        null
    }
}

fun ViewInteraction.isDisplayed(): Boolean {
    return whenDisplayed() != null
}

fun ViewInteraction.checkNotExist() = check(doesNotExist())

fun ViewInteraction.click() = perform(clickOnPartlyVisibleView())

fun ViewInteraction.tap() = perform(ViewActions.click())

fun ViewInteraction.scrollTo() = perform(ExtendedScrollTo())

fun ViewInteraction.scrollTo(position: Int) = perform(scrollToPosition<ViewHolder>(position))

fun ViewInteraction.swipeRight() = perform(ViewActions.swipeRight())
fun ViewInteraction.swipeLeft() = perform(ViewActions.swipeLeft())
fun ViewInteraction.swipeUp() = perform(ViewActions.swipeUp())
