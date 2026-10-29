package com.yandex.div.compose.views.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.collection.observeStaticItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.scroll.restrictParentScroll
import com.yandex.div2.DivGallery

@Composable
internal fun DivGalleryView(
    modifier: Modifier,
    data: DivGallery
) {
    val properties = data.observeProperties()
    val modifier = modifier.applyIf(data.restrictParentScroll.observedValue()) {
        restrictParentScroll(isHorizontal = properties.isHorizontal)
    }
    val collectionItems = data.observeStaticItems(data.items)
    val items = collectionItems.observeVisible()

    if (properties.columnCount > 1) {
        GalleryGridView(modifier, items, collectionItems.components, properties)
    } else {
        GalleryListView(
            modifier,
            items,
            collectionItems.components,
            properties,
            isScrollable = data.observeIsScrollable(properties.isHorizontal),
        )
    }
}

// Scrollable composables (LazyColumn, LazyRow) do not work with
// Modifier.wrapContentSize(unbounded = true) as they require bounded constraints
// to measure their content and determine scroll behavior.
@Composable
private fun DivGallery.observeIsScrollable(isHorizontal: Boolean): Boolean =
    if (isHorizontal) width.observeIsConstrained() else height.observeIsConstrained()
