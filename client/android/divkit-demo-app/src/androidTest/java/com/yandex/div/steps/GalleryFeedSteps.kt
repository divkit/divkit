package com.yandex.div.steps

import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.yandex.div2.DivVisibilityAction
import com.yandex.divkit.demo.R
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matcher
import org.junit.Assert

// Timeout in millis to wait for visibility actions being dispatched
private const val VISIBILITY_ACTIONS_TIMEOUT = 500L

internal fun galleryFeed(f: GalleryFeedSteps.() -> Unit) = f(GalleryFeedSteps())

@StepsDsl
internal class GalleryFeedSteps {

    fun scrollPerItem(): Unit = step("scroll feed per item") {
        for (i in 0 until 10) {
            feed().perform(object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return ViewMatchers.isAssignableFrom(RecyclerView::class.java)
                }

                override fun getDescription(): String {
                    return "scroll to position with snapping"
                }

                override fun perform(uiController: UiController, view: View) {
                    if (view !is RecyclerView) {
                        return
                    }
                    val smoothScroller = object : LinearSmoothScroller(view.context) {
                        override fun getVerticalSnapPreference() = SNAP_TO_START
                    }
                    smoothScroller.targetPosition = i
                    view.layoutManager?.startSmoothScroll(smoothScroller)
                    uiController.loopMainThreadForAtLeast(VISIBILITY_ACTIONS_TIMEOUT)
                }
            })
        }
    }

    fun assert(f: GalleryFeedAssertions.() -> Unit) = f(GalleryFeedAssertions())
}

private fun feed() = onView(withId(R.id.gallery_feed))

@StepsDsl
internal class GalleryFeedAssertions {

    fun checkTrackedVisibilityActionsForContent(visibilityActions: List<DivVisibilityAction>): Unit =
        step("check tracked visibility actions for content") {
            Assert.assertTrue(visibilityActions.any { it.logId.rawValue == "content_item_show:0" })
        }
}
