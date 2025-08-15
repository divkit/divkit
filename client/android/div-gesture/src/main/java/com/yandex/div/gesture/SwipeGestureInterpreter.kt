package com.yandex.div.gesture

import android.graphics.PointF
import android.view.MotionEvent
import kotlin.math.abs

/**
 * Interprets touch events into swipe gesture.
 */
internal class SwipeGestureInterpreter(
    private val touchSlop: Int,
    private val gestureConsumer: SwipeGestureConsumer,
) {

    private val gestureTracker: SwipeGestureTracker = SwipeGestureTracker()

    /**
     * Consumes touch event.
     *
     * @return `true` if event is consumed, `false` otherwise.
     */
    fun consumeTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                gestureTracker.reset(event)
                gestureConsumer.onTouchStart(gestureTracker)
            }
            MotionEvent.ACTION_MOVE -> {
                gestureTracker.trackMovement(event)
                gestureConsumer.onSwipe(gestureTracker)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                gestureConsumer.onTouchEnd(gestureTracker)
            }
            else -> false
        }
    }

    private inner class SwipeGestureTracker : SwipeGestureInfo {
        override var isSwipingLeft = false
        override var isSwipingRight = false
        override var isSwipingUp = false
        override var isSwipingDown = false

        private val totalMove = PointF()
        private val lastMove = PointF()
        private var lastX = 0f
        private var lastY = 0f

        override var isTouchSlopBreached = false
            get() {
                field = field || abs(totalMove.y) > touchSlop || abs(totalMove.x) > touchSlop
                return field
            }

        private val isSwiping: Boolean
            get() = isSwipingLeft || isSwipingRight || isSwipingUp || isSwipingDown

        fun reset(event: MotionEvent) {
            lastMove.set(0f, 0f)
            totalMove.set(0f, 0f)
            lastX = event.rawX
            lastY = event.rawY

            isTouchSlopBreached = false
            isSwipingLeft = false
            isSwipingRight = false
            isSwipingUp = false
            isSwipingDown = false
        }

        fun trackMovement(event: MotionEvent) {
            lastMove.set(lastX - event.rawX, lastY - event.rawY)
            totalMove.x += lastMove.x
            totalMove.y += lastMove.y
            lastY = event.rawY
            lastX = event.rawX

            if (!isTouchSlopBreached || isSwiping) {
                return
            }

            if (abs(totalMove.x) > abs(totalMove.y)) {
                if (totalMove.x > 0) {
                    isSwipingLeft = true
                } else {
                    isSwipingRight = true
                }
            } else if (abs(totalMove.y) > abs(totalMove.x)) {
                if (totalMove.y > 0) {
                    isSwipingUp = true
                } else {
                    isSwipingDown = true
                }
            }
        }
    }
}
