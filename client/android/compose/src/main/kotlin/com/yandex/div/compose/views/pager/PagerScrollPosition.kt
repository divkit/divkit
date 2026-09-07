package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.utils.scroll.desiredSnapOffset
import kotlin.math.max

internal data class InitialScroll(
    val itemIndex: Int,
    val scrollOffset: Int,
)

internal fun calculateInitialScroll(
    defaultItem: Int,
    snapPosition: SnapPosition,
    pageSize: Dp?,
    itemSpacing: Dp,
    startPadding: Dp,
    endPadding: Dp,
    viewportSize: Dp,
    itemCount: Int,
    density: Density,
): InitialScroll {
    if (pageSize == null || snapPosition == SnapPosition.Start) {
        return InitialScroll(itemIndex = defaultItem, scrollOffset = 0)
    }

    val (itemIndex, scrollOffset) = with(density) {
        calculateScrollPosition(
            defaultItem = defaultItem,
            snapPosition = snapPosition,
            pageSizePx = pageSize.roundToPx(),
            spacingPx = itemSpacing.roundToPx(),
            startPx = startPadding.roundToPx(),
            endPx = endPadding.roundToPx(),
            viewportPx = viewportSize.roundToPx(),
            itemCount = itemCount,
        )
    }

    return InitialScroll(itemIndex = itemIndex, scrollOffset = scrollOffset)
}

private fun calculateScrollPosition(
    defaultItem: Int,
    snapPosition: SnapPosition,
    pageSizePx: Int,
    spacingPx: Int,
    startPx: Int,
    endPx: Int,
    viewportPx: Int,
    itemCount: Int,
): InitialScroll {
    val desiredOffset = desiredSnapOffset(
        snapPosition = snapPosition,
        viewportSizePx = viewportPx,
        itemSizePx = pageSizePx,
        startPaddingPx = startPx,
        endPaddingPx = endPx,
    )
    val itemStride = pageSizePx.toLong() + spacingPx
    if (itemStride <= 0) return InitialScroll(defaultItem, 0)

    val centeredScroll = startPx + defaultItem.toLong() * itemStride - desiredOffset

    val startClamp = edgeClamp(
        excess = (startPx - desiredOffset).toLong(),
        contentOutside = defaultItem.toLong() * itemStride,
    )
    val endClamp = edgeClamp(
        excess = (endPx - (viewportPx - desiredOffset - pageSizePx)).toLong(),
        contentOutside = (itemCount - 1L - defaultItem) * itemStride,
    )

    val absoluteScroll = (centeredScroll - startClamp + endClamp).coerceAtLeast(0)
    val itemIndex = (absoluteScroll / itemStride).coerceIn(0, itemCount - 1L).toInt()
    val itemOffset = (absoluteScroll - itemIndex * itemStride).toInt()
    return InitialScroll(itemIndex, itemOffset)
}

private fun edgeClamp(excess: Long, contentOutside: Long): Long {
    if (excess <= 0) return 0
    return max(0, excess - contentOutside)
}
