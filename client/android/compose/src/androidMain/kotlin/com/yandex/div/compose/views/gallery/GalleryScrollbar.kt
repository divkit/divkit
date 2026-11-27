package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.ScrollIndicatorState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.math.roundToInt

@Composable
internal fun Modifier.galleryScrollbar(properties: GalleryProperties, scrollState: ScrollableState): Modifier {
    if (!properties.hasScrollbar) return this
    val state = scrollState.scrollIndicatorState ?: return this
    val isHorizontal = properties.isHorizontal
    val contentPadding = properties.contentPadding

    val color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    return drawWithCache {
        val left = contentPadding.calculateLeftPadding(layoutDirection).toPx()
        val right = contentPadding.calculateRightPadding(layoutDirection).toPx()
        val top = contentPadding.calculateTopPadding().toPx()
        val bottom = contentPadding.calculateBottomPadding().toPx()
        val axisPadding = if (isHorizontal) left + right else top + bottom
        val trackLength = (if (isHorizontal) size.width else size.height) - axisPadding
        val thickness = 4.dp.toPx()
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val track = ScrollbarTrack(
            bounds = Rect(left, top, size.width - right, size.height - bottom),
            length = trackLength,
            axisPadding = axisPadding,
            thickness = thickness,
        )

        onDrawWithContent {
            drawContent()
            drawScrollbar(scrollState, state, isHorizontal, isRtl, color, track)
        }
    }
}

private fun DrawScope.drawScrollbar(
    scrollState: ScrollableState,
    state: ScrollIndicatorState,
    isHorizontal: Boolean,
    isRtl: Boolean,
    color: Color,
    track: ScrollbarTrack,
) {
    if (!scrollState.canScrollForward && !scrollState.canScrollBackward) return

    val scrollOffset = state.scrollOffset
    val contentSize = state.contentSize
    val viewportSize = state.viewportSize
    if (scrollOffset == Int.MAX_VALUE || contentSize == Int.MAX_VALUE || viewportSize == Int.MAX_VALUE) return

    val extent = viewportSize - track.axisPadding
    val range = maxOf(contentSize - track.axisPadding, scrollState.visibleItemsSpan(isHorizontal).toFloat(), extent + 1)
    if (extent <= 0f || track.length <= 0f) return

    val thumbLength = (track.length * extent / range).roundToInt().toFloat()
        .coerceAtLeast(track.thickness * 2)
        .coerceAtMost(track.length)
    val thumbOffset = when {
        !scrollState.canScrollBackward -> 0f
        !scrollState.canScrollForward -> track.length - thumbLength
        else -> ((track.length - thumbLength) * scrollOffset / (range - extent))
            .roundToInt().toFloat().coerceIn(0f, track.length - thumbLength)
    }
    // The thumb runs along the bottom edge of a horizontal gallery and the end edge of a vertical one.
    val bounds = track.bounds
    if (isHorizontal) {
        val left = if (isRtl) bounds.right - thumbOffset - thumbLength else bounds.left + thumbOffset
        drawRect(color, Offset(left, bounds.bottom - track.thickness), Size(thumbLength, track.thickness))
    } else {
        val left = if (isRtl) bounds.left else bounds.right - track.thickness
        drawRect(color, Offset(left, bounds.top + thumbOffset), Size(track.thickness, thumbLength))
    }
}

private fun ScrollableState.visibleItemsSpan(isHorizontal: Boolean): Int {
    if (this !is LazyStaggeredGridState) return 0

    var first = Int.MAX_VALUE
    var last = Int.MIN_VALUE
    layoutInfo.visibleItemsInfo.fastForEach { item ->
        val offset = if (isHorizontal) item.offset.x else item.offset.y
        val size = if (isHorizontal) item.size.width else item.size.height
        first = minOf(first, offset)
        last = maxOf(last, offset + size)
    }
    return if (first <= last) last - first else 0
}

private class ScrollbarTrack(
    val bounds: Rect,
    val length: Float,
    val axisPadding: Float,
    val thickness: Float,
)
