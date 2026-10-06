package com.yandex.div.compose.views.pager

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.internal.animation.springInterpolation
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivPageTransformation
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun observePageTransformation(data: DivPageTransformation?): PageTransformation? = when (data) {
    null -> null
    is DivPageTransformation.Slide -> with(data.value) {
        PageTransformation(
            nextPageAlpha.observedFloatValue(),
            previousPageAlpha.observedFloatValue(),
            nextPageScale.observedFloatValue(),
            previousPageScale.observedFloatValue(),
            interpolator.observedValue().toEasing(),
        )
    }
    is DivPageTransformation.Overlap -> with(data.value) {
        PageTransformation(
            nextPageAlpha.observedFloatValue(),
            previousPageAlpha.observedFloatValue(),
            nextPageScale.observedFloatValue(),
            previousPageScale.observedFloatValue(),
            interpolator.observedValue().toEasing(),
            overlap = true,
            reversedStackingOrder = reversedStackingOrder.observedValue(),
        )
    }
}

internal data class PageTransformation(
    val nextAlpha: Float,
    val previousAlpha: Float,
    val nextScale: Float,
    val previousScale: Float,
    val easing: Easing,
    val overlap: Boolean = false,
    private val reversedStackingOrder: Boolean = false,
) {
    /** In an overlap, the pages stacked at or below the selected one stay in place; the pages above slide over them. */
    fun isStationary(position: Float): Boolean = overlap && stackingOrder(position) <= 0f

    fun stackingOrder(position: Float): Float = if (reversedStackingOrder) -position else position

    fun applyAppearance(layer: GraphicsLayerScope, position: Float) {
        val fraction = easing.transform(abs(position).coerceAtMost(1f))
        val cornerAlpha = if (position > 0) nextAlpha else previousAlpha
        val cornerScale = if (position > 0) nextScale else previousScale
        layer.alpha = lerp(1f, cornerAlpha, fraction)
        layer.scaleX = lerp(1f, cornerScale, fraction)
        layer.scaleY = layer.scaleX
    }
}

internal fun Modifier.pageTransformation(
    transformation: PageTransformation?,
    pagePosition: State<PageTransformationPosition>,
    index: Int,
    listState: LazyListState,
    isHorizontal: Boolean,
    layoutDirection: LayoutDirection,
): Modifier = when {
    transformation == null -> this
    !transformation.overlap -> graphicsLayer {
        transformation.applyAppearance(this, pagePosition.value.positionOf(index))
    }
    // Overlapped pages are stacked by their positions and moved from where LazyList actually placed them.
    else -> layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            val geometry = pagePosition.value
            val position = geometry.positionOf(index)
            val placement = coordinates?.let { listState.layoutInfo.placementOffset(it, isHorizontal, layoutDirection) }
            val translation = geometry.translationOf(index, transformation.isStationary(position), placement)
            val direction = if (isHorizontal && layoutDirection == LayoutDirection.Rtl) -1 else 1
            placeable.placeRelativeWithLayer(0, 0, zIndex = transformation.stackingOrder(position)) {
                transformation.applyAppearance(this, position)
                if (isHorizontal) translationX = translation * direction else translationY = translation
            }
        }
    }
}

/** The offset of a placed page in list coordinates, like [LazyListItemInfo.offset]. */
private fun LazyListLayoutInfo.placementOffset(
    coordinates: LayoutCoordinates,
    isHorizontal: Boolean,
    layoutDirection: LayoutDirection,
): Int {
    val position = coordinates.positionInParent()
    val offset = when {
        !isHorizontal -> position.y
        layoutDirection == LayoutDirection.Rtl -> viewportSize.width - coordinates.size.width - position.x
        else -> position.x
    }
    return offset.roundToInt() - beforeContentPadding
}

private val SpringEasing = Easing(::springInterpolation)

@Suppress("MagicNumber")
private fun DivAnimationInterpolator.toEasing(): Easing = when (this) {
    DivAnimationInterpolator.LINEAR -> LinearEasing
    DivAnimationInterpolator.EASE -> CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    DivAnimationInterpolator.EASE_IN -> CubicBezierEasing(0.42f, 0f, 1f, 1f)
    DivAnimationInterpolator.EASE_OUT -> CubicBezierEasing(0f, 0f, 0.58f, 1f)
    DivAnimationInterpolator.EASE_IN_OUT -> CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    DivAnimationInterpolator.SPRING -> SpringEasing
}

@Composable
internal fun rememberPageTransformationPosition(
    listState: LazyListState,
    snapPosition: SnapPosition,
    defaultItem: Int,
    neighbourSize: (Int) -> Int?,
): State<PageTransformationPosition> = remember(listState, snapPosition, defaultItem, neighbourSize) {
    derivedStateOf {
        listState.layoutInfo.pageTransformationPosition(snapPosition, defaultItem, neighbourSize)
    }
}

/**
 * Scroll progress between snap targets. [positionOf] measures a page's distance from the selected page
 * in pages, like the position passed to ViewPager2.PageTransformer: 0 for the selected page, -1 for the
 * previous one, 1 for the next one.
 *
 * The selected [index] and the fractional [offset] are kept apart: infinite pagers use indices around
 * Int.MAX_VALUE / 2, where Float has no fractional precision. [edgeShift] moves the sliding pages of an
 * overlap as if the list could scroll every page, including the edge ones, to its snap position.
 */
internal class PageTransformationPosition(
    private val index: Int,
    private val offset: Float = 0f,
    private val snapOffsets: Map<Int, PageOffset> = emptyMap(),
    private val edgeShift: Float = 0f,
) {
    fun positionOf(pageIndex: Int): Float = (pageIndex - index).toFloat() + offset

    fun translationOf(pageIndex: Int, stationary: Boolean, placementOffset: Int?): Float {
        val offset = snapOffsets[pageIndex] ?: return 0f
        // A stationary page stays at its snap position, while the other pages follow the scroll.
        val target = if (stationary) offset.layout - offset.snap else offset.layout + edgeShift
        // LazyList may leave pinned pages in place during a scroll without remeasurement.
        return target - (placementOffset ?: offset.layout)
    }
}

/** The page offset in the list layout and the scroll distance that brings the page to its snap position. */
internal class PageOffset(val layout: Int, val snap: Float)

private fun LazyListLayoutInfo.pageTransformationPosition(
    snapPosition: SnapPosition,
    defaultItem: Int,
    neighbourSize: (Int) -> Int?,
): PageTransformationPosition {
    val items = itemsWithNeighbours(neighbourSize)
    if (items.isEmpty()) return PageTransformationPosition(defaultItem)
    val snapOffsets = items.associate { it.index to PageOffset(it.offset, snapOffset(it, snapPosition)) }

    val (minOffset, maxOffset) = scrollRange(items)
    if (minOffset == maxOffset) {
        return PageTransformationPosition(
            defaultItem, snapOffsets = snapOffsets, edgeShift = -(snapOffsets[defaultItem]?.snap ?: 0f)
        )
    }
    // Snap targets that the list cannot scroll to are clamped to its scroll range.
    val targets = items.map { item ->
        SnapTarget(item.index, item.size, snapOffsets.getValue(item.index).snap.coerceIn(minOffset, maxOffset))
    }
    val lower = targets.targetAt(targets.filter { it.offset <= 0 }.maxOfOrNull { it.offset }, defaultItem, maxOffset)
    val upper = targets.targetAt(targets.filter { it.offset >= 0 }.minOfOrNull { it.offset }, defaultItem, maxOffset)
    return interpolatePagePosition(lower, upper, snapOffsets, mainAxisItemSpacing)
}

/** The scroll distances to the list edges, or infinities while the edge pages are not laid out. */
private fun LazyListLayoutInfo.scrollRange(items: List<LazyListItemInfo>): Pair<Float, Float> {
    val first = items.first()
    val last = items.last()
    val minOffset = if (first.index == 0) first.offset.toFloat() else Float.NEGATIVE_INFINITY
    val maxOffset = if (last.index == totalItemsCount - 1) {
        val contentSize = viewportEndOffset - afterContentPadding
        (last.offset.toFloat() + last.size - contentSize).coerceAtLeast(minOffset)
    } else {
        Float.POSITIVE_INFINITY
    }
    return minOffset to maxOffset
}

/** A page with the scroll distance to its snap target, clamped to the list's scroll range. */
private class SnapTarget(val index: Int, val size: Int, val offset: Float)

/**
 * Pages clamped to the same snap target resolve to the default page,
 * then to the last page at the end of the list and to the first page elsewhere.
 */
private fun List<SnapTarget>.targetAt(offset: Float?, defaultItem: Int, maxOffset: Float): SnapTarget? {
    val ties = filter { it.offset == offset }
    ties.firstOrNull { it.index == defaultItem }?.let { return it }
    return if (offset == maxOffset) ties.lastOrNull() else ties.firstOrNull()
}

private fun interpolatePagePosition(
    lower: SnapTarget?,
    upper: SnapTarget?,
    snapOffsets: Map<Int, PageOffset>,
    spacing: Int,
): PageTransformationPosition {
    if (lower != null && upper != null) {
        val span = upper.offset - lower.offset
        val fraction = if (span > 0f) -lower.offset / span else 0f
        // Overlap keeps the raw snap anchors even where the list clamps scrolling at its edges.
        val edgeShift = -lerp(snapOffsets.getValue(lower.index).snap, snapOffsets.getValue(upper.index).snap, fraction)
        return PageTransformationPosition(lower.index, -(upper.index - lower.index) * fraction, snapOffsets, edgeShift)
    }

    // Only one snap target is known, so the distance to the next one is estimated by the page stride.
    val target = lower ?: upper ?: return PageTransformationPosition(0)
    val stride = (target.size.toFloat() + spacing).coerceAtLeast(1f)
    val edgeShift = target.offset - snapOffsets.getValue(target.index).snap
    return PageTransformationPosition(target.index, target.offset / stride, snapOffsets, edgeShift)
}

/** The scroll distance that brings [item] to its snap position, as LazyList's SnapLayoutInfoProvider computes it. */
internal fun LazyListLayoutInfo.snapOffset(item: LazyListItemInfo, snapPosition: SnapPosition): Float {
    val desiredOffset = snapPosition.position(
        layoutSize = if (orientation == Orientation.Vertical) viewportSize.height else viewportSize.width,
        itemSize = item.size,
        beforeContentPadding = beforeContentPadding,
        afterContentPadding = afterContentPadding,
        itemIndex = item.index,
        itemCount = totalItemsCount,
    )
    return item.offset - desiredOffset.toFloat()
}

/** Visible items plus the adjacent pages outside the viewport, when their sizes are known. */
internal fun LazyListLayoutInfo.itemsWithNeighbours(neighbourSize: (Int) -> Int?): List<LazyListItemInfo> {
    val first = visibleItemsInfo.firstOrNull() ?: return visibleItemsInfo
    val last = visibleItemsInfo.last()
    fun neighbour(index: Int, offset: (size: Int) -> Int): LazyListItemInfo? {
        if (index !in 0 until totalItemsCount) return null
        val size = neighbourSize(index) ?: return null
        return NeighbourPageInfo(index, offset(size), size)
    }

    val before = neighbour(first.index - 1) { size -> first.offset - mainAxisItemSpacing - size }
    val after = neighbour(last.index + 1) { last.offset + last.size + mainAxisItemSpacing }
    return listOfNotNull(before) + visibleItemsInfo + listOfNotNull(after)
}

private class NeighbourPageInfo(
    override val index: Int,
    override val offset: Int,
    override val size: Int,
) : LazyListItemInfo {
    override val key: Any get() = index
    override val contentType: Any? get() = null
}
