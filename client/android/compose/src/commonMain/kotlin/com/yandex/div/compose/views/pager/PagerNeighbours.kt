package com.yandex.div.compose.views.pager

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo

/** Visible items plus the adjacent pages outside the viewport, when their sizes are known. */
internal fun LazyListLayoutInfo.itemsWithNeighbours(neighbourSize: (Int) -> Int?): List<LazyListItemInfo> {
    val first = visibleItemsInfo.firstOrNull() ?: return visibleItemsInfo
    val last = visibleItemsInfo.last()
    fun neighbour(index: Int, offset: (size: Int) -> Int): LazyListItemInfo? {
        if (index !in 0 until totalItemsCount) return null
        val size = neighbourSize(index) ?: return null
        return NeighbourPageInfo(index, offset(size), size)
    }

    val before = neighbour(first.index - 1) { size -> first.offset - mainAxisItemSpacing - size }
    val after = neighbour(last.index + 1) { last.offset + last.size + mainAxisItemSpacing }
    return listOfNotNull(before) + visibleItemsInfo + listOfNotNull(after)
}

internal class NeighbourPageInfo(
    override val index: Int,
    override val offset: Int,
    override val size: Int,
) : LazyListItemInfo {
    override val key: Any get() = index
    override val contentType: Any? get() = null
}
