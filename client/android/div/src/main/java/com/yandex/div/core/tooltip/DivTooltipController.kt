package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.R
import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivPathUtils.compactPathList
import com.yandex.div.core.state.DivPathUtils.statePath
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PathFormatException
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div2.DivActionShowTooltip
import com.yandex.div2.DivTooltip
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivTooltipController @Inject constructor(
    private val activeTooltipFactory: ActiveTooltipFactory,
    private val divBinder: Provider<DivBinder>,
) {

    private val activeTooltips = mutableMapOf<TooltipKey, ActiveTooltipComponent>()
    private val buildingTooltips = mutableListOf<TooltipData>()
    private val cancelledTooltips = mutableSetOf<TooltipData>()

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
        if (activeTooltips.containsKey(key) || isBuilding(key)) {
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
        if (activeTooltips.containsKey(key) || isBuilding(key)) {
            return
        }

        var data: TooltipData? = null
        var isCancelled = false
        val activeTooltip = try {
            activeTooltipFactory.create(
                divTooltip = divTooltip,
                anchor = anchor,
                divView = divView,
                scopeId = scopeId,
                multiple = multiple,
                onDataCreated = {
                    data = it
                    buildingTooltips += it
                    it.collectStateVariables()
                },
                onFinished = { finishedTooltip ->
                    if (activeTooltips[key] === finishedTooltip) {
                        activeTooltips.remove(key)
                    }
                },
            )
        } finally {
            data?.let {
                buildingTooltips -= it
                isCancelled = cancelledTooltips.remove(it)
            }
        } ?: return
        if (isCancelled) {
            activeTooltip.dispose()
            return
        }
        activeTooltips[key] = activeTooltip

        val pendingStatePaths = activeTooltip.data.pendingStatePaths.toList()
        activeTooltip.data.pendingStatePaths.clear()
        activeTooltip.switchStates(pendingStatePaths, divView)
        activeTooltip.start(multiple)
    }

    fun hideTooltip(id: String, scopeId: String? = null) {
        cancelBuildingTooltips { it.id == id && it.scopeId == scopeId }
        activeTooltips[TooltipKey(id, scopeId)]?.dismiss()
    }

    fun cancelTooltips(divView: Div2View) {
        cancelBuildingTooltips { it.divView == divView }
        val matchingTooltips = activeTooltips.values
            .filter { it.data.divView == divView }

        matchingTooltips.forEach(ActiveTooltipComponent::dispose)
    }

    fun cancelAllTooltips(): Boolean {
        val hasCancelledBuildingTooltips = cancelBuildingTooltips { true }
        if (activeTooltips.isEmpty()) {
            return hasCancelledBuildingTooltips
        }

        activeTooltips.values.toList().forEach(ActiveTooltipComponent::dispose)
        return true
    }

    private val pendingBuildingTooltips get() = buildingTooltips.filter { it !in cancelledTooltips }

    private fun isBuilding(key: TooltipKey) =
        pendingBuildingTooltips.any { it.id == key.id && it.scopeId == key.scopeId }

    private fun cancelBuildingTooltips(predicate: (TooltipData) -> Boolean): Boolean {
        val tooltips = buildingTooltips.filter(predicate)
        cancelledTooltips += tooltips
        return tooltips.isNotEmpty()
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

    fun captureShownTooltips(): Collection<TooltipData> {
        return activeTooltips.values
            .filter(ActiveTooltipComponent::isWindowShown)
            .map(ActiveTooltipComponent::data)
    }

    fun switchStates(divView: Div2View, paths: List<DivStatePath>): List<DivStatePath> {
        val handled = mutableListOf<DivStatePath>()
        pendingBuildingTooltips.forEach { data ->
            if (data.divView != divView) return@forEach
            val scopedPaths = paths.filter { path -> data.blocks.any { it.path.isAncestorOf(path) } }
            data.pendingStatePaths += scopedPaths
            handled += scopedPaths
        }
        activeTooltips.values.toList().forEach { tooltip ->
            if (tooltip.data.divView == divView) handled += tooltip.switchStates(paths, divView)
        }
        return handled
    }

    @Throws(PathFormatException::class)
    fun resolveStatePath(divView: Div2View, relativePath: String): DivStatePath {
        val states = DivStatePath.parse("0/$relativePath").getStates()
        val paths = (pendingBuildingTooltips + activeTooltips.values.map(ActiveTooltipComponent::data))
            .filter { it.divView == divView }
            .flatMap { it.blocks }
            .flatMap { it.resolveStatePaths(states) }
        return when (paths.size) {
            0 -> throw PathFormatException("State '$relativePath' is not found in shown tooltips")
            1 -> paths.first()
            else -> throw PathFormatException("State '$relativePath' is ambiguous in shown tooltips")
        }
    }

    private fun TooltipData.collectStateVariables() {
        val stateManager = divView.dataComponent.stateManager
        blocks.forEach { stateManager.collectStateVariables(it) }
    }

    private fun ActiveTooltipComponent.switchStates(
        paths: List<DivStatePath>,
        divView: Div2View,
    ): List<DivStatePath> {
        return data.tooltipBlock.switchStates(tooltipView, paths, divView) +
            data.substrateBlock?.switchStates(substrateView, paths, divView).orEmpty()
    }

    private fun DivBlock.resolveStatePaths(states: List<Pair<String, String>>): List<DivStatePath> {
        val statePath = states.fold(path) { p, (divId, stateId) -> p.append(divId, stateId, stateId) }
        val key = statePath.pathToLastState ?: return emptyList()
        val counter = DivStateCounter(key).apply { visit(this@resolveStatePaths) }
        return List(counter.count) { statePath }
    }

    private fun DivBlock.switchStates(view: View?, paths: List<DivStatePath>, divView: Div2View): List<DivStatePath> {
        view ?: return emptyList()
        val scopedPaths = paths.filter { path.isAncestorOf(it) }
        if (scopedPaths.isEmpty()) return scopedPaths

        val binder = divBinder.get()
        val layouts = compactPathList(scopedPaths)
            .mapNotNull { it.pathToLastState }
            .flatMap { view.findStateLayouts(it) }
            .distinct()
        if (layouts.isEmpty()) binder.bind(view, this, divView)
        layouts.forEach { layout -> layout.divBlock?.let { binder.bind(layout, it, divView) } }
        binder.attachIndicators(divView)
        return scopedPaths
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

        fun View.findStateLayouts(key: String): List<DivStateLayout> = when {
            this is DivStateLayout && currentStatePath?.pathToLastState == key -> listOf(this)
            this is ViewGroup -> children.flatMap { it.findStateLayouts(key) }.toList()
            else -> emptyList()
        }
    }
}

private val TooltipData.blocks get() = listOfNotNull(tooltipBlock, substrateBlock)

private data class TooltipKey(
    val id: String,
    val scopeId: String?,
)

private class DivStateCounter(private val statePath: String) : DivTreeVisitor<Unit>() {

    var count = 0
        private set

    override fun defaultVisit(divBlock: DivBlock) = Unit

    override fun visitState(block: DivBlock.State) {
        if (block.path.statePath(block.divValue) == statePath) count++
        super.visitState(block)
    }
}
