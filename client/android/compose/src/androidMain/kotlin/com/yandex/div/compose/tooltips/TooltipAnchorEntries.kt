package com.yandex.div.compose.tooltips

import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.geometry.Rect
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div2.DivTooltip

internal class TooltipAnchorEntries(
    tooltips: List<DivTooltip>,
    localComponent: DivLocalComponent,
    private val tooltipStateStorage: TooltipStateStorage,
) : RememberObserver {
    private val entries = tooltips.map { TooltipAnchorEntry(it, localComponent) }

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
