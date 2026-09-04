package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
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
    val gridState = rememberLazyGridState(initialFirstVisibleItemIndex = clampedDefaultItem)
    val isHorizontal = orientation == DivGallery.Orientation.HORIZONTAL

    BoxWithConstraints(modifier = modifier) {
        val isLazyGridSupported = constraints.hasBoundedWidth && constraints.hasBoundedHeight
        if (isLazyGridSupported && initialDefaultItem > 0) {
            AdjustDefaultGridItemAlignment(
                gridState = gridState,
                targetIndex = clampedDefaultItem,
                isHorizontal = isHorizontal,
                contentPadding = contentPadding,
                scrollContentAlignment = scrollContentAlignment,
            )
        }
        when {
            !isLazyGridSupported -> NonScrollableGridView(
                items = items,
                isHorizontal = isHorizontal,
                isCrossAxisBounded = if (isHorizontal) constraints.hasBoundedHeight else constraints.hasBoundedWidth,
                columnCount = columnCount,
                itemSpacing = itemSpacing,
                crossSpacing = crossSpacing,
                crossContentAlignment = crossContentAlignment,
                contentPadding = contentPadding,
            )
            isHorizontal -> GalleryLazyHorizontalGrid(
                modifier = Modifier,
                items = items,
                gridState = gridState,
                columnCount = columnCount,
                itemSpacing = itemSpacing,
                crossSpacing = crossSpacing,
                crossContentAlignment = crossContentAlignment,
                contentPadding = contentPadding,
            )
            else -> GalleryLazyVerticalGrid(
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
    gridState: LazyGridState,
    targetIndex: Int,
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

@Composable
private fun NonScrollableGridView(
    items: List<Div>,
    isHorizontal: Boolean,
    isCrossAxisBounded: Boolean,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    if (isHorizontal) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            items.chunked(columnCount).forEach { columnItems ->
                Column(verticalArrangement = Arrangement.spacedBy(crossSpacing)) {
                    columnItems.forEach { item ->
                        ScrollableChildItem(
                            data = item,
                            modifier = if (isCrossAxisBounded) {
                                Modifier.weight(1f)
                            } else {
                                Modifier
                            },
                            isHorizontal = true,
                            crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
                        )
                    }
                    if (isCrossAxisBounded) {
                        repeat(columnCount - columnItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            items.chunked(columnCount).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(crossSpacing)) {
                    rowItems.forEach { item ->
                        ScrollableChildItem(
                            data = item,
                            modifier = if (isCrossAxisBounded) {
                                Modifier.weight(1f)
                            } else {
                                Modifier
                            },
                            isHorizontal = false,
                            crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
                        )
                    }
                    if (isCrossAxisBounded) {
                        repeat(columnCount - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryLazyHorizontalGrid(
    modifier: Modifier,
    items: List<Div>,
    gridState: LazyGridState,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(columnCount),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
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
private fun GalleryLazyVerticalGrid(
    modifier: Modifier,
    items: List<Div>,
    gridState: LazyGridState,
    columnCount: Int,
    itemSpacing: Dp,
    crossSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
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
