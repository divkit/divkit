package com.yandex.div.steps

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.scrollTo
import com.yandex.div.view.swipeLeft
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import kotlin.math.roundToInt

internal fun gallery(f: DivGallerySteps.() -> Unit) = f(DivGallerySteps())

@StepsDsl
class DivGallerySteps: DivTestAssetSteps() {

    private val gallery = onView(isAssignableFrom(RecyclerView::class.java))

    fun ActivityTestRule<*>.buildContainer(): Unit = step("Build container") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun swipeLeft() = step("Swipe gallery left") {
        gallery.swipeLeft()
    }

    fun scrollTo(position: Int) = step("Scroll gallery to position $position") {
        gallery.scrollTo(position)
    }

    fun scrollItemToVisibility(
        position: Int,
        visibilityPercentage: Int,
    ): Unit =
        step("Scroll gallery item $position to $visibilityPercentage% visibility") {
            gallery.perform(scrollItemToVisibilityAction(position, visibilityPercentage))
        }
}

private fun scrollItemToVisibilityAction(
    position: Int,
    visibilityPercentage: Int,
): ViewAction =
    object : ViewAction {
        override fun getConstraints(): Matcher<View> =
            allOf(
                isAssignableFrom(RecyclerView::class.java),
                isDisplayed(),
            )

        override fun getDescription() = "scroll gallery item $position to $visibilityPercentage% visibility"

        override fun perform(
            uiController: UiController,
            view: View,
        ) {
            require(visibilityPercentage in 1..100)
            val recycler = view as RecyclerView
            val layoutManager = requireNotNull(recycler.layoutManager)
            require(layoutManager.canScrollHorizontally())

            layoutManager.scrollToPosition(position)
            uiController.loopMainThreadUntilIdle()

            val item =
                requireNotNull(layoutManager.findViewByPosition(position)) {
                    "Gallery item $position is not laid out"
                }
            val galleryVisibleRect = Rect()
            check(recycler.getGlobalVisibleRect(galleryVisibleRect)) { "Gallery is not visible" }
            val itemLocation = IntArray(2)
            item.getLocationOnScreen(itemLocation)
            val targetVisibleWidth = (item.width * visibilityPercentage / 100f).roundToInt()
            val scrollDistance =
                itemLocation[0] + item.width - galleryVisibleRect.left - targetVisibleWidth
            check(scrollDistance >= 0) {
                "Gallery item $position has already crossed $visibilityPercentage% visibility"
            }

            recycler.scrollBy(scrollDistance, 0)
            uiController.loopMainThreadUntilIdle()
        }
    }
