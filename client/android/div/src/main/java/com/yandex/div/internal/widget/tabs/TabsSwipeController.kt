package com.yandex.div.internal.widget.tabs

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.sign

internal class TabsSwipeController(
    private val host: ViewGroup,
    private val viewPager: ViewPager2,
    private val recyclerView: RecyclerView,
    private val smoothScrollController: TabsSmoothScrollController,
) {

    private val touchSlop = ViewConfiguration.get(host.context).scaledTouchSlop
    private var viewDragHelper: ViewDragHelper? = null
    private var isSwipeLocked = false
    private var isNestedScrollLocked = false
    private var isNestedScrollDecided = false
    private var touchX = 0f
    private var touchY = 0f

    var isScrollEnabled = true
        set(value) {
            field = value
            updateUserInputEnabled()
        }

    var disabledScrollPages: Set<Int>? = null
        set(value) {
            field = value
            updateUserInputEnabled()
        }

    var isEdgeScrollEnabled: Boolean
        get() = viewDragHelper == null
        set(value) {
            if (value) {
                viewDragHelper = null
                return
            }
            viewDragHelper = ViewDragHelper.create(host, object : ViewDragHelper.Callback() {
                override fun tryCaptureView(child: View, pointerId: Int) = false

                override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
                    isSwipeLocked = true
                    updateUserInputEnabled()
                }
            }).apply {
                setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT or ViewDragHelper.EDGE_LEFT)
            }
        }

    fun onTouchEventBeforeDispatch(event: MotionEvent) {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            smoothScrollController.onTouchDown()
            isSwipeLocked = false
            isNestedScrollLocked = false
            isNestedScrollDecided = false
            touchX = event.x
            touchY = event.y
            updateUserInputEnabled()
        } else if (event.actionMasked == MotionEvent.ACTION_MOVE && !isNestedScrollDecided) {
            val dx = event.x - touchX
            if (abs(dx) > touchSlop && abs(dx) > abs(event.y - touchY)) {
                isNestedScrollDecided = true
                isNestedScrollLocked = hasScrollableChild(
                    recyclerView,
                    touchX - viewPager.left - recyclerView.left,
                    touchY - viewPager.top - recyclerView.top,
                    -sign(dx).toInt(),
                )
                updateUserInputEnabled()
            }
        }
        viewDragHelper?.processTouchEvent(event)
    }

    fun onTouchEventAfterDispatch(event: MotionEvent) {
        if (event.actionMasked == MotionEvent.ACTION_UP ||
            event.actionMasked == MotionEvent.ACTION_CANCEL) {
            isNestedScrollLocked = false
            updateUserInputEnabled()
        }
    }

    fun updateUserInputEnabled() {
        viewPager.isUserInputEnabled = resolveUserInputEnabled()
    }

    private fun resolveUserInputEnabled(): Boolean {
        if (!isScrollEnabled) {
            return false
        }
        if (isSwipeLocked) {
            return false
        }
        if (isNestedScrollLocked) {
            return false
        }
        return disabledScrollPages?.contains(viewPager.currentItem) != true
    }

    private fun hasScrollableChild(parent: ViewGroup, x: Float, y: Float, direction: Int): Boolean {
        for (index in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(index)
            val childX = x + parent.scrollX - child.left
            val childY = y + parent.scrollY - child.top
            val insideWidth = childX >= 0 && childX < child.width
            val insideHeight = childY >= 0 && childY < child.height
            if (!insideWidth || !insideHeight) {
                continue
            }
            if (child is ViewGroup && hasScrollableChild(child, childX, childY, direction)) {
                return true
            }
            if (child.canScrollHorizontally(direction)) {
                return true
            }
        }
        return false
    }
}
