package com.yandex.div.core.tooltip

import android.graphics.Rect
import android.view.MotionEvent
import com.yandex.div.core.util.SafePopupWindow

internal class TouchTranslationCoordinator(
    private val touchTranslator: TouchTranslator,
    private val popup: SafePopupWindow,
    private val tooltipContainer: DivTooltipContainer,
    private val closeByTapOutside: Boolean,
) {
    private val hitRect = Rect()
    private var dismissOnGestureEnd = false

    fun onTouchDownDiscardedAtRoot(event: MotionEvent) {
        dismissOnGestureEnd = closeByTapOutside || isTooltipHit(event)
        touchTranslator.startGestureTranslation(event)
    }

    fun onTooltipMotionEvent(event: MotionEvent) {
        val isTranslating = touchTranslator.canTranslateGesture
        touchTranslator.onTooltipMotionEvent(event)

        if (dismissOnGestureEnd && isTranslating && !touchTranslator.canTranslateGesture) {
            // Looks like gesture has ended time to dismiss tooltip.
            popup.dismiss()
        }
    }

    private fun isTooltipHit(event: MotionEvent): Boolean {
        val tooltipView = tooltipContainer.tooltipView ?: return false
        tooltipView.getHitRect(hitRect)
        return hitRect.contains(event.x.toInt(), event.y.toInt())
    }
}
