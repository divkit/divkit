package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.abs

internal class DivPagerState(
    val pageCount: Int,
    private val listState: LazyListState?,
    private val snapPosition: SnapPosition,
    private val initialPage: Int = 0,
    val infiniteScroll: Boolean = false,
) {

    private val itemWindow = if (infiniteScroll && pageCount > 0) {
        PagerItemWindow.virtuallyUnbounded(pageCount)
    } else {
        null
    }

    val currentPage: Int
        get() {
            val listState = listState ?: return initialPage
            if (listState.layoutInfo.visibleItemsInfo.isEmpty()) return initialPage
            val rawPage = listState.pagerPosition(snapPosition).first
            return itemWindow?.realIndex(rawPage) ?: rawPage
        }

    val currentPageOffsetFraction: Float
        get() {
            val listState = listState ?: return 0f
            if (listState.layoutInfo.visibleItemsInfo.isEmpty()) return 0f
            return listState.pagerPosition(snapPosition).second
        }
}

internal fun LazyListState.pagerPosition(snapPosition: SnapPosition): Pair<Int, Float> {
    val info = layoutInfo
    val snapped = info.snappedItem(snapPosition) ?: return firstVisibleItemIndex to 0f
    if (snapped.size <= 0) {
        return snapped.index to 0f
    }

    return snapped.index to info.snapOffset(snapped, snapPosition) / snapped.size
}

/** Indices and main axis sizes of the visible items. */
internal fun LazyListLayoutInfo.visibleItemSizes(): List<Pair<Int, Int>> =
    visibleItemsInfo.map { it.index to it.size }

/** A page of a laid out pager and the main axis sizes of the visible pages, empty while the list scrolls. */
internal data class PagerLayoutSnapshot(val page: Int?, val itemSizes: List<Pair<Int, Int>>) {
    /** Whether a page visible in both layouts changed its size; scrolls alone never do. */
    fun isResizeOf(previous: PagerLayoutSnapshot): Boolean =
        itemSizes.any { (index, size) -> previous.itemSizes.any { it.first == index && it.second != size } }
}

/** The visible item closest to its snap position. */
internal fun LazyListLayoutInfo.snappedItem(snapPosition: SnapPosition): LazyListItemInfo? =
    visibleItemsInfo.minByOrNull { abs(snapOffset(it, snapPosition)) }

internal val LazyListLayoutInfo.mainAxisViewportSize: Int
    get() = if (orientation == Orientation.Vertical) viewportSize.height else viewportSize.width

/** The scroll distance that brings [item] to its snap position, as LazyList's SnapLayoutInfoProvider computes it. */
internal fun LazyListLayoutInfo.snapOffset(item: LazyListItemInfo, snapPosition: SnapPosition): Float {
    val desiredOffset = snapPosition.position(
        layoutSize = mainAxisViewportSize,
        itemSize = item.size,
        beforeContentPadding = beforeContentPadding,
        afterContentPadding = afterContentPadding,
        itemIndex = item.index,
        itemCount = totalItemsCount,
    )
    return item.offset - desiredOffset.toFloat()
}
