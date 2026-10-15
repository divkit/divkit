package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/** Inputs that define where pages are aligned in the viewport. */
internal data class PagerAlignment(
    val snapPosition: SnapPosition,
    val startPadding: Dp,
    val endPadding: Dp,
    val itemSpacing: Dp,
    val pageSize: Dp?,
    val viewportSize: Dp,
) {
    fun matchesLayout(info: LazyListLayoutInfo, density: Density): Boolean = with(density) {
        val paddingsMatch = info.beforeContentPadding == startPadding.roundToPx() &&
            info.afterContentPadding == endPadding.roundToPx()
        paddingsMatch && info.mainAxisItemSpacing == itemSpacing.roundToPx() &&
            info.mainAxisViewportSize == viewportSize.roundToPx()
    }
}

internal class PagerWindowState(
    initialItemWindow: PagerItemWindow,
    initialRealPage: Int,
    initialItemKeys: List<Long>,
    initialAlignment: PagerAlignment,
) {

    private var itemWindow = initialItemWindow
    private var realPage = initialRealPage
    private var cycle = 0
    private var itemKeys = initialItemKeys
    private var alignment = initialAlignment

    /**
     * Returns a raw page to restore when the finite/cyclic window or the [alignment] changes.
     * [rawPage] is measured with the new [alignment], so after its change the page tracked before is restored.
     */
    fun update(
        itemWindow: PagerItemWindow,
        rawPage: Int,
        isPositionAvailable: Boolean,
        itemKeys: List<Long>,
        alignment: PagerAlignment,
    ): Int? {
        if (!isPositionAvailable || itemWindow.realItemCount == 0) return null
        val alignmentChanged = this.alignment != alignment
        this.alignment = alignment
        if (this.itemWindow != itemWindow || this.itemKeys != itemKeys) {
            val previousKey = this.itemKeys.getOrNull(realPage)
            realPage = itemKeys.indexOf(previousKey).takeIf { it >= 0 }
                ?: realPage.coerceIn(0, itemWindow.realItemCount - 1)
            this.itemWindow = itemWindow
            this.itemKeys = itemKeys
            val baseRawPage = itemWindow.rawIndex(realPage)
            val minCycle = -baseRawPage / itemWindow.realItemCount
            val maxCycle = (itemWindow.itemCount - 1 - baseRawPage) / itemWindow.realItemCount
            cycle = cycle.coerceIn(minCycle, maxCycle)
            return baseRawPage + cycle * itemWindow.realItemCount
        }
        if (alignmentChanged) {
            return itemWindow.rawIndex(realPage) + cycle * itemWindow.realItemCount
        }

        realPage = itemWindow.realIndex(rawPage)
        cycle = (rawPage - itemWindow.edgeItemCount).floorDiv(itemWindow.realItemCount)
        return null
    }
}
