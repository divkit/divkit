package com.yandex.div.steps

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.scrollTo
import com.yandex.div.view.tap
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import ru.tinkoff.allure.Step

internal fun tabSwipe(f: DivTabSwipeTestSteps.() -> Unit) = f(DivTabSwipeTestSteps())

@StepsDsl
class DivTabSwipeTestSteps : DivTestAssetSteps() {

    fun ActivityTestRule<*>.buildContainer(): Unit = Step.step("Build container") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun swipeTabLeft(): Unit = Step.step("Swipe tab left") {
        tab.perform(swipeLeft())
    }

    fun swipeTabRight(): Unit = Step.step("Swipe tab right") {
        tab.perform(swipeRight())
    }

    fun clickOnHeader(header: String) = Step.step("Click on header \"$header\"") {
        onView(allOf(withParent(withResourceName("tab_sliding_oval_indicator")),
            withText(header))).scrollTo().tap()
    }

    fun swipeHeadersLeft() = Step.step("Scroll headers") {
        onView(withResourceName("base_tabbed_title_container_scroller")).perform(swipeLeft())
    }

    internal fun assert(f: DivTabSwipeAssertions.() -> Unit) = f(DivTabSwipeAssertions())
}

private val tab get() = onView(withResourceName("div_tabs_pager_container"))

@StepsDsl
class DivTabSwipeAssertions() {

    fun checkTabPageChangedLogged(cardId: String, tabId: Int, visibilityActions: List<String>) =
        step("Check tab page change logged, cardId=$cardId, selectedTab=$tabId") {
            Assert.assertTrue(visibilityActions.any {
                it == "logTabPageChanged(cardId = $cardId, selectedTab = $tabId)"
            })
        }

    fun checkTabTitleScrollLogged(cardId: String, visibilityActions: List<String>) =
        step("Check tab title scroll logged") {
            Assert.assertTrue(visibilityActions.any {
                it == "logTabTitlesScroll(cardId = $cardId)"
            })
        }
}
