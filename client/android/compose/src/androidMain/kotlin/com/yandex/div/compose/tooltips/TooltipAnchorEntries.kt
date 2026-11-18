package com.yandex.div.compose.tooltips

import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.geometry.Rect
import com.yandex.div2.DivTooltip

internal class TooltipAnchorEntries(
    tooltips: List<DivTooltip>,
    private val tooltipStateStorage: TooltipStateStorage,
) : RememberObserver {
    private val entries = tooltips.map { TooltipAnchorEntry(tooltip = it) }

    fun updateBounds(bounds: Rect) {
        entries.forEach { it.bounds.value = bounds }
    }

    override fun onRemembered() {
        entries.forEach(tooltipStateStorage::register)
    }

    override fun onForgotten() {
        entries.forEach(tooltipStateStorage::unregister)
    }

    override fun onAbandoned() = Unit
}
