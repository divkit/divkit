package com.yandex.div.internal.widget.tabs

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.pager.DivPagerSmoothScroller
import kotlin.math.abs

internal class TabsSmoothScrollController(
    private val viewPager: ViewPager2,
    private val recyclerView: RecyclerView,
) {

    private var smoothScroller: DivPagerSmoothScroller? = null

    fun scrollToItem(position: Int, duration: Int?) {
        val scrollDuration = if (duration == null && recyclerView.childCount == 0) {
            0
        } else {
            duration
        }
        val useDefaultScroll = scrollDuration == null || scrollDuration == 0
        if (useDefaultScroll && smoothScroller?.isRunning != true && !recyclerView.isComputingLayout) {
            // Tab runtimes must switch before subsequent actions, including before attach.
            viewPager.setCurrentItem(
                position, scrollDuration == null && shouldAnimateDefaultScroll(position),
            )
            return
        }
        val scroller = smoothScroller ?: DivPagerSmoothScroller(
            viewPager, recyclerView, ::shouldAnimateDefaultScroll,
        ).also {
            smoothScroller = it
        }
        scroller.scrollTo(position, scrollDuration)
    }

    private fun shouldAnimateDefaultScroll(position: Int): Boolean {
        if (recyclerView.childCount == 0) {
            return false
        }
        val itemCount = viewPager.adapter?.itemCount ?: return false
        if (itemCount == 0) {
            return false
        }
        // Match ViewPager2's bounded target before checking its long-jump branch.
        val target = position.coerceIn(0, itemCount - 1)
        if (target == viewPager.currentItem) {
            return true
        }
        // ViewPager2 posts an unowned Runnable for jumps beyond three pages.
        return abs(target - getScrollPosition()) <= MAX_NATIVE_ANIMATED_PAGE_DISTANCE
    }

    private fun getScrollPosition(): Double {
        if (viewPager.scrollState == ViewPager2.SCROLL_STATE_IDLE) {
            return viewPager.currentItem.toDouble()
        }
        val manager = recyclerView.layoutManager as LinearLayoutManager
        val position = manager.findFirstVisibleItemPosition()
        val page = manager.findViewByPosition(position) ?: return RecyclerView.NO_POSITION.toDouble()
        val helper = OrientationHelper.createHorizontalHelper(manager)
        val start = helper.getDecoratedStart(page) - helper.startAfterPadding
        val offset = if (viewPager.isLayoutRtl()) {
            start
        } else {
            -start
        }
        val size = helper.getDecoratedMeasurement(page)
        val fraction = if (size == 0) {
            0f
        } else {
            offset.toFloat() / size
        }
        return position + fraction.toDouble()
    }

    fun cancelScroll() {
        smoothScroller?.cancel()
        recyclerView.stopScroll()
    }

    fun onTouchDown() {
        smoothScroller?.cancel()
    }

    fun release() {
        smoothScroller?.dispose()
        smoothScroller = null
    }

    private companion object {
        const val MAX_NATIVE_ANIMATED_PAGE_DISTANCE = 3
    }
}
