package com.yandex.div.view

import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.PositionAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import com.yandex.div.view.ViewActions.callOnClickAction
import com.yandex.div.view.ViewActions.clickOnPartlyVisibleView
import com.yandex.div.view.ViewActions.waitForCompletelyDisplayedAndPerformAction
import com.yandex.div.view.ViewAssertions.doesExistAndVisible
import com.yandex.div.view.ViewAssertions.doesNotExistOrGone
import com.yandex.div.view.actions.ExtendedScrollTo
import junit.framework.AssertionFailedError
import org.hamcrest.Matcher

fun ViewInteraction.checkIsDisplayed() = check(matches(ViewMatchers.isDisplayed()))
fun ViewInteraction.checkIsChecked() = check(matches(ViewMatchers.isChecked()))
fun ViewInteraction.checkIsNotChecked() = check(matches(ViewMatchers.isNotChecked()))

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

fun ViewInteraction.whenChecked(): ViewInteraction? {
    return try {
        checkIsChecked()
    } catch (e: AssertionFailedError) {
        null
    }
}

fun ViewInteraction.whenMatches(matcher: Matcher<View>): ViewInteraction? {
    return try {
        check(matches(matcher))
    } catch (e: AssertionFailedError) {
        null
    }
}

fun ViewInteraction.isMatching(matcher: Matcher<View>): Boolean {
    return whenMatches(matcher) != null
}

fun ViewInteraction.whenNotChecked(): ViewInteraction? {
    return try {
        checkIsNotChecked()
    } catch (e: AssertionFailedError) {
        null
    }
}

fun ViewInteraction.checkNotExist() = check(doesNotExist())

fun ViewInteraction.checkIsAbove(matcher: Matcher<View>) = check(
    PositionAssertions.isCompletelyAbove(
        matcher
    )
)

fun ViewInteraction.click() = perform(clickOnPartlyVisibleView())

fun ViewInteraction.tap() = perform(ViewActions.click())

fun ViewInteraction.scrollTo() = perform(ExtendedScrollTo())

fun ViewInteraction.callOnClick() = perform(callOnClickAction())

fun ViewInteraction.clearText() = perform(ViewActions.clearText())

fun ViewInteraction.deleteText() = perform(TextViewActions.clearText())

fun ViewInteraction.inputText(text: String) = perform(typeText(text))

fun ViewInteraction.replaceText(text: String) = perform(ViewActions.replaceText(text))

fun ViewInteraction.clickRecyclerViewItem(index: Int) =
    perform(actionOnItemAtPosition<ViewHolder>(index, ViewActions.click()))

fun ViewInteraction.swipeRight() = perform(ViewActions.swipeRight())
fun ViewInteraction.swipeLeft() = perform(ViewActions.swipeLeft())

fun ViewInteraction.waitForCompletelyDisplayedAndCallOnClick() =
    perform(waitForCompletelyDisplayedAndPerformAction(callOnClickAction()))

fun ViewInteraction.waitForCompletelyDisplayedAndClick() =
    perform(waitForCompletelyDisplayedAndPerformAction(ViewActions.click()))

fun ViewInteraction.waitForCompletelyDisplayedAndLongClick() =
    perform(waitForCompletelyDisplayedAndPerformAction(ViewActions.longClick()))

fun ViewInteraction.pressSpace() = perform(ViewActions.pressKey(KeyEvent.KEYCODE_SPACE))

fun ViewInteraction.pressEnter() = perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))

fun ViewInteraction.pressBackButton() = perform(ViewActions.pressBack())

fun ViewInteraction.swipeLeftCenterToRightCenter() = perform(
    waitForCompletelyDisplayedAndPerformAction(
        GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.CENTER_LEFT,
            GeneralLocation.CENTER_RIGHT, Press.FINGER
        )
    )
)

fun ViewInteraction.swipeRightCenterToLeftCenter() = perform(
    waitForCompletelyDisplayedAndPerformAction(
        GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.CENTER_RIGHT,
            GeneralLocation.CENTER_LEFT, Press.FINGER
        )
    )
)

fun ViewInteraction.swipeTopCenterToBottomCenter() = perform(
    waitForCompletelyDisplayedAndPerformAction(
        GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.TOP_CENTER,
            GeneralLocation.BOTTOM_CENTER, Press.FINGER
        )
    )
)

fun ViewInteraction.swipeFromCenterToBottomCenter(swipeSpeed: Swipe = Swipe.SLOW) = perform(
    waitForCompletelyDisplayedAndPerformAction(
        GeneralSwipeAction(
            swipeSpeed,
            GeneralLocation.CENTER,
            GeneralLocation.BOTTOM_CENTER, Press.FINGER
        )
    )
)

fun ViewInteraction.swipeFromCenterToTopCenter(swipeSpeed: Swipe = Swipe.SLOW) = perform(
    waitForCompletelyDisplayedAndPerformAction(
        GeneralSwipeAction(
            swipeSpeed,
            GeneralLocation.CENTER,
            GeneralLocation.TOP_CENTER, Press.FINGER
        )
    )
)

fun ViewInteraction.waitForCompletelyDisplayed(
    timeoutInMillis: Long? = null
): ViewInteraction {
    return if (timeoutInMillis == null)
        perform(waitForCompletelyDisplayedAndPerformAction())
    else perform(
        waitForCompletelyDisplayedAndPerformAction(
            timeoutInMillis = timeoutInMillis
        )
    )
}

fun ViewInteraction.waitMainThreadBecomeIdle(): ViewInteraction {
    return perform(object : ViewAction {

        override fun getDescription(): String =
            "Wait while main thread become idle on displayed view"

        override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayed()

        override fun perform(uiController: UiController, view: View) =
            uiController.loopMainThreadUntilIdle()
    })
}

fun ViewInteraction.checkExistsAndVisible() = check(doesExistAndVisible())

fun ViewInteraction.checkExistsAndCompletelyVisible() = check(matches(isCompletelyDisplayed()))

fun ViewInteraction.checkNotExistsOrGone() = check(doesNotExistOrGone())


fun <T : View> ViewInteraction.configure(block: (T) -> Unit): ViewInteraction {
    return perform(ViewActions.actionWithAssertions(ConfigureViewAction(block)))
}

private class ConfigureViewAction<T : View>(
    private val block: (T) -> Unit
) : ViewAction {

    override fun getDescription() = "ConfigureViewAction"

    override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayed()

    @Suppress("UNCHECKED_CAST")
    override fun perform(uiController: UiController, view: View) {
        val receiver = view as? T ?: return
        block(receiver)
    }
}
