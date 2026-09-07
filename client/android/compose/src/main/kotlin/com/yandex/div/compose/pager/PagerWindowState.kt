package com.yandex.div.compose.pager

internal class PagerWindowState(
    initialItemWindow: PagerItemWindow,
    initialRealPage: Int,
) {

    private var itemWindow = initialItemWindow
    private var realPage = initialRealPage

    /** Returns a raw page to restore when the finite/cyclic window changes. */
    fun update(
        itemWindow: PagerItemWindow,
        rawPage: Int,
        isPositionAvailable: Boolean,
    ): Int? {
        if (!isPositionAvailable || itemWindow.realItemCount == 0) return null
        if (this.itemWindow != itemWindow) {
            this.itemWindow = itemWindow
            return itemWindow.rawIndex(realPage)
        }

        realPage = itemWindow.realIndex(rawPage)
        return null
    }
}
