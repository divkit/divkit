package com.yandex.div.steps

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.internal.util.textString
import com.yandex.div.view.click
import com.yandex.div.view.tap
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.idling.SimpleIdlingResource
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.assertPopupDisplayed
import org.hamcrest.Matchers

private const val TIMER_FINISHED_TEXT = "DONE"

internal fun divSelect(f: DivSelectSteps.() -> Unit) = f(DivSelectSteps())

@StepsDsl
class DivSelectSteps : DivTestAssetSteps() {
    private val selectInteraction = onView(ViewMatchers.withHint("Select country"))
    private val firstItemInteraction = onView(withText("United Kingdom"))
    private val timerInteraction = onView(Matchers.anyOf(withText("IN PROGRESS")))

    fun ActivityTestRule<DummyActivity>.buildContainer() {
        testAsset = "ui_test_data/select/select_with_default.json"
        buildContainer(MATCH_PARENT, MATCH_PARENT)
    }

    fun clickOnSelect(): Unit = step("Click on select") {
        selectInteraction.click()
    }

    fun selectFirstOption(): Unit = step("Select first option") {
        firstItemInteraction.tap()
    }


    fun openSelectWithDelayedAction(action: String) = step("Open select with delayed action") {
        val timer = prepareTimerIdlingResource()
        invokeDelayedAction(action)

        clickOnSelect()
        assertPopupDisplayed()

        waitForIdlingResource(timer)
    }

    fun stealSelect(): View = step("Steal select view") {
        selectInteraction.stealView()
    }

    private fun invokeDelayedAction(action: String): Unit =
        step("Invoke action $action with delay") {
            onView(withText(action)).click()
        }

    private fun prepareTimerIdlingResource(): IdlingResource =
        step("Prepare timer idling resource") {
            timerInteraction.stealView().asIdlingResource {
                (this as TextView).textString == TIMER_FINISHED_TEXT
            }
        }

    private inline fun View.asIdlingResource(crossinline checkIdle: View.() -> Boolean) =
        object : SimpleIdlingResource() {
            override fun checkIdle(): Boolean = checkIdle(this@asIdlingResource)
        }

    private fun ViewInteraction.stealView(): View {
        var stolenView: View? = null
        check { view, _ -> stolenView = view }
        return stolenView!!
    }

    fun assert(f: DivSelectAssertions.() -> Unit) = f(DivSelectAssertions())
}

@StepsDsl
class DivSelectAssertions {
    private val popupInteraction = onView(ViewMatchers.withChild(withText("United Kingdom")))

    fun popupAlignedWithAnchor(anchorView: View) {
        popupInteraction.check { popup, _ ->
            val (left, top) = IntArray(2).apply { popup.getLocationOnScreen(this) }
            val (anchorLeft, anchorTop) = IntArray(2).apply { anchorView.getLocationOnScreen(this) }

            assert(left == anchorLeft)
            assert(top == anchorTop)
            assert(popup.width == anchorView.width)
        }
    }
}
