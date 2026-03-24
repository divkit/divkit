package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import kotlinx.coroutines.flow.first

@Composable
internal fun GalleryListView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    itemSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
    scrollMode: DivGallery.ScrollMode,
    isScrollable: Boolean,
) {
    if (isScrollable) {
        val clampedDefaultItem = defaultItem.coerceIn(0, (items.size - 1).coerceAtLeast(0))
        val isPaging = scrollMode == DivGallery.ScrollMode.PAGING

        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = clampedDefaultItem
        )

        val flingBehavior = if (isPaging) {
            rememberSnapFlingBehavior(lazyListState = listState, snapPosition = SnapPosition.Center)
        } else {
            ScrollableDefaults.flingBehavior()
        }

        if (isPaging && clampedDefaultItem > 0) {
            CenterDefaultItem(listState, clampedDefaultItem)
        }

        LazyGalleryListView(
            modifier,
            items,
            orientation,
            listState,
            contentPadding,
            itemSpacingDp,
            crossContentAlignment,
            flingBehavior
        )
    } else {
        NonScrollableGalleryView(
            modifier,
            items,
            orientation,
            itemSpacingDp,
            crossContentAlignment,
            contentPadding
        )
    }
}

@Composable
private fun LazyGalleryListView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    listState: LazyListState,
    contentPadding: PaddingValues,
    itemSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    flingBehavior: FlingBehavior,
) {
    when (orientation) {
        DivGallery.Orientation.HORIZONTAL -> GalleryLazyRow(
            modifier = modifier,
            items = items,
            listState = listState,
            contentPadding = contentPadding,
            itemSpacingDp = itemSpacingDp,
            crossContentAlignment = crossContentAlignment,
            flingBehavior = flingBehavior,
        )
        DivGallery.Orientation.VERTICAL -> GalleryLazyColumn(
            modifier = modifier,
            items = items,
            listState = listState,
            contentPadding = contentPadding,
            itemSpacingDp = itemSpacingDp,
            crossContentAlignment = crossContentAlignment,
            flingBehavior = flingBehavior,
        )
    }
}

@Composable
private fun CenterDefaultItem(listState: LazyListState, defaultItem: Int) {
    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .first { it.isNotEmpty() }

        val layoutInfo = listState.layoutInfo
        val viewportSize = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val targetItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == defaultItem }
        if (targetItem != null) {
            val desiredOffset = (viewportSize - targetItem.size) / 2
            listState.scroll {
                scrollBy((targetItem.offset - desiredOffset).toFloat())
            }
        }
    }
}

@Composable
private fun GalleryLazyColumn(
    modifier: Modifier,
    items: List<Div>,
    listState: LazyListState,
    contentPadding: PaddingValues,
    itemSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    flingBehavior: FlingBehavior
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacingDp),
        horizontalAlignment = crossContentAlignment.toHorizontalAlignment(),
        flingBehavior = flingBehavior,
    ) {
        items(count = items.size) { index ->
            GalleryChildItem(
                childDiv = items[index],
                isHorizontal = false,
                crossContentAlignment = crossContentAlignment,
            )
        }
    }
}

@Composable
private fun GalleryLazyRow(
    modifier: Modifier,
    items: List<Div>,
    listState: LazyListState,
    contentPadding: PaddingValues,
    itemSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    flingBehavior: FlingBehavior
) {
    LazyRow(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
        verticalAlignment = crossContentAlignment.toVerticalAlignment(),
        flingBehavior = flingBehavior,
    ) {
        items(count = items.size) { index ->
            GalleryChildItem(
                childDiv = items[index],
                isHorizontal = true,
                crossContentAlignment = crossContentAlignment,
            )
        }
    }
}

@Composable
private fun NonScrollableGalleryView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    itemSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    contentPadding: PaddingValues,
) {
    when (orientation) {
        DivGallery.Orientation.HORIZONTAL -> Row(
            modifier = modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
            verticalAlignment = crossContentAlignment.toVerticalAlignment(),
        ) {
            items.forEach { GalleryChildItem(it, isHorizontal = true, crossContentAlignment) }
        }

        DivGallery.Orientation.VERTICAL -> Column(
            modifier = modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(itemSpacingDp),
            horizontalAlignment = crossContentAlignment.toHorizontalAlignment(),
        ) {
            items.forEach { GalleryChildItem(it, isHorizontal = false, crossContentAlignment) }
        }
    }
}
