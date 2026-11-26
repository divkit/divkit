package com.yandex.div.compose.views.pager

import androidx.compose.animation.core.Easing
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
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
import com.yandex.div.compose.pager.maxScrollOffset
import com.yandex.div.compose.pager.minScrollOffset
import com.yandex.div.compose.pager.snapOffset
import kotlin.math.abs
import kotlin.math.roundToInt

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

@Composable
internal fun rememberPageTransformationPosition(
    listState: LazyListState,
    snapPosition: SnapPosition,
    settledPage: IntState,
    neighbourSize: (Int) -> Int?,
): State<PageTransformationPosition> = remember(listState, snapPosition, settledPage, neighbourSize) {
    derivedStateOf {
        listState.layoutInfo.pageTransformationPosition(snapPosition, settledPage.intValue, neighbourSize)
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
    settledPage: Int,
    neighbourSize: (Int) -> Int?,
): PageTransformationPosition {
    val items = itemsWithNeighbours(neighbourSize)
    if (items.isEmpty()) return PageTransformationPosition(settledPage)
    val snapOffsets = items.associate { it.index to PageOffset(it.offset, snapOffset(it, snapPosition)) }

    val minOffset = minScrollOffset(items)
    val maxOffset = maxScrollOffset(items)
    if (minOffset == maxOffset) {
        return PageTransformationPosition(
            settledPage, snapOffsets = snapOffsets, edgeShift = -(snapOffsets[settledPage]?.snap ?: 0f)
        )
    }
    // Snap targets that the list cannot scroll to are clamped to its scroll range.
    val targets = items.map { item ->
        SnapTarget(item.index, item.size, snapOffsets.getValue(item.index).snap.coerceIn(minOffset, maxOffset))
    }
    val lower = targets.targetAt(targets.filter { it.offset <= 0 }.maxOfOrNull { it.offset }, settledPage)
    val upper = targets.targetAt(targets.filter { it.offset >= 0 }.minOfOrNull { it.offset }, settledPage)
    return interpolatePagePosition(lower, upper, snapOffsets, mainAxisItemSpacing)
}

/** A page with the scroll distance to its snap target, clamped to the list's scroll range. */
private class SnapTarget(val index: Int, val size: Int, val offset: Float)

/** Pages clamped to the same snap target resolve to the one closest by index to the settled page. */
private fun List<SnapTarget>.targetAt(offset: Float?, settledPage: Int): SnapTarget? =
    filter { it.offset == offset }.minByOrNull { abs(it.index - settledPage) }

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
