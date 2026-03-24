package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.yandex.div2.Div
import com.yandex.div2.DivGallery

@Composable
internal fun GalleryGridView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    columnCount: Int,
    itemSpacingDp: Dp,
    crossSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
) {
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = defaultItem.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    )

    when (orientation) {
        DivGallery.Orientation.HORIZONTAL -> GalleryLazyHorizontalGrid(
            modifier = modifier,
            items = items,
            gridState = gridState,
            columnCount = columnCount,
            itemSpacingDp = itemSpacingDp,
            crossSpacingDp = crossSpacingDp,
            crossContentAlignment = crossContentAlignment,
            contentPadding = contentPadding,
        )
        DivGallery.Orientation.VERTICAL -> GalleryLazyVerticalGrid(
            modifier = modifier,
            items = items,
            gridState = gridState,
            columnCount = columnCount,
            itemSpacingDp = itemSpacingDp,
            crossSpacingDp = crossSpacingDp,
            crossContentAlignment = crossContentAlignment,
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun GalleryLazyHorizontalGrid(
    modifier: Modifier,
    items: List<Div>,
    gridState: LazyGridState,
    columnCount: Int,
    itemSpacingDp: Dp,
    crossSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    contentPadding: PaddingValues,
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(columnCount),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
        verticalArrangement = Arrangement.spacedBy(crossSpacingDp),
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
private fun GalleryLazyVerticalGrid(
    modifier: Modifier,
    items: List<Div>,
    gridState: LazyGridState,
    columnCount: Int,
    itemSpacingDp: Dp,
    crossSpacingDp: Dp,
    crossContentAlignment: DivGallery.CrossContentAlignment,
    contentPadding: PaddingValues,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacingDp),
        horizontalArrangement = Arrangement.spacedBy(crossSpacingDp),
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
