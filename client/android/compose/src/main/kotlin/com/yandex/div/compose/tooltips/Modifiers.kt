package com.yandex.div.compose.tooltips

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div2.DivTooltip

@Composable
internal fun Modifier.tooltipAnchors(tooltips: List<DivTooltip>?): Modifier {
    if (tooltips.isNullOrEmpty()) {
        return this
    }

    val tooltipStateStorage = LocalDivViewContext.current.component.tooltipStateStorage

    val entries = remember(tooltips) {
        tooltips.map { TooltipAnchorEntry(tooltip = it) }
    }

    DisposableEffect(entries) {
        entries.forEach(tooltipStateStorage::register)
        onDispose {
            entries.forEach(tooltipStateStorage::unregister)
        }
    }

    return onGloballyPositioned { coordinates ->
        val bounds = coordinates.boundsInWindow()
        entries.forEach { it.bounds.value = bounds }
    }
}
