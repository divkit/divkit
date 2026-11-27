package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.utils.scroll.IntrinsicSizeBarrier
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.isScrollAxisBounded
import com.yandex.div.compose.utils.scroll.scrollAxisContentSize
import com.yandex.div.compose.views.gallery.scroll.ApplyDefaultItem
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollLayout
import com.yandex.div.compose.views.gallery.scroll.galleryScrollLayout
import com.yandex.div.compose.views.gallery.scroll.galleryScrolling
import com.yandex.div.compose.views.gallery.scroll.rememberDefaultItem
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded

@Composable
internal fun GalleryListView(
    modifier: Modifier,
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
    isScrollable: Boolean,
) {
    val items = collection.items
    if (isScrollable) {
        // The scroll state and the default item live outside BoxWithConstraints: a parent that
        // measures the gallery once more with an unbounded scroll axis switches the branch below.
        val listState = rememberLazyListState(properties.defaultItem.coerceIn(0, maxOf(items.lastIndex, 0)))
        val scrollLayout = remember(listState) { listState.galleryScrollLayout() }
        val defaultItem = rememberDefaultItem(properties.defaultItem)
        BoxWithConstraints(modifier = modifier.then(IntrinsicSizeBarrier)) {
            if (isScrollAxisBounded(properties.isHorizontal)) {
                ApplyDefaultItem(scrollLayout, defaultItem, properties.scrollContentAlignment)
                ScrollableGalleryView(collection, properties, listState, scrollLayout)
            } else {
                NonScrollableGalleryView(Modifier, collection, properties)
            }
        }
    } else {
        NonScrollableGalleryView(modifier, collection, properties)
    }
}

@Composable
private fun BoxWithConstraintsScope.ScrollableGalleryView(
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
    listState: LazyListState,
    scrollLayout: GalleryScrollLayout,
) {
    val items = collection.items
    val isHorizontal = properties.isHorizontal
    val crossAlignment = properties.crossAlignment
    val maxItemSize = scrollAxisContentSize(properties.contentPadding, isHorizontal, LocalLayoutDirection.current)

    val compositionKeyStorage = LocalComponent.current.compositionKeyStorage
    OrientedLazyList(
        isHorizontal = isHorizontal,
        modifier = Modifier.galleryScrolling(properties, items, scrollLayout).galleryScrollbar(properties, listState),
        listState = listState,
        contentPadding = properties.contentPadding,
        itemSpacing = properties.itemSpacing,
        crossAxisAlignment = crossAlignment,
        flingBehavior = rememberGalleryFlingBehavior(properties, scrollLayout),
    ) {
        items(
            count = items.size,
            key = { compositionKeyStorage.get(items[it].value()) },
        ) { index ->
            ScrollableChildItem(
                data = items[index],
                component = collection.components?.get(items[index]),
                modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal),
                isHorizontal = isHorizontal,
                crossAxisAlignment = crossAlignment,
                matchParentMaxSize = maxItemSize,
            )
        }
    }
}
