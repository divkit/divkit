package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.getScrollAxisPaddings
import com.yandex.div.compose.utils.scroll.isScrollAxisBounded
import com.yandex.div.compose.utils.scroll.scrollAxisContentSize
import com.yandex.div.compose.views.gallery.scroll.ApplyDefaultItem
import com.yandex.div.compose.views.gallery.scroll.BindGalleryScrollActions
import com.yandex.div.compose.views.gallery.scroll.DefaultItem
import com.yandex.div.compose.views.gallery.scroll.GalleryLayoutInfo
import com.yandex.div.compose.views.gallery.scroll.GalleryLayoutItem
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollLayout
import com.yandex.div.compose.views.gallery.scroll.galleryScrolling
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded

/**
 * Staggered grid whose lane size comes from the content: an unbounded cross axis needs a natural lane
 * size before match-parent content can be measured with finite constraints. Lays out every item, so it
 * scrolls with a plain [ScrollState] when the scroll axis is bounded.
 */
@Composable
internal fun BoxWithConstraintsScope.MeasuredLanesStaggeredGrid(
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
    defaultItem: DefaultItem,
) {
    val items = collection.items
    if (isScrollAxisBounded(properties.isHorizontal)) {
        val scrollState = rememberScrollState()
        // Scroll-axis positions relative to the content start, written on every measurement. The items
        // are data classes: an equal list is not a change, so observers wake up only when the items move.
        var itemPositions by remember { mutableStateOf(emptyList<GalleryLayoutItem>()) }
        val (beforePadding, afterPadding) = properties.scrollAxisPaddingsPx()
        val scrollLayout = remember(scrollState, beforePadding, afterPadding) {
            GalleryScrollLayout(
                scrollState = scrollState,
                currentLayoutInfo = { itemPositions.toGalleryLayoutInfo(scrollState, beforePadding, afterPadding) },
            )
        }
        ApplyDefaultItem(scrollLayout, defaultItem, properties.scrollContentAlignment)
        val flingBehavior = rememberGalleryFlingBehavior(properties, scrollLayout)
        MeasuredLanesLayout(
            collection = collection,
            properties = properties,
            modifier = Modifier
                .galleryScrolling(properties, items, scrollLayout)
                .galleryScrollbar(properties, scrollState)
                .applyIf(properties.isHorizontal) { horizontalScroll(scrollState, flingBehavior = flingBehavior) }
                .applyIf(!properties.isHorizontal) { verticalScroll(scrollState, flingBehavior = flingBehavior) },
            matchParentMaxSize = scrollAxisContentSize(
                properties.contentPadding, properties.isHorizontal, LocalLayoutDirection.current,
            ),
            onItemsPositioned = { itemPositions = it },
        )
    } else {
        NonScrollableGalleryView(Modifier, collection, properties)
    }
}

/** Lays out every item in the lanes without scrolling: the gallery is as long as its content. */
@Composable
internal fun NonScrollableGalleryView(
    modifier: Modifier,
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
) {
    BindGalleryScrollActions(properties.id, collection.items)
    MeasuredLanesLayout(collection, properties, modifier, matchParentMaxSize = Dp.Unspecified, onItemsPositioned = null)
}

@Composable
private fun GalleryProperties.scrollAxisPaddingsPx(): Pair<Int, Int> {
    val (before, after) = contentPadding.getScrollAxisPaddings(isHorizontal, LocalLayoutDirection.current)
    return with(LocalDensity.current) { before.roundToPx() to after.roundToPx() }
}

/**
 * Layout info of items positioned relative to the content start after the content padding, the coordinates
 * of a lazy layout. Every item is laid out, so distances are exact.
 */
private fun List<GalleryLayoutItem>.toGalleryLayoutInfo(
    scrollState: ScrollState,
    beforePadding: Int,
    afterPadding: Int,
) = GalleryLayoutInfo(
    items = map { it.copy(offset = it.offset - scrollState.value) },
    itemCount = size,
    viewportSize = scrollState.viewportSize,
    beforePadding = beforePadding,
    afterPadding = afterPadding,
)

@Suppress("DEPRECATION")
@Composable
private fun MeasuredLanesLayout(
    collection: VisibleCollectionItems,
    properties: GalleryProperties,
    modifier: Modifier,
    matchParentMaxSize: Dp,
    onItemsPositioned: ((List<GalleryLayoutItem>) -> Unit)?,
) {
    val items = collection.items
    val isHorizontal = properties.isHorizontal
    val density = LocalDensity.current
    val itemSpacingPx = with(density) { properties.itemSpacing.roundToPx() }
    val crossSpacingPx = with(density) { properties.crossSpacing.roundToPx() }
    val crossAlignment = properties.crossAlignment

    MultiMeasureLayout(
        modifier = modifier.padding(properties.contentPadding),
        content = {
            items.forEach { item ->
                ScrollableChildItem(
                    data = item,
                    component = collection.components?.get(item),
                    modifier = Modifier.fillMaxCrossAxisIfBounded(isHorizontal),
                    isHorizontal = isHorizontal,
                    crossAxisAlignment = crossAlignment,
                    matchParentMaxSize = matchParentMaxSize,
                )
            }
        },
    ) { measurables, constraints ->
        measureStaggeredGridLanes(
            measurables = measurables,
            constraints = constraints,
            isHorizontal = isHorizontal,
            columnCount = properties.columnCount,
            itemSpacing = itemSpacingPx,
            crossSpacing = crossSpacingPx,
            onItemsPositioned = onItemsPositioned,
        )
    }
}

private fun MeasureScope.measureStaggeredGridLanes(
    measurables: List<Measurable>,
    constraints: Constraints,
    isHorizontal: Boolean,
    columnCount: Int,
    itemSpacing: Int,
    crossSpacing: Int,
    onItemsPositioned: ((List<GalleryLayoutItem>) -> Unit)?,
): MeasureResult {
    if (measurables.isEmpty()) {
        onItemsPositioned?.invoke(emptyList())
        return layout(constraints.constrainWidth(0), constraints.constrainHeight(0)) {}
    }

    val totalCrossSpacing = crossSpacing * (columnCount - 1)
    val lanes = measureLanes(measurables, constraints, isHorizontal, columnCount, totalCrossSpacing)
    val positioned = lanes.placeables.positionInLanes(columnCount, itemSpacing, isHorizontal)
    onItemsPositioned?.invoke(positioned.items.mapIndexed { index, item ->
        GalleryLayoutItem(index, item.mainAxisOffset, item.mainAxisSize)
    })
    val crossAxisSize = lanes.laneCrossAxisSize * columnCount + totalCrossSpacing
    val width = (if (isHorizontal) positioned.mainAxisSize else crossAxisSize)
        .coerceIn(constraints.minWidth, constraints.maxWidth)
    val height = (if (isHorizontal) crossAxisSize else positioned.mainAxisSize)
        .coerceIn(constraints.minHeight, constraints.maxHeight)

    return layout(width, height) {
        positioned.items.forEach { item ->
            val crossAxisOffset = item.laneIndex * (lanes.laneCrossAxisSize + crossSpacing)
            if (isHorizontal) {
                item.placeable.placeRelative(item.mainAxisOffset, crossAxisOffset)
            } else {
                item.placeable.placeRelative(crossAxisOffset, item.mainAxisOffset)
            }
        }
    }
}

private fun measureLanes(
    measurables: List<Measurable>,
    constraints: Constraints,
    isHorizontal: Boolean,
    columnCount: Int,
    totalCrossSpacing: Int,
): MeasuredLanes {
    val maxCrossAxisSize = if (isHorizontal) constraints.maxHeight else constraints.maxWidth
    if (maxCrossAxisSize != Constraints.Infinity) {
        val laneCrossAxisSize = (maxCrossAxisSize - totalCrossSpacing).coerceAtLeast(0) / columnCount
        val laneConstraints = staggeredGridItemConstraints(isHorizontal, laneCrossAxisSize)
        return MeasuredLanes(laneCrossAxisSize, measurables.map { it.measure(laneConstraints) })
    }

    val naturalPlaceables = measurables.map { it.measure(staggeredGridItemConstraints(isHorizontal, null)) }
    val laneCrossAxisSize = naturalPlaceables.maxOf { if (isHorizontal) it.height else it.width }
    // Remeasure every child: even an unchanged outer size can hide match-parent descendants
    // that need the finite lane constraints. Nested two-pass galleries/overlaps multiply
    // measurement work; matching the first-pass size alone is not safe grounds to skip this.
    val laneConstraints = staggeredGridItemConstraints(isHorizontal, laneCrossAxisSize)
    return MeasuredLanes(laneCrossAxisSize, measurables.map { it.measure(laneConstraints) })
}

private fun List<Placeable>.positionInLanes(
    columnCount: Int,
    itemSpacing: Int,
    isHorizontal: Boolean,
): PositionedLanes {
    val laneMainAxisEnds = IntArray(columnCount)
    val items = map { placeable ->
        val laneIndex = laneMainAxisEnds.indices.minBy { laneMainAxisEnds[it] }
        val mainAxisOffset = laneMainAxisEnds[laneIndex]
        val itemSize = if (isHorizontal) placeable.width else placeable.height
        laneMainAxisEnds[laneIndex] += itemSize + itemSpacing
        PositionedStaggeredGridItem(
            placeable = placeable,
            laneIndex = laneIndex,
            mainAxisOffset = mainAxisOffset,
            mainAxisSize = itemSize,
        )
    }
    val mainAxisSize = laneMainAxisEnds.maxOrNull()
        ?.minus(itemSpacing)
        ?.coerceAtLeast(0)
        ?: 0
    return PositionedLanes(items, mainAxisSize)
}

private fun staggeredGridItemConstraints(
    isHorizontal: Boolean,
    laneCrossAxisSize: Int?,
): Constraints = if (isHorizontal) {
    Constraints(
        maxWidth = Constraints.Infinity,
        maxHeight = laneCrossAxisSize ?: Constraints.Infinity,
    )
} else {
    Constraints(
        maxWidth = laneCrossAxisSize ?: Constraints.Infinity,
        maxHeight = Constraints.Infinity,
    )
}

private class MeasuredLanes(
    val laneCrossAxisSize: Int,
    val placeables: List<Placeable>,
)

private class PositionedLanes(
    val items: List<PositionedStaggeredGridItem>,
    val mainAxisSize: Int,
)

private data class PositionedStaggeredGridItem(
    val placeable: Placeable,
    val laneIndex: Int,
    val mainAxisOffset: Int,
    val mainAxisSize: Int,
)
