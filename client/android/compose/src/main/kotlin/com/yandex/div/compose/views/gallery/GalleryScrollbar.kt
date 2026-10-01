package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.ScrollIndicatorState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivGallery
import kotlin.math.roundToInt

@Composable
internal fun Modifier.galleryScrollbar(
    scrollbar: Expression<DivGallery.Scrollbar>,
    scrollState: ScrollableState,
    isHorizontal: Boolean,
    contentPadding: PaddingValues,
): Modifier {
    if (scrollbar.observedValue() == DivGallery.Scrollbar.NONE) return this
    val state = scrollState.scrollIndicatorState ?: return this

    val context = LocalContext.current
    val color = remember(context) {
        val attributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorControlNormal))
        val color = Color(attributes.getColor(0, android.graphics.Color.GRAY))
        attributes.recycle()
        color.copy(alpha = color.alpha * .5f)
    }

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
    if (isHorizontal) {
        drawHorizontalScrollbar(track, thumbOffset, thumbLength, isRtl, color)
    } else {
        drawVerticalScrollbar(track, thumbOffset, thumbLength, isRtl, color)
    }
}

private fun DrawScope.drawHorizontalScrollbar(
    track: ScrollbarTrack,
    thumbOffset: Float,
    thumbLength: Float,
    isRtl: Boolean,
    color: Color,
) {
    val left = if (isRtl) {
        track.bounds.right - thumbOffset - thumbLength
    } else {
        track.bounds.left + thumbOffset
    }
    drawRect(
        color = color,
        topLeft = Offset(left, track.bounds.bottom - track.thickness),
        size = Size(thumbLength, track.thickness),
    )
}

private fun DrawScope.drawVerticalScrollbar(
    track: ScrollbarTrack,
    thumbOffset: Float,
    thumbLength: Float,
    isRtl: Boolean,
    color: Color,
) {
    val left = if (isRtl) track.bounds.left else track.bounds.right - track.thickness
    drawRect(
        color = color,
        topLeft = Offset(left, track.bounds.top + thumbOffset),
        size = Size(track.thickness, thumbLength),
    )
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
