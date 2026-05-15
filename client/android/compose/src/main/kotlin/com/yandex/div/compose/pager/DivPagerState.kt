package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.abs

internal class DivPagerState(
    val pageCount: Int,
    private val listState: LazyListState,
    private val snapPosition: SnapPosition,
) {

    val currentPage: Int
        get() = pageAndOffset().first

    val currentPageOffsetFraction: Float
        get() = pageAndOffset().second

    private fun pageAndOffset(): Pair<Int, Float> {
        val info = listState.layoutInfo
        return pageAndOffset(
            viewportStart = info.viewportStartOffset,
            viewportEnd = info.viewportEndOffset,
            firstVisibleItemIndex = listState.firstVisibleItemIndex,
            visibleItems = info.visibleItemsInfo,
        )
    }

    private fun pageAndOffset(
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
}
