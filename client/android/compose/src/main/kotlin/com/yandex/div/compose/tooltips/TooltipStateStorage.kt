package com.yandex.div.compose.tooltips

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.geometry.Rect
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivTooltip
import javax.inject.Inject

/**
 * Tracks tooltips currently requested to be shown for a given [com.yandex.div.compose.DivView].
 *
 * The state is backed by a [SnapshotStateMap] so that composables observing
 * [isShown] or [shownTooltipIds] are automatically recomposed when the set of
 * active tooltips changes.
 */
@DivViewScope
internal class TooltipStateStorage @Inject constructor() {
    private val anchors = mutableStateMapOf<String, TooltipAnchorEntry>()
    private val shownTooltips = mutableStateSetOf<String>()
    private val bringToTopEntries = mutableStateListOf<BringToTopEntry>()
    private val bringToTopIds by derivedStateOf(structuralEqualityPolicy()) {
        anchors.values.mapNotNull { entry ->
            entry.tooltip.bringToTopId.takeIf { entry.tooltip.substrateDiv != null }
        }.toSet()
    }

    fun show(id: String) {
        shownTooltips.add(id)
    }

    fun hide(id: String) {
        shownTooltips.remove(id)
    }

    fun isShown(id: String): Boolean {
        return shownTooltips.contains(id)
    }

    fun shownTooltipIds(): List<String> {
        return shownTooltips.toList()
    }

    fun register(entry: TooltipAnchorEntry) {
        anchors[entry.tooltip.id] = entry
    }

    fun unregister(entry: TooltipAnchorEntry) {
        anchors.remove(entry.tooltip.id)
    }

    fun getAnchor(id: String): TooltipAnchorEntry? {
        return anchors[id]
    }

    fun isBringToTopTarget(id: String): Boolean = id in bringToTopIds

    fun register(entry: BringToTopEntry) {
        bringToTopEntries.add(entry)
    }

    fun unregister(entry: BringToTopEntry) {
        bringToTopEntries.remove(entry)
    }

    fun getBringToTopEntry(id: String): BringToTopEntry? =
        bringToTopEntries.firstOrNull { it.id == id }
}

internal data class TooltipAnchorEntry(
    val tooltip: DivTooltip,
    val bounds: MutableState<Rect?> = mutableStateOf(null),
)
