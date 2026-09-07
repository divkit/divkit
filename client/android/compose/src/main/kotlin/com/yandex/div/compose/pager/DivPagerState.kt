package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
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
    return pagerPosition(
        snapPosition = snapPosition,
        viewportStart = info.viewportStartOffset,
        viewportEnd = info.viewportEndOffset,
        firstVisibleItemIndex = firstVisibleItemIndex,
        visibleItems = info.visibleItemsInfo,
    )
}

private fun pagerPosition(
    snapPosition: SnapPosition,
    viewportStart: Int,
    viewportEnd: Int,
    firstVisibleItemIndex: Int,
    visibleItems: List<LazyListItemInfo>,
): Pair<Int, Float> {
    if (visibleItems.isEmpty()) return firstVisibleItemIndex to 0f
    val snapLine = when (snapPosition) {
        SnapPosition.Center -> (viewportStart + viewportEnd) / 2
        SnapPosition.End -> viewportEnd
        else -> viewportStart
    }
    val snapped = visibleItems.minBy { abs(it.anchorFor(snapPosition) - snapLine) }
    if (snapped.size <= 0) {
        return snapped.index to 0f
    }

    val fraction = (snapped.anchorFor(snapPosition) - snapLine).toFloat() / snapped.size
    return snapped.index to fraction
}

private fun LazyListItemInfo.anchorFor(snapPosition: SnapPosition): Int = when (snapPosition) {
    SnapPosition.Center -> offset + size / 2
    SnapPosition.End -> offset + size
    else -> offset
}
