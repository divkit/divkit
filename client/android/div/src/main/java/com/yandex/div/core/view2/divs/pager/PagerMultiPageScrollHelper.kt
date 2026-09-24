package com.yandex.div.core.view2.divs.pager

import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.utils.isForwardScroll
import com.yandex.div.core.view2.divs.utils.snapPositions
import com.yandex.div.internal.scroll.findDirectionalSnapPosition
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Enables a multi-page fling in ViewPager2.
 *
 * The helper calculates the landing page when the finger is released and performs one
 * decelerating scroll to it. A slow drag uses the gallery's directional rule, so snapping never
 * goes against the gesture.
 *
 * While that single scroll is running [isScrollingToTarget] is `true`: an infinite scroll listener
 * must not call `RecyclerView.scrollToPosition` then, because it stops the scroll and cuts the
 * fling off at the loop seam.
 */
internal class PagerMultiPageScrollHelper(
    private val viewPager: ViewPager2,
    private val recyclerView: RecyclerView,
) : LinearSnapHelper() {

    private val directionTracker = DirectionTracker()

    private var originalSnapHelper: SnapHelper? = null
    private var attached = false
    private var forward = true
    private var applyDirectionalSnap = false
    private var flingHandled = false
    private var userInitiatedScroll = false

    /** `true` while a multi-page fling started by this helper is still running. */
    var isScrollingToTarget = false
        private set

    fun attach() {
        if (attached) return

        originalSnapHelper = recyclerView.onFlingListener as? SnapHelper
        originalSnapHelper?.attachToRecyclerView(null)
        // attachToRecyclerView() throws when any fling listener is still set, not only a SnapHelper.
        recyclerView.onFlingListener = null
        attachToRecyclerView(recyclerView)
        // RecyclerView notifies scroll listeners in reverse registration order, so the tracker is
        // registered last to run before the snap listener that attachToRecyclerView() added.
        recyclerView.addOnScrollListener(directionTracker)

        attached = true
    }

    fun detach() {
        if (!attached) return

        attachToRecyclerView(null)
        recyclerView.removeOnScrollListener(directionTracker)
        originalSnapHelper?.attachToRecyclerView(recyclerView)
        originalSnapHelper = null

        applyDirectionalSnap = false
        flingHandled = false
        userInitiatedScroll = false
        isScrollingToTarget = false

        attached = false
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        forward = isForwardScroll(isVertical, velocityX, velocityY, recyclerView.isLayoutRtl())
        userInitiatedScroll = true
        // findTargetSnapPosition() below calls findSnapView(), which must pick the centred page
        // here. A directional snap left over from a gesture that never settled would skew it.
        applyDirectionalSnap = false

        val velocity = abs(if (isVertical) velocityY else velocityX)
        if (velocity <= recyclerView.minFlingVelocity) return false

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return false
        val target = findTargetSnapPosition(layoutManager, velocityX, velocityY)

        if (target == RecyclerView.NO_POSITION || target == layoutManager.currentPosition()) {
            return layoutManager.snap()
        }

        val isPagerLaidOut = (if (isVertical) viewPager.height else viewPager.width) > 0
        if (!isPagerLaidOut) return false

        // Reporting a fling as handled without starting a scroll would leave RecyclerView in the
        // dragging state forever, because it only resets to idle when onFling() returns false.
        val distance = distanceToTarget(layoutManager, target)?.takeIf { it != 0 } ?: return false

        val duration = flingDecelerationDuration(abs(distance), velocity)
        flingHandled = true
        isScrollingToTarget = true
        if (isVertical) {
            recyclerView.smoothScrollBy(0, distance, DecelerateInterpolator(), duration)
        } else {
            recyclerView.smoothScrollBy(distance, 0, DecelerateInterpolator(), duration)
        }
        return true
    }

    private fun LinearLayoutManager.currentPosition(): Int? = findSnapView(this)?.let { getPosition(it) }

    private fun LinearLayoutManager.snap(): Boolean {
        val target = findTarget()
        findViewByPosition(target) ?: return false

        val smoothScroller = createScroller(this) ?: return false
        smoothScroller.targetPosition = target

        flingHandled = true
        startSmoothScroll(smoothScroller)
        return true
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (!applyDirectionalSnap) {
            return super.findSnapView(layoutManager)
        }
        applyDirectionalSnap = false

        val linearLayoutManager = layoutManager as? LinearLayoutManager
            ?: return super.findSnapView(layoutManager)
        val target = linearLayoutManager.findTarget()
        return linearLayoutManager.findViewByPosition(target) ?: super.findSnapView(layoutManager)
    }

    private fun LinearLayoutManager.findTarget(): Int = findDirectionalSnapPosition(forward, snapPositions())

    private fun distanceToTarget(layoutManager: LinearLayoutManager, targetPosition: Int): Int? {
        layoutManager.findViewByPosition(targetPosition)?.let { targetView ->
            val distance = calculateDistanceToFinalSnap(layoutManager, targetView) ?: return null
            return if (isVertical) distance[1] else distance[0]
        }

        val currentView = super.findSnapView(layoutManager) ?: return null
        val currentPosition = layoutManager.getPosition(currentView)
        if (currentPosition == RecyclerView.NO_POSITION) return null

        val currentDistance = calculateDistanceToFinalSnap(layoutManager, currentView)
            ?.let { if (isVertical) it[1] else it[0] }
            ?: return null

        // The target page is not laid out, so its distance is extrapolated from the page step. It
        // is taken from the decorated page rather than from the viewport, which keeps the helper
        // independent of how ViewPager2 measures its raw pages.
        val pageStep = if (isVertical) {
            layoutManager.getDecoratedMeasuredHeight(currentView)
        } else {
            layoutManager.getDecoratedMeasuredWidth(currentView)
        }
        if (pageStep <= 0) return null

        val vector = layoutManager.computeScrollVectorForPosition(targetPosition) ?: return null
        val direction = if (isVertical) vector.y.sign else vector.x.sign
        if (direction == 0f) return currentDistance

        return currentDistance + (abs(targetPosition - currentPosition) * pageStep * direction).roundToInt()
    }

    private val isVertical get() = viewPager.orientation == ViewPager2.ORIENTATION_VERTICAL

    private inner class DirectionTracker : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (recyclerView.scrollState != RecyclerView.SCROLL_STATE_DRAGGING) return
            if (dx == 0 && dy == 0) return

            forward = isForwardScroll(isVertical, dx, dy, recyclerView.isLayoutRtl())
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    userInitiatedScroll = true
                    flingHandled = false
                    isScrollingToTarget = false
                }

                RecyclerView.SCROLL_STATE_IDLE -> {
                    // The snap listener runs right after this one and calls findSnapView(). A
                    // fling has already selected its target, while a slow drag still needs one.
                    applyDirectionalSnap = userInitiatedScroll && !flingHandled
                    userInitiatedScroll = false
                    flingHandled = false
                    isScrollingToTarget = false
                }
            }
        }
    }

    private companion object {
        fun flingDecelerationDuration(distancePx: Int, velocityPxPerSecond: Int): Int {
            if (distancePx == 0) return 0
            val duration = 2.0 * abs(distancePx) * 1000.0 / abs(velocityPxPerSecond).coerceAtLeast(1)
            return ceil(duration).toInt().coerceAtLeast(1)
        }
    }
}
