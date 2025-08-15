package com.yandex.div.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.viewpager.widget.ViewPager
import org.hamcrest.Matcher

/**
 * @see https://github.com/material-components/material-components-android/blob/4b9148cc6660539c06569f8a92e6b57c0e29e798/tests/javatests/com/google/android/material/testutils/ViewPagerActions.java#L30
 */
object ViewPagerActions {

    /** Moves `ViewPager` to specific page.  */
    fun scrollToPage(page: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayingAtLeast(90)
            }

            override fun getDescription(): String {
                return "ViewPager move to a specific page"
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val viewPager = view as ViewPager
                viewPager.setCurrentItem(page, false)
                uiController.loopMainThreadUntilIdle()
            }
        }
    }
}
