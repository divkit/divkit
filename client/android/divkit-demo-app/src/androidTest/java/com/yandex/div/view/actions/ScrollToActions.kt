package com.yandex.div.view.actions

import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import androidx.core.view.descendants
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step
import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Target
import org.hamcrest.Matchers.equalTo

fun scrollToSwipe(step: Step.ScrollTo): ViewAction = SwipeWithinVisibleBounds(
    step.from,
    step.to,
    holdDurationMs = 0,
    swiper = LinearSwiper(holdDurationMs = 0, moveDurationMs = Step.ScrollTo.SWIPE_DURATION_MS),
)

// Pages of ViewPager2 next to the current one are shown, but lie outside the container.
fun View.isScrollDestinationVisible(destination: Target): Boolean {
    val matcher = when (destination) {
        is Target.DivId -> withTagValue(equalTo(destination.id))
        is Target.Text -> withText(destination.text)
    }
    val matches = (this as? ViewGroup)?.descendants
        ?.filter { it.isShown && matcher.matches(it) }
        ?.toList()
        .orEmpty()
    if (matches.size > 1) {
        throw AssertionError("Expected one destination, found ${matches.size}: $destination")
    }
    val view = matches.singleOrNull() ?: return false
    val visibleBounds = Rect()
    if (!getGlobalVisibleRect(visibleBounds)) return false
    val location = IntArray(2)
    view.getLocationInWindow(location)
    return RectF(visibleBounds).contains(
        location[0] + view.width / 2f,
        location[1] + view.height / 2f
    )
}
