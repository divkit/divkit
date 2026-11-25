package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.IntState
import com.yandex.div.render.common.pager.PagerItemWindow
import kotlin.math.abs

internal class DivPagerState(
    val pageCount: Int,
    private val listState: LazyListState?,
    private val snapPosition: SnapPosition,
    private val initialPage: Int = 0,
    val infiniteScroll: Boolean = false,
    private val settledPage: IntState? = null,
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
            val rawPage = listState.pagerPosition(snapPosition, settledPage?.intValue ?: initialPage).first
            return itemWindow?.realIndex(rawPage) ?: rawPage
        }

    val currentPageOffsetFraction: Float
        get() {
            val listState = listState ?: return 0f
            if (listState.layoutInfo.visibleItemsInfo.isEmpty()) return 0f
            return listState.pagerPosition(snapPosition, settledPage?.intValue ?: initialPage).second
        }
}

internal fun LazyListState.pagerPosition(snapPosition: SnapPosition, settledPage: Int): Pair<Int, Float> {
    val info = layoutInfo
    val items = info.visibleItemsInfo
    val snapped = info.snappedItem(snapPosition, settledPage) ?: return firstVisibleItemIndex to 0f
    val offset = info.reachableSnapOffset(snapped, snapPosition)
    // The page the list scrolls towards: its snap position is one page away, however close an edge brings it.
    val adjacentIndex = if (offset < 0f) snapped.index + 1 else snapped.index - 1
    val adjacent = items.firstOrNull { it.index == adjacentIndex }
    val stride = if (adjacent != null) {
        abs(info.reachableSnapOffset(adjacent, snapPosition) - offset)
    } else {
        (snapped.size + info.mainAxisItemSpacing).toFloat()
    }
    return snapped.index to if (stride > 0f) offset / stride else 0f
}

/**
 * The page a laid out pager stands on and the number of page resizes its layouts have seen,
 * which is null while the list scrolls.
 */
internal data class PagerLayoutSnapshot(val page: Int?, val resizeCount: Int?) {
    /** Whether a page changed its size between two layouts in which the list stood still. */
    fun isResizeOf(previous: PagerLayoutSnapshot): Boolean =
        resizeCount != null && previous.resizeCount != null && resizeCount != previous.resizeCount
}

/**
 * The item the list stands on: the one closest to its reachable snap position. Of the items that share that
 * position at an edge of the list, it is the one closest by index to [settledPage].
 */
internal fun LazyListLayoutInfo.snappedItem(
    snapPosition: SnapPosition,
    settledPage: Int,
    items: List<LazyListItemInfo> = visibleItemsInfo,
): LazyListItemInfo? = closestItem(snapPosition, settledPage, items, offset = 0f)

/**
 * The item of [items] whose reachable snap position is closest to the scroll distance [offset]; of the items that
 * share it, the one closest by index to [page].
 */
internal fun LazyListLayoutInfo.closestItem(
    snapPosition: SnapPosition,
    page: Int,
    items: List<LazyListItemInfo>,
    offset: Float,
): LazyListItemInfo? {
    val minOffset = minScrollOffset(items)
    val maxOffset = maxScrollOffset(items)
    var closest: LazyListItemInfo? = null
    var closestDistance = Float.POSITIVE_INFINITY
    for (index in items.indices) {
        val item = items[index]
        val distance = abs(snapOffset(item, snapPosition).coerceIn(minOffset, maxOffset) - offset)
        val current = closest
        val isCloser = current == null || distance < closestDistance ||
            distance == closestDistance && abs(item.index - page) < abs(current.index - page)
        if (isCloser) {
            closest = item
            closestDistance = distance
        }
    }
    return closest
}

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

/**
 * The scroll distance that brings [item] as close to its snap position as the list scrolls: an edge of the list
 * stops the pages next to it on the way. [items] are the laid out items the edges are looked for in.
 */
internal fun LazyListLayoutInfo.reachableSnapOffset(
    item: LazyListItemInfo,
    snapPosition: SnapPosition,
    items: List<LazyListItemInfo> = visibleItemsInfo,
): Float = snapOffset(item, snapPosition).coerceIn(minScrollOffset(items), maxScrollOffset(items))

/** The scroll distance to the start of the list, or an infinity while the first item is not among [items]. */
internal fun minScrollOffset(items: List<LazyListItemInfo>): Float {
    val first = items.firstOrNull()
    return if (first?.index == 0) first.offset.toFloat().coerceAtMost(0f) else Float.NEGATIVE_INFINITY
}

/** The scroll distance to the end of the list, or an infinity while the last item is not among [items]. */
internal fun LazyListLayoutInfo.maxScrollOffset(items: List<LazyListItemInfo>): Float {
    val last = items.lastOrNull()
    if (last?.index != totalItemsCount - 1) return Float.POSITIVE_INFINITY
    val contentSize = viewportEndOffset - afterContentPadding
    return (last.offset.toFloat() + last.size - contentSize).coerceAtLeast(0f)
}
