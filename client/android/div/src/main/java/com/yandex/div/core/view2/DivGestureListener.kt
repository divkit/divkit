package com.yandex.div.core.view2

import android.view.GestureDetector
import android.view.MotionEvent

internal class DivGestureListener(
    private val awaitLongClick: Boolean
) : GestureDetector.SimpleOnGestureListener() {
    var onSingleTapListener: (() -> Unit)? = null
    var onDoubleTapListener: (() -> Unit)? = null

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (onDoubleTapListener != null && onSingleTapListener != null) {
            onSingleTapListener?.invoke()
            return true
        }

        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        if (onDoubleTapListener == null && onSingleTapListener != null) {
            onSingleTapListener?.invoke()
            return true
        }

        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleTapListener?.apply {
            invoke()
            return true
        }

        return false
    }

    override fun onDown(e: MotionEvent): Boolean {
        return !awaitLongClick && (onDoubleTapListener != null || onSingleTapListener != null)
    }
}
