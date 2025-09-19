package com.yandex.div.core.tooltip

import android.view.MotionEvent
import com.yandex.div.core.util.SafePopupWindow

internal class TouchTranslationCoordinator(
    private val touchTranslator: TouchTranslator,
    private val popup: SafePopupWindow,
) {
    fun onTouchDownDiscardedAtRoot(event: MotionEvent) {
        touchTranslator.startGestureTranslation(event)
    }

    fun onTooltipMotionEvent(event: MotionEvent) {
        val isTranslating = touchTranslator.canTranslateGesture
        touchTranslator.onTooltipMotionEvent(event)

        if (isTranslating && !touchTranslator.canTranslateGesture) {
            // Looks like gesture has ended time to dismiss tooltip.
            popup.dismiss()
        }
    }
}
