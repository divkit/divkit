package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
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

    val scrollOffset = with(density) {
        calculateScrollOffset(
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

    return InitialScroll(itemIndex = 0, scrollOffset = scrollOffset)
}

internal fun desiredSnapOffset(
    snapPosition: SnapPosition,
    viewportSizePx: Int,
    pageSizePx: Int,
    endPaddingPx: Int,
    startPaddingPx: Int,
): Int = when (snapPosition) {
    SnapPosition.Center -> (viewportSizePx - pageSizePx) / 2
    SnapPosition.End -> viewportSizePx - endPaddingPx - pageSizePx
    else -> startPaddingPx
}

private fun calculateScrollOffset(
    defaultItem: Int,
    snapPosition: SnapPosition,
    pageSizePx: Int,
    spacingPx: Int,
    startPx: Int,
    endPx: Int,
    viewportPx: Int,
    itemCount: Int,
): Int {
    val desiredOffset = desiredSnapOffset(snapPosition, viewportPx, pageSizePx, endPx, startPx)
    val centeredScroll = startPx + defaultItem * (pageSizePx + spacingPx) - desiredOffset

    val startClamp = edgeClamp(
        excess = startPx - desiredOffset,
        contentOutside = defaultItem * (pageSizePx + spacingPx),
    )
    val endClamp = edgeClamp(
        excess = endPx - (viewportPx - desiredOffset - pageSizePx),
        contentOutside = (itemCount - 1 - defaultItem) * (pageSizePx + spacingPx),
    )

    return (centeredScroll - startClamp + endClamp).coerceAtLeast(0)
}

private fun edgeClamp(excess: Int, contentOutside: Int): Int {
    if (excess <= 0) return 0
    return max(0, excess - contentOutside)
}
