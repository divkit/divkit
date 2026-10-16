package com.yandex.div.core.tooltip

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import com.yandex.div.internal.tooltip.TouchEventDispatcher

/**
 * Passes motions from one window to another one that is related to binding context.
 */
internal class TouchTranslator(private val targetWindowView: View) {
    internal var canTranslateGesture = false
        private set
    private val dispatcher = TouchEventDispatcher { targetWindowView.peekDecorView() }

    fun onTooltipMotionEvent(e: MotionEvent): Boolean {
        if (!canTranslateGesture) {
            return false
        }

        val handled = dispatcher.dispatch(e)

        if (e.actionMasked == MotionEvent.ACTION_UP || e.actionMasked == MotionEvent.ACTION_CANCEL) {
            canTranslateGesture = false
        }
        return handled
    }

    fun startGestureTranslation(e: MotionEvent) {
        canTranslateGesture = true
        dispatcher.dispatch(e)
    }
}

private fun View.peekDecorView(): View? {
    (this.context as? Activity)?.let {
        return it.window.decorView
    }

    return (parent as? View)?.peekDecorView()
}
