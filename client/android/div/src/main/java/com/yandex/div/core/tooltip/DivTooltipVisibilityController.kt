package com.yandex.div.core.tooltip

import android.view.Gravity
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div2.Div
import javax.inject.Inject

@DivScope
internal class DivTooltipVisibilityController @Inject constructor(
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val animationsEnabledController: DivAnimationsEnabledController,
) {

    fun showTooltip(tooltipData: TooltipData, onShown: () -> Unit) {
        tooltipData.tooltipContainer?.doOnActualLayout {
            onShown()
            startVisibilityTracking(tooltipData)
        }
        tryShowTooltip(tooltipData)
    }

    private fun tryShowTooltip(data: TooltipData) {
        val popupWindow = data.popupWindow ?: return
        val tooltipContainer = data.tooltipContainer ?: return
        val tooltipView = tooltipContainer.tooltipView ?: return

        if (animationsEnabledController.isEnabled()) {
            val resolver = data.bindingContext.expressionResolver
            tooltipContainer.substrateView?.let { animateEnter(data.divTooltip, resolver, tooltipView, it) }
                ?: popupWindow.setupAnimation(data.divTooltip, resolver)
        }

        popupWindow.showAtLocation(data.anchor, Gravity.NO_GRAVITY, 0, 0)
    }

    fun hideTooltip(data: TooltipData) {
        val popupWindow = data.popupWindow ?: return
        val tooltipContainer = data.tooltipContainer

        val substrateView = tooltipContainer?.substrateView
        val tooltipView = tooltipContainer?.tooltipView
        if (substrateView == null || tooltipView == null) {
            if (!animationsEnabledController.isEnabled()) {
                popupWindow.clearAnimation()
            }
            popupWindow.dismiss()
            return
        }

        substrateView.clearAnimation()
        tooltipView.clearAnimation()
        if (!animationsEnabledController.isEnabled()) {
            dismissPopup(popupWindow)
            return
        }

        animateExit(data.divTooltip, data.bindingContext.expressionResolver, tooltipView, substrateView) {
            dismissPopup(popupWindow)
        }
    }

    private fun dismissPopup(popupWindow: SafePopupWindow) {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    fun dismissTooltip(tooltip: TooltipData) = tooltip.dismiss()

    private fun TooltipData.dismiss(): TooltipData? {
        val popup = popupWindow ?: return null
        dismissed = true
        stopAnchorTracking()
        ticket?.cancel()

        return if (popup.isShowing) {
            popup.clearAnimation()
            popup.dismiss()
            null
        } else {
            stopVisibilityTracking(bindingContext, divTooltip.div)
            this
        }
    }

    fun onDismiss(data: TooltipData) {
        stopVisibilityTracking(data.bindingContext, data.divTooltip.div)
        val tooltipContainer = data.tooltipContainer ?: return
        val div = divVisibilityActionTracker.getDivWithWaitingDisappearActions()[tooltipContainer] ?: return
        divVisibilityActionTracker.trackDetachedView(
            tooltipContainer,
            div,
            data.bindingContext.expressionResolver,
            data.bindingContext.divView
        )
    }

    private fun startVisibilityTracking(tooltipData: TooltipData) {
        val context = tooltipData.bindingContext
        val div = tooltipData.divTooltip.div
        stopVisibilityTracking(context, div)
        divVisibilityActionTracker.trackVisibilityActionsOf(
            context.divView,
            context.expressionResolver,
            tooltipData.tooltipContainer,
            div,
        )
    }

    private fun stopVisibilityTracking(context: BindingContext, div: Div) =
        divVisibilityActionTracker.trackVisibilityActionsOf(context.divView, context.expressionResolver, null, div)
}
