package com.yandex.div.utils

import android.os.SystemClock
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.viewpager2.widget.ViewPager2
import org.hamcrest.Matcher

/**
 * @see https://github.com/material-components/material-components-android/blob/4b9148cc6660539c06569f8a92e6b57c0e29e798/tests/javatests/com/google/android/material/testutils/ViewPagerActions.java#L30
 */
object ViewPagerActions {

    fun waitForIdle(): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isDisplayingAtLeast(90)

        override fun getDescription() = "Wait for ViewPager2 to finish scrolling"

        override fun perform(uiController: UiController, view: View) {
            val viewPager = view as ViewPager2
            val deadline = SystemClock.uptimeMillis() + 5000
            while (viewPager.scrollState != ViewPager2.SCROLL_STATE_IDLE && SystemClock.uptimeMillis() < deadline) {
                uiController.loopMainThreadForAtLeast(16)
            }
            check(viewPager.scrollState == ViewPager2.SCROLL_STATE_IDLE) { "ViewPager2 did not finish scrolling" }
        }
    }

    /** Moves `ViewPager2` to specific page. */
    fun scrollToPage(page: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayingAtLeast(90)
            }

            override fun getDescription(): String {
                return "ViewPager2 move to a specific page"
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val viewPager = view as ViewPager2
                viewPager.setCurrentItem(page, false)
                uiController.loopMainThreadUntilIdle()
            }
        }
    }
}
