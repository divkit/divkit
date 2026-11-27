package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.utils.scroll.IntrinsicSizeBarrier
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.scrollAxisContentSize
import com.yandex.div.compose.views.gallery.scroll.ApplyDefaultItem
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollLayout
import com.yandex.div.compose.views.gallery.scroll.galleryScrollLayout
import com.yandex.div.compose.views.gallery.scroll.galleryScrolling
import com.yandex.div.compose.views.gallery.scroll.rememberDefaultItem
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded

@Composable
internal fun GalleryGridView(
    modifier: Modifier,
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
) {
    // The scroll state and the default item live outside BoxWithConstraints: a parent that
    // measures the gallery once more with an unbounded axis switches the branch below.
    // The grid starts at the first item, where the lanes are assigned from, and scrolls to the default one.
    val gridState = rememberLazyStaggeredGridState()
    val scrollLayout = remember(gridState) { gridState.galleryScrollLayout() }
    val defaultItem = rememberDefaultItem(properties.defaultItem)

    BoxWithConstraints(modifier = modifier.then(IntrinsicSizeBarrier)) {
        // A lazy grid divides a bounded cross axis into lanes and scrolls along a bounded scroll axis.
        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            ApplyDefaultItem(scrollLayout, defaultItem, properties.scrollContentAlignment)
            LazyGalleryGrid(collection, properties, gridState, scrollLayout)
        } else {
            MeasuredLanesStaggeredGrid(collection, properties, defaultItem)
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.LazyGalleryGrid(
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
    gridState: LazyStaggeredGridState,
    scrollLayout: GalleryScrollLayout,
) {
    val items = collection.items
    val isHorizontal = properties.isHorizontal
    val compositionKeyStorage = LocalComponent.current.compositionKeyStorage
    val crossAlignment = properties.crossAlignment
    val maxItemSize = scrollAxisContentSize(properties.contentPadding, isHorizontal, LocalLayoutDirection.current)
    val content: LazyStaggeredGridScope.() -> Unit = {
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

    val lanes = StaggeredGridCells.Fixed(properties.columnCount)
    val gridModifier = Modifier
        .galleryScrolling(properties, items, scrollLayout)
        .galleryScrollbar(properties, gridState)
    val flingBehavior = rememberGalleryFlingBehavior(properties, scrollLayout)
    val crossArrangement = Arrangement.spacedBy(properties.crossSpacing)
    if (isHorizontal) {
        LazyHorizontalStaggeredGrid(
            rows = lanes,
            modifier = gridModifier,
            state = gridState,
            contentPadding = properties.contentPadding,
            verticalArrangement = crossArrangement,
            horizontalItemSpacing = properties.itemSpacing,
            flingBehavior = flingBehavior,
            content = content,
        )
    } else {
        LazyVerticalStaggeredGrid(
            columns = lanes,
            modifier = gridModifier,
            state = gridState,
            contentPadding = properties.contentPadding,
            verticalItemSpacing = properties.itemSpacing,
            horizontalArrangement = crossArrangement,
            flingBehavior = flingBehavior,
            content = content,
        )
    }
}
