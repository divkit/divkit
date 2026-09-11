package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.getScrollAxisPaddings
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded
import com.yandex.div2.Div
import com.yandex.div2.DivGallery

@Composable
internal fun GalleryGridView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    scrollContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
) {
    val initialDefaultItem = remember { defaultItem }
    val clampedDefaultItem = initialDefaultItem.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    // DivGridLayoutManager.instantScrollToPosition establishes lane history from item zero before
    // restoring the default position. Starting directly at defaultItem changes preceding lanes.
    val gridState = rememberLazyStaggeredGridState()
    val isHorizontal = orientation == DivGallery.Orientation.HORIZONTAL

    BoxWithConstraints(modifier = modifier) {
        val isLazyGridSupported = constraints.hasBoundedWidth && constraints.hasBoundedHeight
        if (isLazyGridSupported && items.isNotEmpty() && initialDefaultItem > 0) {
            AdjustDefaultGridItemAlignment(
                gridState = gridState,
                targetIndex = clampedDefaultItem,
                totalItemsCount = items.size,
                isHorizontal = isHorizontal,
                contentPadding = contentPadding,
                scrollContentAlignment = scrollContentAlignment,
            )
        }
        when {
            !isLazyGridSupported -> NonScrollableStaggeredGridView(
                items = items,
                isHorizontal = isHorizontal,
                columnCount = columnCount,
                itemSpacing = itemSpacing,
                crossSpacing = crossSpacing,
                crossContentAlignment = crossContentAlignment,
                contentPadding = contentPadding,
            )
            isHorizontal -> GalleryLazyHorizontalStaggeredGrid(
                modifier = Modifier,
                items = items,
                gridState = gridState,
                columnCount = columnCount,
                itemSpacing = itemSpacing,
                crossSpacing = crossSpacing,
                crossContentAlignment = crossContentAlignment,
                contentPadding = contentPadding,
            )
            else -> GalleryLazyVerticalStaggeredGrid(
                modifier = Modifier,
                items = items,
                gridState = gridState,
                columnCount = columnCount,
                itemSpacing = itemSpacing,
                crossSpacing = crossSpacing,
                crossContentAlignment = crossContentAlignment,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun AdjustDefaultGridItemAlignment(
    gridState: LazyStaggeredGridState,
    targetIndex: Int,
    totalItemsCount: Int,
    isHorizontal: Boolean,
    contentPadding: PaddingValues,
    scrollContentAlignment: DivGallery.ContentAlignment,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val (startPadding, endPadding) = contentPadding.getScrollAxisPaddings(isHorizontal, layoutDirection)
    val startPaddingPx = with(density) { startPadding.roundToPx() }
    val endPaddingPx = with(density) { endPadding.roundToPx() }

    AdjustScrollToItem(
        gridState = gridState,
        targetIndex = targetIndex,
        totalItemsCount = totalItemsCount,
        isHorizontal = isHorizontal,
        restartKey = GalleryScrollAlignmentKey(
            alignment = scrollContentAlignment,
            isHorizontal = isHorizontal,
            startPaddingPx = startPaddingPx,
            endPaddingPx = endPaddingPx,
        ),
        desiredOffset = { viewportSize, itemSize ->
            scrollContentAlignment.calculateDesiredScrollOffset(
                viewportSizePx = viewportSize,
                itemSizePx = itemSize,
                startPaddingPx = startPaddingPx,
                endPaddingPx = endPaddingPx,
            )
        },
    )
}

@Suppress("DEPRECATION")
@Composable
private fun NonScrollableStaggeredGridView(
    items: List<Div>,
    isHorizontal: Boolean,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    val density = LocalDensity.current
    val itemSpacingPx = with(density) { itemSpacing.roundToPx() }
    val crossSpacingPx = with(density) { crossSpacing.roundToPx() }
    val fallbackCrossAxisAlignment = crossContentAlignment.toCrossAxisAlignment()

    // MultiMeasureLayout is limited to this compatibility path: an unbounded cross axis needs
    // a natural lane size before match-parent content can be measured with finite constraints.
    MultiMeasureLayout(
        modifier = Modifier.padding(contentPadding),
        content = {
            items.forEach { item ->
                ScrollableChildItem(
                    data = item,
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal),
                    isHorizontal = isHorizontal,
                    crossAxisAlignment = fallbackCrossAxisAlignment,
                )
            }
        },
    ) { measurables, constraints ->
        measureNonScrollableStaggeredGrid(
            measurables = measurables,
            constraints = constraints,
            isHorizontal = isHorizontal,
            columnCount = columnCount,
            itemSpacing = itemSpacingPx,
            crossSpacing = crossSpacingPx,
        )
    }
}

private fun MeasureScope.measureNonScrollableStaggeredGrid(
    measurables: List<Measurable>,
    constraints: Constraints,
    isHorizontal: Boolean,
    columnCount: Int,
    itemSpacing: Int,
    crossSpacing: Int,
): MeasureResult {
    if (measurables.isEmpty()) {
        return layout(constraints.constrainWidth(0), constraints.constrainHeight(0)) {}
    }

    val totalCrossSpacing = crossSpacing * (columnCount - 1)
    val maxCrossAxisSize = if (isHorizontal) constraints.maxHeight else constraints.maxWidth
    val boundedLaneCrossAxisSize = if (maxCrossAxisSize != Constraints.Infinity) {
        (maxCrossAxisSize - totalCrossSpacing).coerceAtLeast(0) / columnCount
    } else {
        null
    }
    val initialPlaceables = measurables.map {
        it.measure(staggeredGridItemConstraints(isHorizontal, boundedLaneCrossAxisSize))
    }
    val laneCrossAxisSize = boundedLaneCrossAxisSize ?: initialPlaceables.maxOf { placeable ->
        if (isHorizontal) placeable.height else placeable.width
    }
    val placeables = if (boundedLaneCrossAxisSize == null) {
        // Remeasure every child: even an unchanged outer size can hide match-parent descendants
        // that need the finite lane constraints. Nested two-pass galleries/overlaps multiply
        // measurement work; matching the first-pass size alone is not safe grounds to skip this.
        measurables.map {
            it.measure(staggeredGridItemConstraints(isHorizontal, laneCrossAxisSize))
        }
    } else {
        initialPlaceables
    }
    val laneMainAxisEnds = IntArray(columnCount)
    val positionedItems = placeables.map { placeable ->
        val laneIndex = laneMainAxisEnds.indices.minBy { laneMainAxisEnds[it] }
        val mainAxisOffset = laneMainAxisEnds[laneIndex]
        laneMainAxisEnds[laneIndex] += if (isHorizontal) placeable.width else placeable.height
        laneMainAxisEnds[laneIndex] += itemSpacing
        PositionedStaggeredGridItem(
            placeable = placeable,
            laneIndex = laneIndex,
            mainAxisOffset = mainAxisOffset,
        )
    }
    val mainAxisSize = laneMainAxisEnds.maxOrNull()
        ?.minus(itemSpacing)
        ?.coerceAtLeast(0)
        ?: 0
    val crossAxisSize = laneCrossAxisSize * columnCount + totalCrossSpacing
    val width = (if (isHorizontal) mainAxisSize else crossAxisSize)
        .coerceIn(constraints.minWidth, constraints.maxWidth)
    val height = (if (isHorizontal) crossAxisSize else mainAxisSize)
        .coerceIn(constraints.minHeight, constraints.maxHeight)

    return layout(width, height) {
        positionedItems.forEach { item ->
            val crossAxisOffset = item.laneIndex * (laneCrossAxisSize + crossSpacing)
            if (isHorizontal) {
                item.placeable.placeRelative(item.mainAxisOffset, crossAxisOffset)
            } else {
                item.placeable.placeRelative(crossAxisOffset, item.mainAxisOffset)
            }
        }
    }
}

private fun staggeredGridItemConstraints(
    isHorizontal: Boolean,
    laneCrossAxisSize: Int?,
): Constraints = if (isHorizontal) {
    Constraints(
        maxWidth = Constraints.Infinity,
        maxHeight = laneCrossAxisSize ?: Constraints.Infinity,
    )
} else {
    Constraints(
        maxWidth = laneCrossAxisSize ?: Constraints.Infinity,
        maxHeight = Constraints.Infinity,
    )
}

@Composable
private fun GalleryLazyHorizontalStaggeredGrid(
    modifier: Modifier,
    items: List<Div>,
    gridState: LazyStaggeredGridState,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(columnCount),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        horizontalItemSpacing = itemSpacing,
        verticalArrangement = Arrangement.spacedBy(crossSpacing),
    ) {
        items(count = items.size) { index ->
            ScrollableChildItem(
                data = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = true),
                isHorizontal = true,
                crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
            )
        }
    }
}

@Composable
private fun GalleryLazyVerticalStaggeredGrid(
    modifier: Modifier,
    items: List<Div>,
    gridState: LazyStaggeredGridState,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columnCount),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        verticalItemSpacing = itemSpacing,
        horizontalArrangement = Arrangement.spacedBy(crossSpacing),
    ) {
        items(count = items.size) { index ->
            ScrollableChildItem(
                data = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = false),
                isHorizontal = false,
                crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
            )
        }
    }
}

private data class PositionedStaggeredGridItem(
    val placeable: Placeable,
    val laneIndex: Int,
    val mainAxisOffset: Int,
)
