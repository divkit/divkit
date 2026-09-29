package com.yandex.div.compose.pager

internal class PagerWindowState(
    initialItemWindow: PagerItemWindow,
    initialRealPage: Int,
    initialItemKeys: List<Long>,
) {

    private var itemWindow = initialItemWindow
    private var realPage = initialRealPage
    private var cycle = 0
    private var itemKeys = initialItemKeys

    /** Returns a raw page to restore when the finite/cyclic window changes. */
    fun update(
        itemWindow: PagerItemWindow,
        rawPage: Int,
        isPositionAvailable: Boolean,
        itemKeys: List<Long>,
    ): Int? {
        if (!isPositionAvailable || itemWindow.realItemCount == 0) return null
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

        realPage = itemWindow.realIndex(rawPage)
        cycle = (rawPage - itemWindow.edgeItemCount).floorDiv(itemWindow.realItemCount)
        return null
    }
}
