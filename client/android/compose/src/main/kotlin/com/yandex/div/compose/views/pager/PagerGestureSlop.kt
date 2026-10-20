package com.yandex.div.compose.views.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration

/**
 * The touch slop the pager list waits for before it drags its pages, and the configuration its page content
 * keeps for its own gestures.
 *
 * A pager that restricts its parent's scroll claims a gesture at the default touch slop, so no ancestor can
 * intercept it, the way `ParentScrollRestrictor` does with `requestDisallowInterceptTouchEvent` on DOWN; its
 * pages then wait for [pagingTouchSlopExtra] more inside [PagerSwipeLimiter]. Without the restriction the list
 * itself waits for the paging touch slop, as ViewPager2 does, so an ancestor scrolling along the same axis
 * still claims the gesture first.
 */
internal class PagerGestureSlop(
    val listViewConfiguration: ViewConfiguration,
    val contentViewConfiguration: ViewConfiguration,
    val pagingTouchSlopExtra: Float,
)

@Composable
internal fun rememberPagerGestureSlop(isParentScrollRestricted: Boolean): PagerGestureSlop {
    val viewConfiguration = LocalViewConfiguration.current
    val pagingTouchSlopExtra = rememberPagingTouchSlopExtra()
    val pagerViewConfiguration = rememberPagerViewConfiguration(viewConfiguration, pagingTouchSlopExtra)
    return remember(isParentScrollRestricted, viewConfiguration, pagerViewConfiguration, pagingTouchSlopExtra) {
        if (isParentScrollRestricted) {
            PagerGestureSlop(viewConfiguration, viewConfiguration, pagingTouchSlopExtra)
        } else {
            PagerGestureSlop(pagerViewConfiguration, viewConfiguration, pagingTouchSlopExtra = 0f)
        }
    }
}

/** How much longer than the default touch slop ViewPager2 waits before it starts a page drag. */
@Composable
private fun rememberPagingTouchSlopExtra(): Float {
    val context = LocalContext.current
    val touchSlop = LocalViewConfiguration.current.touchSlop
    return remember(context, touchSlop) {
        (android.view.ViewConfiguration.get(context).scaledPagingTouchSlop - touchSlop).coerceAtLeast(0f)
    }
}

/** Uses the paging touch slop that ViewPager2 waits for before it starts a page drag. */
@Composable
private fun rememberPagerViewConfiguration(
    configuration: ViewConfiguration,
    pagingTouchSlopExtra: Float,
): ViewConfiguration {
    return remember(configuration, pagingTouchSlopExtra) {
        object : ViewConfiguration by configuration {
            override val touchSlop = configuration.touchSlop + pagingTouchSlopExtra
        }
    }
}
