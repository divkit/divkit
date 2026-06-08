package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div2.Div
import com.yandex.div2.DivGallery

@Composable
internal fun GalleryListView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    itemSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
    scrollMode: DivGallery.ScrollMode,
    isScrollable: Boolean,
) {
    if (isScrollable) {
        val isHorizontal = orientation == DivGallery.Orientation.HORIZONTAL
        BoxWithConstraints(modifier = modifier) {
            val isScrollAxisBounded = if (isHorizontal) constraints.hasBoundedWidth else constraints.hasBoundedHeight
            if (isScrollAxisBounded) {
                ScrollableGalleryView(
                    items = items,
                    isHorizontal = isHorizontal,
                    itemSpacing = itemSpacing,
                    crossContentAlignment = crossContentAlignment,
                    contentPadding = contentPadding,
                    defaultItem = defaultItem,
                    scrollMode = scrollMode,
                )
            } else {
                NonScrollableGalleryView(
                    modifier = Modifier,
                    items = items,
                    orientation = orientation,
                    itemSpacing = itemSpacing,
                    crossContentAlignment = crossContentAlignment,
                    contentPadding = contentPadding,
                )
            }
        }
    } else {
        NonScrollableGalleryView(
            modifier,
            items,
            orientation,
            itemSpacing,
            crossContentAlignment,
            contentPadding
        )
    }
}

@Composable
private fun ScrollableGalleryView(
    items: List<Div>,
    isHorizontal: Boolean,
    itemSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
    scrollMode: DivGallery.ScrollMode,
) {
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
        AdjustScrollToItem(
            listState = listState,
            targetIndex = clampedDefaultItem,
            desiredOffset = { viewportSize, itemSize -> (viewportSize - itemSize) / 2 },
        )
    }

    val crossAlignment = crossContentAlignment.toCrossAxisAlignment()

    OrientedLazyList(
        isHorizontal = isHorizontal,
        modifier = Modifier,
        listState = listState,
        contentPadding = contentPadding,
        itemSpacing = itemSpacing,
        crossAxisAlignment = crossAlignment,
        flingBehavior = flingBehavior,
    ) {
        items(count = items.size) { index ->
            ScrollableChildItem(
                childDiv = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal),
                isHorizontal = isHorizontal,
                crossAxisAlignment = crossAlignment,
            )
        }
    }
}

@Composable
private fun NonScrollableGalleryView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    itemSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
) {
    when (orientation) {
        DivGallery.Orientation.HORIZONTAL -> Row(
            modifier = modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalAlignment = crossContentAlignment.toVerticalAlignment(),
        ) {
            items.forEach {
                ScrollableChildItem(
                    childDiv = it,
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = true),
                    isHorizontal = true,
                    crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
                )
            }
        }

        DivGallery.Orientation.VERTICAL -> Column(
            modifier = modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalAlignment = crossContentAlignment.toHorizontalAlignment(),
        ) {
            items.forEach {
                ScrollableChildItem(
                    childDiv = it,
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = false),
                    isHorizontal = false,
                    crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
                )
            }
        }
    }
}
