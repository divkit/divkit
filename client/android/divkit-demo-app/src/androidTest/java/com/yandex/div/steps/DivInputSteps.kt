package com.yandex.div.steps

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl

internal fun divInput(f: DivInputSteps.() -> Unit) = f(DivInputSteps())

@StepsDsl
class DivInputSteps : DivTestAssetSteps() {

    fun ActivityTestRule<DummyActivity>.buildContainerForCase(case: String) {
        testAsset = "ui_test_data/input/div_input_$case.json"
        buildContainer(MATCH_PARENT, MATCH_PARENT)
    }

    fun typeTextInInput(text: String): Unit = step("Enter text ") {
        onView(isAssignableFrom(EditText::class.java)).perform(typeText(text))
    }

    fun assert(f: DivInputAssertions.() -> Unit) = f(DivInputAssertions())
}

@StepsDsl
class DivInputAssertions {

    fun textTyped(text: String): Unit = step("Assert text '$text' is typed in input") {
        onView(withText("Text: $text")).check(matches(isDisplayed()))
    }
}
