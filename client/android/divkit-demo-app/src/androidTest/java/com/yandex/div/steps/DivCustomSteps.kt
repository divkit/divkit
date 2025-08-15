package com.yandex.div.steps

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl

internal fun divCustom(f: DivCustomSteps.() -> Unit) = f(DivCustomSteps())

@StepsDsl
class DivCustomSteps : DivTestAssetSteps() {

    fun ActivityTestRule<DummyActivity>.buildContainerForCase(case: String) {
        testAsset = "ui_test_data/custom/div_custom_$case.json"
        buildContainer(MATCH_PARENT, MATCH_PARENT)
    }

    fun clickOnText(text: String): Unit = step("Click on text: '$text'") {
        onView(withText(text)).perform(click())
    }

    fun assert(f: DivCustomAssertions.() -> Unit) = f(DivCustomAssertions())
}

@StepsDsl
class DivCustomAssertions {

    fun textDisplayed(text: String): Unit = step("Assert text '$text' is displayed") {
        onView(withText(text)).check(matches(isDisplayed()))
    }
}
