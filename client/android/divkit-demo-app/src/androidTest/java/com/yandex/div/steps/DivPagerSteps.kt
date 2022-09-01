package com.yandex.div.steps

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.rule.ActivityTestRule
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.view.swipeLeft
import com.yandex.div.view.swipeRight
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import ru.tinkoff.allure.step as allureStep

internal fun pager(f: DivPagerSteps.() -> Unit) = f(DivPagerSteps())

@StepsDsl
class DivPagerSteps: DivTestAssetSteps() {

    fun ActivityTestRule<*>.buildContainer(): Unit = allureStep("Build container") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun swipeLeft() = step("Swipe pager left") {
        pager.swipeLeft()
    }

    fun swipeRight() = step("Swipe pager right") {
        pager.swipeRight()
    }

    fun assert(f: DivPagerAssertions.() -> Unit) = f(DivPagerAssertions())
}

private val pager = onView(isAssignableFrom(ViewPager2::class.java))

@StepsDsl
class DivPagerAssertions {

    fun checkOnPage(page: Int) = step("Check pager on page=$page") {
        pager.perform(ViewActions.longClick()).check(ViewAssertions.matches(getMatcher(page)))
    }

    private fun getMatcher(page: Int) : Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Check pager selected page=$page")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is ViewPager2) return false
                return view.currentItem == page
            }
        }
    }
}
