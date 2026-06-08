package com.yandex.div.compose.views.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivGallery
import com.yandex.div2.DivVisibility

@Composable
internal fun DivGalleryView(
    modifier: Modifier,
    data: DivGallery,
) {
    val orientation = data.orientation.observedValue()
    val itemSpacing = data.itemSpacing.observedValue().toDp()
    val crossContentAlignment = data.crossContentAlignment.observedValue()
    val columnCount = data.columnCount?.observedIntValue() ?: 1
    val defaultItem = data.defaultItem.observedIntValue()
    val contentPadding = data.paddings.observeInsets()

    val items = data.items.orEmpty().filter {
        it.value().visibility.observedValue() != DivVisibility.GONE
    }

    if (columnCount > 1) {
        GalleryGridView(
            modifier = modifier,
            items = items,
            orientation = orientation,
            columnCount = columnCount,
            itemSpacing = itemSpacing,
            crossSpacing = data.crossSpacing?.observedValue()?.toDp() ?: itemSpacing,
            crossContentAlignment = crossContentAlignment,
            contentPadding = contentPadding,
            defaultItem = defaultItem,
        )
    } else {
        val isHorizontal = orientation == DivGallery.Orientation.HORIZONTAL
        val isScrollable = data.isScrollable(orientation)

        GalleryListView(
            modifier = modifier,
            items = items,
            orientation = orientation,
            itemSpacing = itemSpacing,
            crossContentAlignment = crossContentAlignment,
            contentPadding = contentPadding,
            defaultItem = defaultItem,
            scrollMode = data.scrollMode.observedValue(),
            isScrollable = isScrollable,
        )
    }
}

// Scrollable composables (LazyColumn, LazyRow) do not work with
// Modifier.wrapContentSize(unbounded = true) as they require bounded constraints
// to measure their content and determine scroll behavior.
@Composable
private fun DivGallery.isScrollable(orientation: DivGallery.Orientation): Boolean {
    return when (orientation) {
        DivGallery.Orientation.HORIZONTAL -> {
            width.observeIsConstrained()
        }
        DivGallery.Orientation.VERTICAL -> {
            height.observeIsConstrained()
        }
    }
}
