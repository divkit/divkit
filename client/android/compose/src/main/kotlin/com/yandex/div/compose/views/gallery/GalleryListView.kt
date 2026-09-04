package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.getScrollAxisPaddings
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded
import com.yandex.div2.Div
import com.yandex.div2.DivGallery

@Composable
internal fun GalleryListView(
    modifier: Modifier,
    items: List<Div>,
    orientation: DivGallery.Orientation,
    itemSpacing: Dp,
    crossContentAlignment: DivGallery.ContentAlignment,
    scrollContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
    scrollMode: DivGallery.ScrollMode,
    isScrollable: Boolean,
) {
    if (isScrollable) {
        val isHorizontal = orientation == DivGallery.Orientation.HORIZONTAL
        BoxWithConstraints(modifier = modifier) {
            val isScrollAxisBounded = if (isHorizontal) {
                constraints.hasBoundedWidth
            } else {
                constraints.hasBoundedHeight
            }
            if (isScrollAxisBounded) {
                ScrollableGalleryView(
                    items = items,
                    isHorizontal = isHorizontal,
                    itemSpacing = itemSpacing,
                    crossContentAlignment = crossContentAlignment,
                    scrollContentAlignment = scrollContentAlignment,
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
    scrollContentAlignment: DivGallery.ContentAlignment,
    contentPadding: PaddingValues,
    defaultItem: Int,
    scrollMode: DivGallery.ScrollMode,
) {
    val initialDefaultItem = remember { defaultItem }
    val clampedDefaultItem = initialDefaultItem.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val isPaging = scrollMode == DivGallery.ScrollMode.PAGING

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = clampedDefaultItem
    )

    val flingBehavior = if (isPaging) {
        val snapPosition = remember(scrollContentAlignment) { scrollContentAlignment.toSnapPosition() }
        rememberSnapFlingBehavior(lazyListState = listState, snapPosition = snapPosition)
    } else {
        ScrollableDefaults.flingBehavior()
    }

    if (initialDefaultItem > 0) {
        AdjustDefaultItemAlignment(
            listState = listState,
            targetIndex = clampedDefaultItem,
            isHorizontal = isHorizontal,
            contentPadding = contentPadding,
            scrollContentAlignment = scrollContentAlignment,
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
                data = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal),
                isHorizontal = isHorizontal,
                crossAxisAlignment = crossAlignment,
            )
        }
    }
}

@Composable
private fun AdjustDefaultItemAlignment(
    listState: LazyListState,
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
        listState = listState,
        targetIndex = targetIndex,
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
                    data = it,
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
                    data = it,
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = false),
                    isHorizontal = false,
                    crossAxisAlignment = crossContentAlignment.toCrossAxisAlignment(),
                )
            }
        }
    }
}
