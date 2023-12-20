package com.yandex.div.steps

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div2.DivTooltip
import com.yandex.divkit.demo.R
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.assertNoPopupsAreDisplayed
import org.hamcrest.Matchers.allOf
import ru.tinkoff.allure.step

internal fun tooltipDiv(f: DivTooltipSteps.() -> Unit) = f(DivTooltipSteps())

@StepsDsl
internal class DivTooltipSteps {
    fun showTooltip(p: DivTooltip.Position): Unit = step("show tooltip with position: $p") {
        onView(withText(DivTooltip.Position.toString(p))).perform(click())
    }

    fun clickShowing(): Unit = step("Click on status \"showing\"") {
        onView(withText("showing")).perform(click())
    }

    fun swipeOnDiv(): Unit = step("Swipe down div") {
        divView().perform(swipeDown())
    }

    fun clickTooltip(): Unit = step("click on tooltip") {
        tooltip().perform(clickOnPopupWindow())
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
private fun divView() = onView(withId(R.id.morda_screenshot_div))

private fun clickOnPopupWindow() = object : ViewAction {
    override fun getConstraints() = allOf(isClickable(), isDisplayed())

    override fun getDescription() = "Click on popup"

    override fun perform(uiController: UiController, view: View) {
        val layoutParams = view.findPopupDecorViewLayoutParams()
        Tap.SINGLE.sendTap(
            uiController,
            floatArrayOf(layoutParams.x + view.width / 2f, layoutParams.y + view.height / 2f),
            floatArrayOf(0f, 0f),
            InputDevice.SOURCE_UNKNOWN,
            MotionEvent.BUTTON_PRIMARY
        )
    }
}

private fun View.findPopupDecorViewLayoutParams(): WindowManager.LayoutParams {
    val parent = parent
    if (parent is View) {
        return if (parent.layoutParams is WindowManager.LayoutParams) {
            parent.layoutParams as WindowManager.LayoutParams
        } else {
            parent.findPopupDecorViewLayoutParams()
        }
    } else {
        throw AssertionError("Not a popup child: $this")
    }
}
