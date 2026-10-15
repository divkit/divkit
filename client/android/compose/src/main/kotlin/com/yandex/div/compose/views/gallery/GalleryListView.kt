package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.isScrollAxisBounded
import com.yandex.div.compose.utils.scroll.scrollAxisContentSize
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded
import com.yandex.div2.Div

@Composable
internal fun GalleryListView(
    modifier: Modifier,
    items: List<Div>,
    properties: GalleryProperties,
    isScrollable: Boolean,
) {
    if (isScrollable) {
        BoxWithConstraints(modifier = modifier) {
            if (isScrollAxisBounded(properties.isHorizontal)) {
                ScrollableGalleryView(items, properties)
            } else {
                NonScrollableGalleryView(Modifier, items, properties)
            }
        }
    } else {
        NonScrollableGalleryView(modifier, items, properties)
    }
}

@Composable
private fun BoxWithConstraintsScope.ScrollableGalleryView(items: List<Div>, properties: GalleryProperties) {
    val isHorizontal = properties.isHorizontal
    val initialDefaultItem = remember { properties.defaultItem }
    val clampedDefaultItem = initialDefaultItem.coerceIn(0, (items.size - 1).coerceAtLeast(0))

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = clampedDefaultItem
    )

    val flingBehavior = rememberGalleryListFlingBehavior(
        listState, properties.scrollMode, properties.scrollContentAlignment,
    )

    if (initialDefaultItem > 0) {
        AdjustDefaultItemAlignment(listState, clampedDefaultItem, properties)
    }

    val crossAlignment = properties.crossContentAlignment.toCrossAxisAlignment()
    val maxItemSize = properties.contentPadding.scrollAxisContentSize(
        viewportSize = if (isHorizontal) maxWidth else maxHeight,
        isHorizontal = isHorizontal,
        layoutDirection = LocalLayoutDirection.current,
    )

    val viewContext = LocalDivViewContext.current
    OrientedLazyList(
        isHorizontal = isHorizontal,
        modifier = Modifier.galleryScrollbar(
            scrollbar = properties.scrollbar,
            scrollState = listState,
            isHorizontal = isHorizontal,
            contentPadding = properties.contentPadding,
        ),
        listState = listState,
        contentPadding = properties.contentPadding,
        itemSpacing = properties.itemSpacing,
        crossAxisAlignment = crossAlignment,
        flingBehavior = flingBehavior,
    ) {
        items(count = items.size, key = { viewContext.compositionKeyStorage.get(items[it].value()) }) { index ->
            ScrollableChildItem(
                data = items[index],
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal),
                isHorizontal = isHorizontal,
                crossAxisAlignment = crossAlignment,
                matchParentMaxSize = maxItemSize,
            )
        }
    }
}

@Composable
private fun AdjustDefaultItemAlignment(
    listState: LazyListState,
    targetIndex: Int,
    properties: GalleryProperties,
) {
    val alignmentKey = properties.resolveScrollAlignmentKey()
    AdjustScrollToItem(
        listState = listState,
        targetIndex = targetIndex,
        restartKey = alignmentKey,
        desiredOffset = alignmentKey::desiredScrollOffset,
    )
}

@Composable
private fun NonScrollableGalleryView(
    modifier: Modifier,
    items: List<Div>,
    properties: GalleryProperties,
) {
    val crossAlignment = properties.crossContentAlignment.toCrossAxisAlignment()
    if (properties.isHorizontal) {
        Row(
            modifier = modifier.padding(properties.contentPadding),
            horizontalArrangement = Arrangement.spacedBy(properties.itemSpacing),
            verticalAlignment = properties.crossContentAlignment.toVerticalAlignment(),
        ) {
            items.forEach {
                ScrollableChildItem(
                    data = it,
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = true),
                    isHorizontal = true,
                    crossAxisAlignment = crossAlignment,
                )
            }
        }
    } else {
        Column(
            modifier = modifier.padding(properties.contentPadding),
            verticalArrangement = Arrangement.spacedBy(properties.itemSpacing),
            horizontalAlignment = properties.crossContentAlignment.toHorizontalAlignment(),
        ) {
            items.forEach {
                ScrollableChildItem(
                    data = it,
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal = false),
                    isHorizontal = false,
                    crossAxisAlignment = crossAlignment,
                )
            }
        }
    }
}
