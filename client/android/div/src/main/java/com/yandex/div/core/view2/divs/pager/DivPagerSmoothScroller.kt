package com.yandex.div.core.view2.divs.pager

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.MainThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.render.common.pager.normalizePagerIndex
import kotlin.math.roundToInt

internal class DivPagerSmoothScroller(
    private val viewPager: ViewPager2,
    private val recyclerView: RecyclerView,
) {
    private var virtualItemCount = 0
    private var currentFakeDrag: FakeDrag? = null
    private var nextScroll: ScrollRequest? = null
    private val startNextScrollTask = Runnable { startNextScroll() }

    val isRunning: Boolean get() = currentFakeDrag != null || nextScroll != null

    val pendingInstantItem: Int? get() = nextScroll?.takeIf { it.duration == 0 }?.index

    private val touchListener = object : RecyclerView.SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                cancel()
            }
            return false
        }
    }

    private val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) = Unit

        override fun onViewDetachedFromWindow(view: View) {
            cancel()
        }
    }

    init {
        recyclerView.addOnItemTouchListener(touchListener)
        viewPager.addOnAttachStateChangeListener(attachListener)
    }

    @MainThread
    fun updateVirtualItemCount(count: Int) {
        if (virtualItemCount == count) {
            return
        }
        cancel()
        virtualItemCount = count
    }

    @MainThread
    fun dispose() {
        recyclerView.removeOnItemTouchListener(touchListener)
        viewPager.removeOnAttachStateChangeListener(attachListener)
        cancel()
    }

    @MainThread
    fun scrollTo(index: Int, duration: Int? = 0) {
        val drag = if (duration != null && duration > 0) {
            FakeDrag(index, duration)
        } else {
            null
        }
        nextScroll = ScrollRequest(index, duration, drag)
        recyclerView.removeCallbacks(startNextScrollTask)
        recyclerView.post(startNextScrollTask)
    }

    @MainThread
    private fun startNextScroll() {
        val request = nextScroll ?: return
        currentFakeDrag?.cancel(snapToPage = false)
        nextScroll = null
        val drag = request.fakeDrag
        if (drag != null) {
            currentFakeDrag = drag
            drag.start()
        } else {
            viewPager.setCurrentItem(request.index, request.duration == null)
        }
    }

    @MainThread
    fun cancel() {
        clearNextScroll()
        currentFakeDrag?.cancel(snapToPage = true)
        clearNextScroll()
    }

    @MainThread
    private fun clearNextScroll() {
        recyclerView.removeCallbacks(startNextScrollTask)
        nextScroll = null
    }

    @MainThread
    private fun finishFakeDrag(operation: FakeDrag, index: Int, completed: Boolean, snapToPage: Boolean = true) {
        if (currentFakeDrag !== operation) {
            return
        }
        // New actions wait in the UI queue until endFakeDrag and its callbacks return.
        if (viewPager.isFakeDragging) {
            viewPager.endFakeDrag()
        }
        recyclerView.stopScroll()
        if (snapToPage) {
            viewPager.setCurrentItem(index, false)
        }
        if (completed) {
            normalizeInfinitePosition()
        }
        currentFakeDrag = null
    }

    @MainThread
    private fun normalizeInfinitePosition() {
        if (virtualItemCount == 0) {
            return
        }
        val itemCount = viewPager.adapter?.itemCount ?: return
        val realItemCount = itemCount - virtualItemCount * 2
        if (realItemCount <= 0) {
            return
        }
        val currentItem = viewPager.currentItem
        val position = normalizePagerIndex(
            rawIndex = currentItem,
            realItemCount = realItemCount,
            edgeItemCount = virtualItemCount,
        ) + virtualItemCount
        if (position != currentItem) {
            recyclerView.scrollToPosition(position)
        }
    }

    @MainThread
    private fun nearestPage(): Int {
        val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return viewPager.currentItem
        val position = manager.findFirstVisibleItemPosition()
        val child = manager.findViewByPosition(position) ?: return viewPager.currentItem
        val helper = OrientationHelper.createOrientationHelper(manager, manager.orientation)
        val size = helper.getDecoratedMeasurement(child)
        if (size <= 0) {
            return viewPager.currentItem
        }
        val offset = currentOffset(helper, child)
        return position + (offset.toFloat() / size).roundToInt()
    }

    @MainThread
    private fun distanceTo(index: Int): Float? {
        val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return null
        val position = manager.findFirstVisibleItemPosition()
        val child = manager.findViewByPosition(position) ?: return null
        val helper = OrientationHelper.createOrientationHelper(manager, manager.orientation)
        val size = helper.getDecoratedMeasurement(child)
        if (size <= 0) {
            return null
        }
        val distance = (index - position).toFloat() * size - currentOffset(helper, child)
        return if (isHorizontalRtl) {
            distance
        } else {
            -distance
        }
    }

    @MainThread
    private fun currentOffset(helper: OrientationHelper, child: View): Int =
        if (isHorizontalRtl) {
            helper.getDecoratedEnd(child) - helper.endAfterPadding
        } else {
            helper.startAfterPadding - helper.getDecoratedStart(child)
        }

    private val isHorizontalRtl: Boolean
        get() = viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL && recyclerView.isLayoutRtl()

    private class ScrollRequest(
        val index: Int,
        val duration: Int?,
        val fakeDrag: FakeDrag?,
    )

    private inner class FakeDrag(
        private val index: Int,
        duration: Int,
    ) {
        private val animator = ValueAnimator().apply {
            this.duration = duration.toLong()
            interpolator = DecelerateInterpolator()
        }
        private var isFinished = false

        @MainThread
        fun start() {
            val distance = distanceTo(index)
            if (distance == null || distance == 0f || !viewPager.beginFakeDrag()) {
                finish(completed = true)
                return
            }
            if (isFinished) {
                return
            }
            var previousOffset = 0f
            animator.setFloatValues(0f, distance)
            animator.addUpdateListener {
                if (!isFinished) {
                    val offset = it.animatedValue as Float
                    viewPager.fakeDragBy(offset - previousOffset)
                    previousOffset = offset
                }
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                @MainThread
                override fun onAnimationEnd(animation: Animator) {
                    animation.removeAllListeners()
                    animator.removeAllUpdateListeners()
                    finish(completed = true)
                }
            })
            animator.start()
        }

        @MainThread
        fun cancel(snapToPage: Boolean) {
            if (isFinished) {
                return
            }
            animator.removeAllListeners()
            animator.removeAllUpdateListeners()
            animator.cancel()
            finish(completed = false, snapToPage = snapToPage)
        }

        @MainThread
        private fun finish(completed: Boolean, snapToPage: Boolean = true) {
            if (isFinished) {
                return
            }
            isFinished = true
            val targetPage = if (completed) {
                index
            } else {
                nearestPage()
            }
            finishFakeDrag(this, targetPage, completed, snapToPage)
        }
    }
}
