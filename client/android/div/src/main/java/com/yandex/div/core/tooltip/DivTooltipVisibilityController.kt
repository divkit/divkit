package com.yandex.div.core.tooltip

import android.view.Gravity
import android.view.View
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.core.DivBlock
import javax.inject.Inject

@DivScope
internal class DivTooltipVisibilityController @Inject constructor(
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val animationsEnabledController: DivAnimationsEnabledController,
) {

    fun showTooltip(tooltipData: TooltipData, onShown: () -> Unit) {
        tooltipData.tooltipContainer?.doOnActualLayout {
            onShown()
            tooltipData.startVisibilityTracking()
        }
        tryShowTooltip(tooltipData)
    }

    private fun tryShowTooltip(data: TooltipData) {
        val popupWindow = data.popupWindow ?: return
        val tooltipContainer = data.tooltipContainer ?: return
        val tooltipView = tooltipContainer.tooltipView ?: return

        if (animationsEnabledController.isEnabled()) {
            tooltipContainer.substrateView?.let { animateEnter(data.divTooltip, data.anchorResolver, tooltipView, it) }
                ?: popupWindow.setupAnimation(data.divTooltip, data.anchorResolver)
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

        animateExit(data.divTooltip, data.anchorResolver, tooltipView, substrateView) {
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
            stopVisibilityTracking()
            this
        }
    }

    fun onDismiss(data: TooltipData) {
        data.stopVisibilityTracking()
        val tooltipContainer = data.tooltipContainer ?: return
        val tooltipView = tooltipContainer.tooltipView ?: return

        val div = divVisibilityActionTracker.getDivWithWaitingDisappearActions()[tooltipView] ?: return
        divVisibilityActionTracker
            .trackDetachedView(tooltipView, div, data.tooltipBlock.expressionResolver, data.divView)

        val substrateView = tooltipContainer.substrateView ?: return
        val substrateDiv = divVisibilityActionTracker.getDivWithWaitingDisappearActions()[substrateView] ?: return
        val substrateResolver = data.substrateBlock?.expressionResolver ?: return
        divVisibilityActionTracker.trackDetachedView(substrateView, substrateDiv, substrateResolver, data.divView)
    }

    private fun TooltipData.startVisibilityTracking() {
        stopVisibilityTracking()
        val tooltipContainer = tooltipContainer ?: return
        tooltipBlock.startVisibilityTracking(tooltipContainer.tooltipView, divView)
        substrateBlock?.startVisibilityTracking(tooltipContainer.substrateView, divView)
    }

    private fun DivBlock.startVisibilityTracking(view: View?, divView: Div2View) =
        divVisibilityActionTracker.trackVisibilityActionsOf(divView, expressionResolver, view, div)

    private fun TooltipData.stopVisibilityTracking() {
        tooltipBlock.stopVisibilityTracking(divView)
        substrateBlock?.stopVisibilityTracking(divView)
    }

    private fun DivBlock.stopVisibilityTracking(divView: Div2View) =
        divVisibilityActionTracker.trackVisibilityActionsOf(divView, expressionResolver, null, div)
}
