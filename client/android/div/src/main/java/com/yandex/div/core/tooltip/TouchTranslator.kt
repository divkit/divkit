package com.yandex.div.core.tooltip

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import com.yandex.div.core.view2.BindingContext

/**
 * Passes motions from one window to another one that is related to binding context.
 */
internal class TouchTranslator(private val targetWindowView: View) {
    internal var canTranslateGesture = false
        private set
    private val tmpLoc = IntArray(2)

    private fun dispatchMotionEvent(event: MotionEvent): Boolean {
        val targetDecor: View = targetWindowView.peekDecorView() ?: return false

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

    fun onTooltipMotionEvent(e: MotionEvent): Boolean {
        if (!canTranslateGesture) {
            return false
        }

        val handled = dispatchMotionEvent(e)

        if (e.actionMasked == MotionEvent.ACTION_UP || e.actionMasked == MotionEvent.ACTION_CANCEL) {
            canTranslateGesture = false
        }
        return handled
    }

    fun startGestureTranslation(e: MotionEvent) {
        canTranslateGesture = true
        dispatchMotionEvent(e)
    }
}

private fun View.peekDecorView(): View? {
    (this.context as? Activity)?.let {
        return it.window.decorView
    }

    return (parent as? View)?.peekDecorView()
}
