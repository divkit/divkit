package com.yandex.div.internal.tooltip

import android.view.MotionEvent
import android.view.View
import com.yandex.div.core.annotations.InternalApi

@InternalApi
class TouchEventDispatcher(private val targetView: () -> View?) {
    private val tmpLoc = IntArray(2)

    fun dispatch(event: MotionEvent): Boolean {
        val targetDecor = targetView() ?: return false

        targetDecor.getLocationOnScreen(tmpLoc)
        val targetOriginX = tmpLoc[0].toFloat()
        val targetOriginY = tmpLoc[1].toFloat()

        val sourceOriginX = event.rawX - event.x
        val sourceOriginY = event.rawY - event.y

        val dx = sourceOriginX - targetOriginX
        val dy = sourceOriginY - targetOriginY

        val copy = MotionEvent.obtain(event)
        try {
            copy.offsetLocation(dx, dy)
            return targetDecor.dispatchTouchEvent(copy)
        } finally {
            copy.recycle()
        }
    }
}
