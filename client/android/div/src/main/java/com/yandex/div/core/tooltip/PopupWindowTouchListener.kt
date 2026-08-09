package com.yandex.div.core.tooltip

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

internal class PopupWindowTouchListener(
    private val tooltipContainer: DivTooltipContainer,
    private val isModal: Boolean,
    private val shouldDismissByOutsideTouch: Boolean,
    private val tapOutsideActions: List<DivAction>?,
    private val expressionResolver: ExpressionResolver,
    private val divView: Div2View,
    private val touchTranslationCoordinator: TouchTranslationCoordinator,
    private val handleSubstrateClick: Boolean,
    private val onTouchOutside: () -> Unit,
) : View.OnTouchListener {

    private val hitRect = Rect()

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        touchTranslationCoordinator.onTooltipMotionEvent(event)

        if (handleSubstrateClick) {
            tooltipContainer.substrateView?.getHitRect(hitRect)
        } else {
            tooltipContainer.tooltipView?.getHitRect(hitRect)
        }

        if (hitRect.contains(event.x.toInt(), event.y.toInt())) return false

        if (event.action != MotionEvent.ACTION_UP) return isModal

        tapOutsideActions?.filter {
            it.isEnabled.evaluate(expressionResolver)
        }?.forEach { action ->
            divView.div2Component.actionHandler
                .handleActionWithReason(action, divView, expressionResolver, DivActionHandler.DivActionReason.CLICK)
        }

        if (shouldDismissByOutsideTouch) {
            onTouchOutside()
        }

        return isModal
    }
}
