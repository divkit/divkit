package com.yandex.div.steps

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isFocused
import androidx.test.espresso.matcher.ViewMatchers.isNotFocused
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.view.ViewActions
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matcher
import ru.tinkoff.allure.step

internal fun divFocusTypedActions(f: DivFocusTypedActionsSteps.() -> Unit) = f(DivFocusTypedActionsSteps())

@StepsDsl
internal class DivFocusTypedActionsSteps {

    fun triggerFocusInputAction(): Unit = step("Click on button 'Focus input'") {
        clickOnView(focusInputMatcher)
    }

    fun waitForClearFocusActionTriggered(): Unit = step("Wait for until timer clean focus") {
        ViewActions.waitForView(finishedLabelMatcher, waitingTimeout = 2500)
    }

    fun assert(f: DivFocusTypedActionsAssertions.() -> Unit) = f(DivFocusTypedActionsAssertions())
}

@StepsDsl
internal class DivFocusTypedActionsAssertions {
    fun checkInputFocused(): Unit = step("Check input has focus") {
        onView(inputMatcher).check(matches(isFocused()))
    }

    fun checkFocusCleared(): Unit = step("Check input has no focus") {
        onView(inputMatcher).check(matches(isNotFocused()))
    }
}

private fun clickOnView(matcher: Matcher<View>) = onView(matcher).perform(click())

private val focusInputMatcher = getTextMatcher("Focus input")
private val inputMatcher = getTextMatcher("input view")
private val finishedLabelMatcher = getTextMatcher("finished")

private fun getTextMatcher(text: String) = withText(text)
