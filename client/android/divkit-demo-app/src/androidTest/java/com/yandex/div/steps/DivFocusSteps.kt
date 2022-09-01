package com.yandex.div.steps

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div.view.checkNotExist
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import ru.tinkoff.allure.step

internal fun divFocus(f: DivFocusSteps.() -> Unit) = f(DivFocusSteps())

@StepsDsl
internal class DivFocusSteps {

    fun clickOnMiddleInput(): Unit = step("Click on middle input") {
        clickOnView(middleInputMatcher)
    }

    fun clearMiddleInput(): Unit = step("Clear middle input") {
        onView(middleInputMatcher).perform(clearText())
    }

    fun clickOnBottomInput(): Unit = step("Click on bottom input") {
        clickOnView(bottomInputMatcher)
    }

    fun assert(f: DivFocusAssertions.() -> Unit) = f(DivFocusAssertions())
}

@StepsDsl
internal class DivFocusAssertions {

    fun blurOnTopHandled(): Unit = step("Check blur action on top input") {
        checkActionHandled(topInputMatcher, blurActionHandledMatcher)
    }

    fun focusOnMiddleHandled(): Unit = step("Check focus action on middle input") {
        checkActionHandled(middleInputMatcher, focusActionHandledMatcher)
    }

    fun focusOnMiddleNotHandled(): Unit = step("Check focus action on middle input") {
        onView(allOf(middleInputMatcher, focusActionHandledMatcher)).checkNotExist()
    }

    fun focusOnBottomHandled(): Unit = step("Check focus action on bottom input") {
        checkActionHandled(bottomInputMatcher, focusActionHandledMatcher)
    }

    fun blurOnBottomHandled(): Unit = step("Check blur action on bottom input") {
        checkActionHandled(bottomInputMatcher, blurActionHandledMatcher)
    }
}

private fun clickOnView(matcher: Matcher<View>) = onView(matcher).perform(click())

private val topInputMatcher = getPositionMatcher("top_input")
private val middleInputMatcher = getPositionMatcher("middle_input")
private val bottomInputMatcher = getPositionMatcher("bottom_input")

private fun getPositionMatcher(tag: String) = withTagValue(equalTo(tag))

private fun checkActionHandled(position: Matcher<View>, action: Matcher<View>) =
    onView(allOf(position, action)).checkIsDisplayed()

private val blurActionHandledMatcher = withText("blurred")
private val focusActionHandledMatcher = withText("focused")
