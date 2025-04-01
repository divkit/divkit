package com.yandex.div.steps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.utils.clickOutside
import com.yandex.div.utils.swipeLeftOutside
import com.yandex.div2.DivTooltip
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.assertNoPopupsAreDisplayed

internal fun tooltipDiv(f: DivTooltipSteps.() -> Unit) = f(DivTooltipSteps())

@StepsDsl
internal class DivTooltipSteps {
    fun showTooltip(p: DivTooltip.Position): Unit = step("show tooltip with position: $p") {
        onView(withText(DivTooltip.Position.toString(p))).perform(click())
    }

    fun clickOnTooltipWrapper(): Unit = step("Click on tooltip background") {
        tooltip().perform(clickOutside())
    }

    fun swipeOnTooltipWrapper(): Unit = step("Swipe on tooltip background") {
        tooltip().perform(swipeLeftOutside())
    }

    fun clickTooltip(): Unit = step("click on tooltip") {
        tooltip().perform(click())
    }

    fun assert(f: DivTooltipAssertions.() -> Unit) = f(DivTooltipAssertions())
}

@StepsDsl
internal class DivTooltipAssertions {
    fun tooltipShown(): Unit = step("Check tooltip is shown") {
        tooltip().check(matches(isDisplayed()))
    }

    fun noTooltipsDisplayed() = step("Check no tooltips are displayed") {
        assertNoPopupsAreDisplayed()
    }

    fun statusIsClicked(): Unit = step("Check status is clicked") {
        onView(withText("clicked")).check(matches(isDisplayed()))
    }

    fun disappearActionHandled(): Unit = step("Check disappear action is handled") {
        onView(withText("disappear_work")).check(matches(isDisplayed()))
    }
}

private fun tooltip() = onView(withText("tooltip_text")).inRoot(isPlatformPopup())
