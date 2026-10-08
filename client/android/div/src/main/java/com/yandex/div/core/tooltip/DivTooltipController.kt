package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.R
import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div2.DivActionShowTooltip
import com.yandex.div2.DivTooltip
import javax.inject.Inject

@DivScope
internal class DivTooltipController @Inject constructor(
    private val activeTooltipFactory: ActiveTooltipFactory,
) {

    private val activeTooltips = mutableMapOf<TooltipKey, ActiveTooltipComponent>()

    fun showTooltip(
        tooltipId: String,
        divView: Div2View,
        multiple: Boolean = false,
        scopeId: String? = null,
    ) {
        ViewLocator.findSingleViewWithTag(divView, tooltipId, scopeId) { id, _ ->
            findChildWithTooltip(id)?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("Unable to find view for tooltip '$id'"))
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
        scopeId: String?,
    ) {
        val key = TooltipKey(divTooltip.id, scopeId)
        if (activeTooltips.containsKey(key)) {
            return
        }

        anchor.doOnActualLayout {
            tryShowTooltip(
                anchor = anchor,
                divTooltip = divTooltip,
                divView = divView,
                multiple = multiple,
                scopeId = scopeId,
            )
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
        val key = TooltipKey(divTooltip.id, scopeId)
        if (activeTooltips.containsKey(key)) {
            return
        }

        val activeTooltip = activeTooltipFactory.create(
            divTooltip = divTooltip,
            anchor = anchor,
            divView = divView,
            scopeId = scopeId,
            multiple = multiple,
            onFinished = { finishedTooltip ->
                if (activeTooltips[key] === finishedTooltip) {
                    activeTooltips.remove(key)
                }
            },
        ) ?: return
        activeTooltips[key] = activeTooltip
        activeTooltip.start(multiple)
    }

    fun hideTooltip(id: String, scopeId: String? = null) {
        activeTooltips[TooltipKey(id, scopeId)]?.dismiss()
    }

    fun cancelTooltips(divView: Div2View) {
        val matchingTooltips = activeTooltips.values
            .filter { it.data.divView == divView }

        matchingTooltips.forEach(ActiveTooltipComponent::dispose)
    }

    fun cancelAllTooltips(): Boolean {
        if (activeTooltips.isEmpty()) {
            return false
        }

        activeTooltips.values.toList().forEach(ActiveTooltipComponent::dispose)
        return true
    }

    fun handleConfigurationChange(divView: Div2View) {
        val matchingTooltips = activeTooltips.values
            .filter { it.data.divView == divView }

        matchingTooltips.forEach(ActiveTooltipComponent::onConfigurationChanged)
    }

    fun mapTooltip(view: View, tooltips: List<DivTooltip>?) {
        view.setTag(R.id.div_tooltips_tag, tooltips)
    }

    fun clear() {
        cancelAllTooltips()
    }

    fun findViewWithTag(id: String, scopeId: String?): View? {
        activeTooltips.forEach { (key, activeTooltip) ->
            if (key.scopeId == scopeId) {
                activeTooltip.findViewWithTag(id)?.let { return it }
            }
        }
        return null
    }

    fun captureCurrentTooltips(): Collection<TooltipData> {
        return activeTooltips.values.map(ActiveTooltipComponent::data)
    }

    private companion object {

        fun View.findChildWithTooltip(tooltipId: String): Pair<DivTooltip, View>? {
            @Suppress("UNCHECKED_CAST")
            val tooltips = getTag(R.id.div_tooltips_tag) as? List<DivTooltip>
            tooltips?.forEach { tooltip ->
                if (tooltip.id == tooltipId) {
                    return tooltip to this
                }
            }

            if (this !is ViewGroup) {
                return null
            }

            children.forEach { child ->
                child.findChildWithTooltip(tooltipId)?.let { return it }
            }
            return null
        }
    }
}

private data class TooltipKey(
    val id: String,
    val scopeId: String?,
)
