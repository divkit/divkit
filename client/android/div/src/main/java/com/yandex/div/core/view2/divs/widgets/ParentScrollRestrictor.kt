package com.yandex.div.core.view2.divs.widgets

import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.annotation.IntDef
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import kotlin.math.abs

internal class ParentScrollRestrictor(
    @Direction private val restrictedDirection: Int
) : OnInterceptTouchEventListener {

    private var touchSlop = UNDEFINED_TOUCH_SLOP
    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f
    @Direction private var scrollDirection = DIRECTION_NONE

    override fun onInterceptTouchEvent(target: ViewGroup, event: MotionEvent): Boolean {
        val parent = target.parent ?: return false

        if (touchSlop == UNDEFINED_TOUCH_SLOP) {
            touchSlop = ViewConfiguration.get(target.context).scaledTouchSlop
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.x
                initialTouchY = event.y
                scrollDirection = DIRECTION_NONE

                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                if (scrollDirection == DIRECTION_NONE) {
                    scrollDirection = findScrollDirection(event)
                }
                if (scrollDirection == restrictedDirection) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }

    @Direction
    private fun findScrollDirection(event: MotionEvent): Int {
        val dx = abs(initialTouchX - event.x)
        val dy = abs(initialTouchY - event.y)

        if (dx < touchSlop && dy < touchSlop) {
            return DIRECTION_NONE
        }

        return if (dx > dy) DIRECTION_HORIZONTAL else DIRECTION_VERTICAL
    }

    @IntDef(flag = true, value = [DIRECTION_NONE, DIRECTION_VERTICAL, DIRECTION_HORIZONTAL])
    annotation class Direction

    companion object {
        const val DIRECTION_NONE = 0
        const val DIRECTION_HORIZONTAL = 1
        const val DIRECTION_VERTICAL = 1 shl 1

        private const val UNDEFINED_TOUCH_SLOP = -1
    }
}
