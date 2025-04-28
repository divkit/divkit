package com.yandex.div.steps

import android.graphics.Point
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.utils.ViewCoordinatesHelper.clickAtPosition
import com.yandex.div.utils.ViewCoordinatesHelper.getViewCenterCoordinates
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
    
    fun closeTooltip(): Unit = step("close tooltip") {
        Espresso.pressBack()
    }

    fun showNonCloseByTapOutsideTooltip(): Unit = step("Show tooltip with close_by_tap_outside: false") {
        onView(withText("tooltip with close_by_tap_outside: false")).perform(click())
    }

    fun showTooltipWithTapOutsideActions(): Unit = step("Show tooltip with tap_outside_actions") {
        onView(withText("tooltip with tap_outside_actions")).perform(click())
    }

    fun showNonModalTooltip(): Unit = step("Show non-modal tooltip") {
        onView(withText("tooltip mode = non_modal")).perform(click())
    }

    fun clickAtPoint(point: Point): Unit = step("Click at point (${point.x}, ${point.y})") {
        onView(isRoot()).perform(clickAtPosition(point.x, point.y))
    }

    fun getUnderlyingButtonPosition(): Point = step("Get position of the underlying button") {
        return@step getViewCenterCoordinates(withText("non modal test"))
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

    fun outsideActionsCalledIsFalse(): Unit = step("Check outside actions called is false") {
        onView(withText("Outside actions called: false")).check(matches(isDisplayed()))
    }

    fun outsideActionsCalledIsTrue(): Unit = step("Check outside actions called is true") {
        onView(withText("Outside actions called: true")).check(matches(isDisplayed()))
    }

    fun nonModalButtonClickedIsFalse(): Unit = step("Check non modal button clicked is false") {
        onView(withText("Non modal button clicked: false")).check(matches(isDisplayed()))
    }

    fun nonModalButtonClickedIsTrue(): Unit = step("Check non modal button clicked is true") {
        onView(withText("Non modal button clicked: true")).check(matches(isDisplayed()))
    }
}

private fun tooltip() = onView(withText("tooltip_text")).inRoot(isPlatformPopup())
