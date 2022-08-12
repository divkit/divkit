@file:Suppress("HasPlatformType")

package com.yandex.div.view

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import com.yandex.div.view.ViewMatchers.isDisplayedForClicking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import java.util.concurrent.TimeoutException

object ViewActions {

    /**
     * Performs click on view with very light constraints. Use only if there is no other way
     */
    fun callOnClickAction(): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = allOf(isClickable(), isDisplayed())

            override fun getDescription() = "Call view onClick directly"

            override fun perform(uiController: UiController, view: View) {
                view.callOnClick()
            }
        }
    }

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

    /**
     * Performs an action on a child view.
     */
    fun actionOnChild(@IdRes id: Int, action: ViewAction): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = allOf(isDisplayedForClicking(), isClickable())

            override fun getDescription() = "Performs '$action' on a child view with id='$id'"

            override fun perform(uiController: UiController, view: View) {
                val child = view.findViewById<View>(id)
                    ?: throw PerformException.Builder()
                        .withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(RuntimeException("View with id='$id' not found in '$view'"))
                        .build()
                action.perform(uiController, child)
            }
        }
    }

    fun clickAtRight(): ViewAction = ViewActions.actionWithAssertions(
        GeneralClickAction(
            Tap.SINGLE,
            GeneralLocation.CENTER_RIGHT,
            Press.FINGER,
            InputDevice.SOURCE_UNKNOWN,
            MotionEvent.BUTTON_PRIMARY
        )
    )

    const val WAITING_TIMEOUT = 15_000L
    const val MAIN_THREAD_LOOP_TIME_MS = 50L

    /**
     * Wait for view is completely displayed and perform action
     */
    fun waitForCompletelyDisplayedAndPerformAction(
        targetAction: ViewAction? = null,
        timeoutInMillis: Long = WAITING_TIMEOUT,
    ) = waitForViewConditionAndPerformAction<View>(
        description = "Waits view to become completely displayed.",
        timeoutInMillis = timeoutInMillis,
        condition = { view -> isCompletelyDisplayed().matches(view) },
        targetAction = targetAction,
    )

    inline fun <reified ViewType : View> waitForViewConditionAndPerformAction(
        description: String,
        targetAction: ViewAction? = null,
        timeoutInMillis: Long = WAITING_TIMEOUT,
        condition: Matcher<ViewType>,
    ) = waitForViewConditionAndPerformAction(description, targetAction, timeoutInMillis,
        { view: ViewType -> condition.matches(view) }
    )

    inline fun <reified ViewType : View> waitForViewConditionAndPerformAction(
        description: String,
        targetAction: ViewAction? = null,
        timeoutInMillis: Long = WAITING_TIMEOUT,
        crossinline condition: (view: ViewType) -> Boolean,
    ): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = isAssignableFrom(ViewType::class.java)

            override fun getDescription() = description

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + timeoutInMillis

                do {
                    if (condition(view as ViewType)) {
                        targetAction?.perform(uiController, view)
                        return
                    }

                    uiController.loopMainThreadForAtLeast(MAIN_THREAD_LOOP_TIME_MS)
                } while (System.currentTimeMillis() < endTime)

                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
            }
        }
    }
}
