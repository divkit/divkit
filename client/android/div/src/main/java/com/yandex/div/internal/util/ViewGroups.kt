package com.yandex.div.internal.util

import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.yandex.div.R

/**
 * Helps to resolve conflicts in nested scrollable views.
 */
private val SCROLLABLE_RECYCLER_VIEW_IDS = intArrayOf(R.id.div_gallery)

/**
 * Checks, whether a point is inside a scrollable child
 * Should be used with [android.support.v7.widget.helper.FixScrollTouchHelper]
 *
 * @return true if the point is inside a scrollable child
 */
internal fun ViewGroup.hasScrollableChildUnder(event: MotionEvent): Boolean {
    val rawX = event.rawX
    val rawY = event.rawY
    val scrollDir = if (event.historySize < 1) {
        -1
    } else {
        if (event.getHistoricalX(0) < event.x)
            ItemTouchHelper.LEFT
        else
            ItemTouchHelper.RIGHT
    }

    for (i in 0 until childCount) {
        val childAt = getChildAt(i)
        if (childAt.id == R.id.div_tabs_block && Views.hitTest(childAt, rawX, rawY)
                && (Views.findViewAndCast(childAt, R.id.div_tabs_pager_container) as ViewPager).isScrollableViewPagerUnder(rawX, rawY, scrollDir)) {
            return true
        }
        if (childAt is RecyclerView && childAt.isScrollableRecyclerUnder(rawX, rawY, scrollDir)) {
            return true
        }
    }

    return false
}

private fun ViewPager.isScrollableViewPagerUnder(rawX: Float, rawY: Float, scrollDir: Int): Boolean {
    if (canScrollMore(scrollDir)) return true

    // this viewpager tab may have scrollable children

    for (v in SCROLLABLE_RECYCLER_VIEW_IDS.indices) {
        val recyclerView = Views.findOptionalViewAndCast<RecyclerView>(this, SCROLLABLE_RECYCLER_VIEW_IDS[v])
        if (recyclerView != null && recyclerView.isScrollableRecyclerUnder(rawX, rawY, scrollDir)) {
            return true
        }
    }
    return false
}

private fun RecyclerView.isScrollableRecyclerUnder(rawX: Float, rawY: Float, scrollDir: Int): Boolean {
    return Views.hitTest(this, rawX, rawY) && canScrollMore(scrollDir)
}

/**
 * Checks whether RecyclerView view can still be scrolled
 *
 * @param recycler view to check
 * @param scrollDir    should be one of [ItemTouchHelper.LEFT] etc
 * @return true if the view can be scrolled into that direction
 */
private fun RecyclerView.canScrollMore(scrollDir: Int): Boolean {
    if (scrollDir < 0) { // means direction is undefined yet
        return true
    }
    if (scrollDir == ItemTouchHelper.LEFT) {
        return computeHorizontalScrollOffset() > 0
    } else if (scrollDir == ItemTouchHelper.RIGHT) {
        return computeHorizontalScrollExtent() + computeHorizontalScrollOffset() < computeHorizontalScrollRange()
    }

    return false
}

/**
 * Checks whether ViewPager view can still be scrolled
 *
 * @param viewPager view to check
 * @param scrollDir should be one of [ItemTouchHelper.LEFT] etc
 * @return true if the view can be scrolled into that direction
 */
private fun ViewPager.canScrollMore(scrollDir: Int): Boolean {
    if (scrollDir < 0) { // means direction is undefined yet
        return true
    }
    if (scrollDir == ItemTouchHelper.LEFT) {
        // negative to left
        return canScrollHorizontally(-1)
    } else if (scrollDir == ItemTouchHelper.RIGHT) {
        // positive to right
        return canScrollHorizontally(1)
    }
    return false
}
