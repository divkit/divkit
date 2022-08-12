package com.yandex.div.steps

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.viewpager.widget.ViewPager
import com.yandex.div.utils.ViewPagerActions
import com.yandex.div.utils.runOnView
import com.yandex.test.util.StepsDsl
import org.junit.Assert
import ru.tinkoff.allure.Step.Companion.step

internal fun divTabs(f: DivTabsSteps.() -> Unit) = f(DivTabsSteps())

@StepsDsl
internal class DivTabsSteps : DivTestAssetSteps() {

    val selectedTab: Int
        get() = tabs().runOnView<ViewPager, Int> { it.currentItem } ?: -1

    fun ActivityTestRule<*>.buildContainer(): Unit = step("Build container") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, isScrollable = true)
    }

    fun selectTab(tab: Int): Unit = step("Select tab $tab") {
        tabs().perform(ViewPagerActions.scrollToPage(tab))
    }

    fun showMore(): Unit = step("Show more") {
        onView(withText("Показать ещё")).perform(scrollTo(), click())
    }

    fun assert(f: DivTabsAssertions.() -> Unit) = f(DivTabsAssertions())
}

private fun tabs() = onView(isAssignableFrom(ViewPager::class.java))

@StepsDsl
internal class DivTabsAssertions {

    fun checkSelectedTab(expected: Int, actual: Int): Unit =
        step("Check selected tab expected: $expected, actual: $actual") {
            Assert.assertEquals(expected, actual)
        }
}
