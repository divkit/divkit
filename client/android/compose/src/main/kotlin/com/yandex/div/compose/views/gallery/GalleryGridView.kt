package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.scrollAxisContentSize
import com.yandex.div.compose.views.gallery.scroll.BindGalleryScrollActions
import com.yandex.div.compose.views.gallery.scroll.galleryScrollLayout
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded
import com.yandex.div2.Div

@Composable
internal fun GalleryGridView(
    modifier: Modifier,
    items: List<Div>,
    properties: GalleryProperties,
) {
    val initialDefaultItem = remember { properties.defaultItem }
    val clampedDefaultItem = initialDefaultItem.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    // DivGridLayoutManager.instantScrollToPosition establishes lane history from item zero before
    // restoring the default position. Starting directly at defaultItem changes preceding lanes.
    val gridState = rememberLazyStaggeredGridState()

    BoxWithConstraints(modifier = modifier) {
        val isLazyGridSupported = constraints.hasBoundedWidth && constraints.hasBoundedHeight
        if (isLazyGridSupported) {
            val density = LocalDensity.current
            val scrollLayout = remember(gridState, density) { gridState.galleryScrollLayout(density) }
            BindGalleryScrollActions(
                id = properties.id,
                items = items,
                layout = scrollLayout,
                scrollMode = properties.scrollMode,
                alignment = properties.scrollContentAlignment,
            )
            if (items.isNotEmpty() && initialDefaultItem > 0) {
                AdjustDefaultGridItemAlignment(gridState, clampedDefaultItem, items.size, properties)
            }
        }
        when {
            !isLazyGridSupported -> MeasuredLanesStaggeredGrid(items, properties, clampedDefaultItem)
            properties.isHorizontal -> GalleryLazyHorizontalStaggeredGrid(items, gridState, properties)
            else -> GalleryLazyVerticalStaggeredGrid(items, gridState, properties)
        }
    }
}

@Composable
private fun AdjustDefaultGridItemAlignment(
    gridState: LazyStaggeredGridState,
    targetIndex: Int,
    totalItemsCount: Int,
    properties: GalleryProperties,
) {
    val alignmentKey = properties.resolveScrollAlignmentKey()
    AdjustScrollToItem(
        gridState = gridState,
        targetIndex = targetIndex,
        totalItemsCount = totalItemsCount,
        isHorizontal = properties.isHorizontal,
        restartKey = alignmentKey,
        desiredOffset = alignmentKey::desiredScrollOffset,
    )
}

@Composable
private fun BoxWithConstraintsScope.GalleryLazyHorizontalStaggeredGrid(
    items: List<Div>,
    gridState: LazyStaggeredGridState,
    properties: GalleryProperties,
) {
    val viewContext = LocalDivViewContext.current
    val crossAlignment = properties.crossContentAlignment.toCrossAxisAlignment()
    val maxItemSize = properties.contentPadding.scrollAxisContentSize(
        viewportSize = maxWidth,
        isHorizontal = true,
        layoutDirection = LocalLayoutDirection.current,
    )
    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(properties.columnCount),
        modifier = Modifier.galleryScrollbar(
            scrollbar = properties.scrollbar,
            scrollState = gridState,
            isHorizontal = true,
            contentPadding = properties.contentPadding,
        ),
        state = gridState,
        flingBehavior = rememberGalleryGridFlingBehavior(
            gridState, properties.scrollMode, properties.scrollContentAlignment,
        ),
        contentPadding = properties.contentPadding,
        horizontalItemSpacing = properties.itemSpacing,
        verticalArrangement = Arrangement.spacedBy(properties.crossSpacing),
    ) {
        items(count = items.size, key = { viewContext.compositionKeyStorage.get(items[it].value()) }) { index ->
            ScrollableChildItem(
                data = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = true),
                isHorizontal = true,
                crossAxisAlignment = crossAlignment,
                matchParentMaxSize = maxItemSize,
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.GalleryLazyVerticalStaggeredGrid(
    items: List<Div>,
    gridState: LazyStaggeredGridState,
    properties: GalleryProperties,
) {
    val viewContext = LocalDivViewContext.current
    val crossAlignment = properties.crossContentAlignment.toCrossAxisAlignment()
    val maxItemSize = properties.contentPadding.scrollAxisContentSize(
        viewportSize = maxHeight,
        isHorizontal = false,
        layoutDirection = LocalLayoutDirection.current,
    )
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(properties.columnCount),
        modifier = Modifier.galleryScrollbar(
            scrollbar = properties.scrollbar,
            scrollState = gridState,
            isHorizontal = false,
            contentPadding = properties.contentPadding,
        ),
        state = gridState,
        flingBehavior = rememberGalleryGridFlingBehavior(
            gridState, properties.scrollMode, properties.scrollContentAlignment,
        ),
        contentPadding = properties.contentPadding,
        verticalItemSpacing = properties.itemSpacing,
        horizontalArrangement = Arrangement.spacedBy(properties.crossSpacing),
    ) {
        items(count = items.size, key = { viewContext.compositionKeyStorage.get(items[it].value()) }) { index ->
            ScrollableChildItem(
                data = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = false),
                isHorizontal = false,
                crossAxisAlignment = crossAlignment,
                matchParentMaxSize = maxItemSize,
            )
        }
    }
}
