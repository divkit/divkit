package com.yandex.div.compose.views.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.observedValue
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
            modifier = modifier.constrainGridAxes(),
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
            modifier = if (isScrollable) modifier.constrainScrollAxis(isHorizontal) else modifier,
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

private fun Modifier.constrainScrollAxis(isHorizontal: Boolean): Modifier = layout { measurable, constraints ->
    val adjusted = if (isHorizontal && constraints.maxWidth == Constraints.Infinity) {
        constraints.copy(maxWidth = constraints.minWidth.coerceAtLeast(0))
    } else if (!isHorizontal && constraints.maxHeight == Constraints.Infinity) {
        constraints.copy(maxHeight = constraints.minHeight.coerceAtLeast(0))
    } else {
        constraints
    }
    val placeable = measurable.measure(adjusted)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

private fun Modifier.constrainGridAxes(): Modifier = layout { measurable, constraints ->
    val adjusted = constraints.copy(
        maxWidth = if (constraints.maxWidth == Constraints.Infinity) {
            constraints.minWidth.coerceAtLeast(0)
        } else {
            constraints.maxWidth
        },
        maxHeight = if (constraints.maxHeight == Constraints.Infinity) {
            constraints.minHeight.coerceAtLeast(0)
        } else {
            constraints.maxHeight
        },
    )
    val placeable = measurable.measure(adjusted)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}
