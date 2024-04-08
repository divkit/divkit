package com.yandex.div.steps

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isFocusable
import androidx.test.espresso.matcher.ViewMatchers.isFocused
import androidx.test.espresso.matcher.ViewMatchers.isNotFocusable
import androidx.test.espresso.matcher.ViewMatchers.isNotFocused
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo

internal fun divFocusableInput(f: DivFocusableInputSteps.() -> Unit) = f(DivFocusableInputSteps())

private val inputMatcher = isAssignableFrom(EditText::class.java)
private val buttonMatcher = withText(endsWith("able"))

@StepsDsl
class DivFocusableInputSteps : DivTestAssetSteps() {

    fun ActivityTestRule<DummyActivity>.buildContainer() {
        testAsset = "regression_test_data/input_is_enabled.json"
        buildContainer(MATCH_PARENT, MATCH_PARENT)
    }

    fun clickOnInput(): Unit = step("Click on input") {
        onView(inputMatcher).perform(click())
    }

    fun clickOnChangeStateButton(): Unit = step("Set change is_enabled state") {
        onView(buttonMatcher).perform(click())
    }

    fun assert(f: DivFocusableInputAssertions.() -> Unit) = f(DivFocusableInputAssertions())
}

@StepsDsl
class DivFocusableInputAssertions {

    fun checkInputIsFocused(): Unit = step("Assert text input isFocusable = true") {
        onView(inputMatcher).apply {
            check(matches(isFocusable()))
            check(matches(isFocused()))
        }

    }

    fun checkInputIsNotFocused(): Unit = step("Assert text input isFocusable = false") {
        onView(inputMatcher).apply {
            check(matches(isNotFocusable()))
            check(matches(isNotFocused()))
        }
    }
}
