package com.yandex.div.steps

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isFocused
import androidx.test.espresso.matcher.ViewMatchers.isNotFocused
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.ViewActions
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matcher

internal fun divFocusTypedActions(f: DivFocusTypedActionsSteps.() -> Unit) = f(DivFocusTypedActionsSteps())

@StepsDsl
internal class DivFocusTypedActionsSteps : DivTestAssetSteps() {

    fun ActivityTestRule<DummyActivity>.buildContainer() {
        testAsset = "ui_test_data/focus/focus-element-and-clear-actions.json"
        buildContainer(MATCH_PARENT, MATCH_PARENT)
    }

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
