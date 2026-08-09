package com.yandex.div.core.tooltip

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.core.view.children
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.internal.core.DivBlock
import com.yandex.div2.DivActionShowTooltip
import com.yandex.div2.DivTooltip
import javax.inject.Inject

@DivScope
internal class DivTooltipController @Inject constructor(
    private val tooltipRestrictor: DivTooltipRestrictor,
    private val divPreloader: DivPreloader,
    private val viewController: DivTooltipViewController,
    private val visibilityController: DivTooltipVisibilityController,
) {

    private val tooltips = mutableListOf<TooltipData>()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    fun showTooltip(tooltipId: String, divView: Div2View, multiple: Boolean = false, scopeId: String? = null) {
        ViewLocator.findSingleViewWithTag(divView, tooltipId, scopeId) { tooltipId, _ ->
            findChildWithTooltip(tooltipId)?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("Unable to find view for tooltip '$tooltipId'"))
        }.onSuccess { (divTooltip, anchor) ->
            showTooltip(divTooltip, anchor, divView, multiple, scopeId)
        }.onFailure {
            divView.logActionError(DivActionShowTooltip.TYPE, it)
        }
    }

    private fun showTooltip(
        divTooltip: DivTooltip,
        anchor: View,
        divView: Div2View,
        multiple: Boolean,
        scopeId: String?
    ) {
        val containsTooltip = tooltips.any { it.id == divTooltip.id && it.scopeId == scopeId }
        if (containsTooltip) {
            return
        }
        anchor.doOnActualLayout {
            tryShowTooltip(anchor, divTooltip, divView, multiple, scopeId)
        }
        if (!anchor.isActuallyLaidOut && !anchor.isLayoutRequested) {
            anchor.requestLayout()
        }
    }

    private fun tryShowTooltip(
        anchor: View,
        divTooltip: DivTooltip,
        divView: Div2View,
        multiple: Boolean,
        scopeId: String?,
    ) {
        val anchorBlock = anchor.divBlock ?: return
        if (!tooltipRestrictor.canShowTooltip(divView, anchor, divTooltip, multiple, scopeId)) return
        tooltips += createTooltipDataAndTryShow(anchor, divTooltip, anchorBlock, divView, multiple, scopeId)
    }

    private fun createTooltipDataAndTryShow(
        anchor: View,
        divTooltip: DivTooltip,
        anchorBlock: DivBlock,
        divView: Div2View,
        multiple: Boolean,
        scopeId: String?,
    ): TooltipData {
        val data = TooltipData(divTooltip.id, scopeId, divTooltip, anchor, anchorBlock, divView)

        viewController.createPopupWindow(data, { visibilityController.hideTooltip(data) }) {
            tooltips.remove(data)
            visibilityController.onDismiss(data)
            tooltipRestrictor.tooltipShownCallback?.onDivTooltipDismissed(divView, anchor, divTooltip)
        }

        data.ticket = divPreloader.preload(divTooltip.div, data.tooltipBlock.expressionResolver) { hasFailures ->
            if (canShowTooltip(data, hasFailures, multiple)) {
                visibilityController.showTooltip(data) { onPopupShown(data) }
            } else {
                tooltips.remove(data)
            }
        }
        return data
    }

    private fun canShowTooltip(
        tooltipData: TooltipData,
        hasFailures: Boolean,
        multiple: Boolean,
    ): Boolean {
        return when {
            hasFailures -> false
            tooltipData.dismissed -> false
            !tooltipData.anchor.isAttachedToWindow -> false
            else -> tooltipRestrictor.canShowTooltip(
                tooltipData.divView,
                tooltipData.anchor,
                tooltipData.divTooltip,
                multiple,
                tooltipData.scopeId,
            )
        }
    }

    private fun onPopupShown(tooltipData: TooltipData) {
        viewController.onPopupShown(tooltipData)
        tooltipRestrictor.tooltipShownCallback
            ?.onDivTooltipShown(tooltipData.divView, tooltipData.anchor, tooltipData.divTooltip)

        val duration = tooltipData.divTooltip.duration.evaluate(tooltipData.anchorResolver)
        if (duration == 0L) return

        mainThreadHandler.postDelayed(duration) { visibilityController.hideTooltip(tooltipData) }
    }

    fun hideTooltip(id: String, scopeId: String? = null) {
        val tooltip = tooltips.find { it.id == id && it.scopeId == scopeId } ?: return
        visibilityController.hideTooltip(tooltip)
    }

    fun cancelTooltips(divView: Div2View) {
        tooltips.toList().forEach { tooltip ->
            if (tooltip.divView != divView) return@forEach
            visibilityController.dismissTooltip(tooltip)?.let {
                tooltips.remove(it)
            }
        }
    }

    fun cancelAllTooltips(): Boolean {
        if (tooltips.isEmpty()) {
            return false
        }

        tooltips.toList().forEach { visibilityController.dismissTooltip(it) }
        tooltips.clear()
        return true
    }

    fun handleConfigurationChange(divView: Div2View) {
        tooltips.toList().forEach { tooltip ->
            viewController.startAnchorPositionTracking(tooltip, divView, mainThreadHandler)
        }
    }

    fun mapTooltip(view: View, tooltips: List<DivTooltip>?) {
        view.setTag(R.id.div_tooltips_tag, tooltips)
    }

    fun clear() {
        tooltips.toList().forEach {
            it.stopAnchorTracking()
            it.popupWindow?.dismiss()
            it.ticket?.cancel()
        }
        tooltips.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    fun findViewWithTag(id: String, scopeId: String?): View? {
        tooltips.forEach { tooltip ->
            if (tooltip.scopeId != scopeId) return@forEach
            tooltip.popupWindow?.contentView?.findViewWithTag<View>(id)?.let { return it }
        }
        return null
    }

    fun captureCurrentTooltips(): Collection<TooltipData> = tooltips

    private companion object {

        fun View.findChildWithTooltip(tooltipId: String): Pair<DivTooltip, View>? {
            @Suppress("UNCHECKED_CAST")
            val tooltips = getTag(R.id.div_tooltips_tag) as? List<DivTooltip>
            tooltips?.forEach {
                if (it.id == tooltipId) return it to this
            }

            if (this !is ViewGroup) return null

            children.forEach { child ->
                child.findChildWithTooltip(tooltipId)?.let { return it }
            }
            return null
        }
    }
}
