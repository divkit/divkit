package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.isScrollAxisBounded
import com.yandex.div.compose.utils.scroll.scrollAxisContentSize
import com.yandex.div.compose.views.modifiers.fillMaxCrossAxisIfBounded
import com.yandex.div2.Div
import kotlinx.coroutines.flow.first

/**
 * Staggered grid whose lane size comes from the content: an unbounded cross axis needs a natural lane
 * size before match-parent content can be measured with finite constraints. Lays out every item, so it
 * scrolls with a plain [ScrollState] when the scroll axis is bounded.
 */
@Composable
internal fun BoxWithConstraintsScope.MeasuredLanesStaggeredGrid(
    items: List<Div>,
    properties: GalleryProperties,
    defaultItem: Int,
) {
    if (isScrollAxisBounded(properties.isHorizontal)) {
        val scrollState = rememberScrollState()
        val itemPositions = remember { MeasuredItemPositions() }
        if (defaultItem > 0) {
            AdjustDefaultItemAlignment(scrollState, itemPositions, defaultItem, properties)
        }
        val flingBehavior = rememberGalleryLanesFlingBehavior(scrollState, itemPositions, properties)
        MeasuredLanesLayout(
            items = items,
            properties = properties,
            modifier = Modifier
                .galleryScrollbar(properties.scrollbar, scrollState, properties.isHorizontal, properties.contentPadding)
                .applyIf(properties.isHorizontal) { horizontalScroll(scrollState, flingBehavior = flingBehavior) }
                .applyIf(!properties.isHorizontal) { verticalScroll(scrollState, flingBehavior = flingBehavior) },
            matchParentMaxSize = properties.contentPadding.scrollAxisContentSize(
                viewportSize = if (properties.isHorizontal) maxWidth else maxHeight,
                isHorizontal = properties.isHorizontal,
                layoutDirection = LocalLayoutDirection.current,
            ),
            itemPositions = itemPositions,
        )
    } else {
        MeasuredLanesLayout(items, properties, Modifier, matchParentMaxSize = Dp.Unspecified, itemPositions = null)
    }
}

/** Scroll-axis positions of laid out items, relative to the content start after the content padding. */
internal class MeasuredItemPositions {
    var offsets = IntArray(0)
        private set
    var sizes = IntArray(0)
        private set

    fun update(offsets: IntArray, sizes: IntArray) {
        this.offsets = offsets
        this.sizes = sizes
    }
}

// Applies default_item once, like the lazy grid; a restored or user-changed position is kept.
@Composable
private fun AdjustDefaultItemAlignment(
    scrollState: ScrollState,
    itemPositions: MeasuredItemPositions,
    targetIndex: Int,
    properties: GalleryProperties,
) {
    val alignmentKey = properties.resolveScrollAlignmentKey()
    var isAdjusted by rememberSaveable(scrollState) { mutableStateOf(false) }
    LaunchedEffect(scrollState, targetIndex) {
        if (isAdjusted) return@LaunchedEffect
        snapshotFlow { scrollState.viewportSize }.first { it > 0 }
        if (targetIndex !in itemPositions.offsets.indices) return@LaunchedEffect
        val desiredOffset = alignmentKey.desiredScrollOffset(scrollState.viewportSize, itemPositions.sizes[targetIndex])
        scrollState.scrollTo((itemPositions.offsets[targetIndex] - desiredOffset).coerceIn(0, scrollState.maxValue))
        isAdjusted = true
    }
}

@Suppress("DEPRECATION")
@Composable
private fun MeasuredLanesLayout(
    items: List<Div>,
    properties: GalleryProperties,
    modifier: Modifier,
    matchParentMaxSize: Dp,
    itemPositions: MeasuredItemPositions?,
) {
    val isHorizontal = properties.isHorizontal
    val density = LocalDensity.current
    val itemSpacingPx = with(density) { properties.itemSpacing.roundToPx() }
    val crossSpacingPx = with(density) { properties.crossSpacing.roundToPx() }
    val crossAlignment = properties.crossContentAlignment.toCrossAxisAlignment()

    MultiMeasureLayout(
        modifier = modifier.padding(properties.contentPadding),
        content = {
            items.forEach { item ->
                ScrollableChildItem(
                    data = item,
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
            itemPositions = itemPositions,
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
    itemPositions: MeasuredItemPositions?,
): MeasureResult {
    if (measurables.isEmpty()) {
        itemPositions?.update(IntArray(0), IntArray(0))
        return layout(constraints.constrainWidth(0), constraints.constrainHeight(0)) {}
    }

    val totalCrossSpacing = crossSpacing * (columnCount - 1)
    val lanes = measureLanes(measurables, constraints, isHorizontal, columnCount, totalCrossSpacing)
    val positioned = lanes.placeables.positionInLanes(columnCount, itemSpacing, isHorizontal)
    itemPositions?.update(
        offsets = IntArray(positioned.items.size) { positioned.items[it].mainAxisOffset },
        sizes = IntArray(positioned.items.size) { positioned.items[it].mainAxisSize(isHorizontal) },
    )
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
        laneMainAxisEnds[laneIndex] += if (isHorizontal) placeable.width else placeable.height
        laneMainAxisEnds[laneIndex] += itemSpacing
        PositionedStaggeredGridItem(
            placeable = placeable,
            laneIndex = laneIndex,
            mainAxisOffset = mainAxisOffset,
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
) {
    fun mainAxisSize(isHorizontal: Boolean): Int = if (isHorizontal) placeable.width else placeable.height
}
