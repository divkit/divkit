package com.yandex.div.compose.views.slider

import android.graphics.Paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal fun DrawScope.drawSlider(state: SliderState, styles: SliderStyles, isRtl: Boolean) {
    val trackLength = size.width - styles.maxTickOrThumbWidth
    if (trackLength <= 0f || size.height <= 0f) return

    val trackStart = styles.maxTickOrThumbWidth / 2f
    val range = state.range
    val activeStart = state.activeStart.position(range, trackLength, trackStart, isRtl)
    val activeEnd = state.activeEnd.position(range, trackLength, trackStart, isRtl)
    val activeLeft = min(activeStart, activeEnd)
    val activeRight = max(activeStart, activeEnd)

    val rangeBounds = styles.rangeBounds(trackLength, trackStart, isRtl)

    drawTrackExcludingRanges(styles.inactiveTrack, trackStart, trackStart + trackLength, rangeBounds)
    drawTrackExcludingRanges(styles.activeTrack, activeLeft, activeRight, rangeBounds)
    rangeBounds.forEach { bounds ->
        val range = bounds.range
        drawRangeSegment(range, bounds.start + range.marginStart, bounds.end - range.marginEnd, activeLeft, activeRight)
    }
    drawTickMarks(styles.activeTickMark, styles.inactiveTickMark, state, trackLength, trackStart, isRtl)

    drawThumb(state.main.visual.value, styles.thumb, styles.thumbText, range, trackLength, trackStart, isRtl)
    state.secondary?.let { s -> styles.secondaryThumb?.let { d ->
        drawThumb(s.visual.value, d, styles.secondaryThumbText, range, trackLength, trackStart, isRtl)
    } }
}

private fun DrawScope.drawThumb(
    value: Float,
    style: SliderDrawable,
    textStyle: SliderTextStyle?,
    range: SliderRange,
    trackLength: Float,
    trackStart: Float,
    isRtl: Boolean,
) {
    val centerX = value.position(range, trackLength, trackStart, isRtl)
    drawSliderDrawable(style, centerX)
    textStyle?.let {
        val text = value.coerceIn(range.minValue, range.maxValue).roundToInt().toString()
        drawThumbText(text, it, centerX)
    }
}

private fun DrawScope.drawTrackExcludingRanges(
    style: SliderDrawable,
    start: Float,
    end: Float,
    excludedRanges: List<SliderRangeBounds>,
) {
    if (end <= start) return
    var segmentStart = start
    excludedRanges.forEach { range ->
        val excludedStart = range.start.coerceIn(start, end)
        val excludedEnd = range.end.coerceIn(start, end)
        if (excludedEnd <= segmentStart) return@forEach
        if (excludedStart > segmentStart) drawSliderDrawable(style, segmentStart, excludedStart)
        segmentStart = max(segmentStart, excludedEnd)
    }
    if (segmentStart < end) drawSliderDrawable(style, segmentStart, end)
}

private fun DrawScope.drawRangeSegment(
    range: SliderVisualRange,
    start: Float,
    end: Float,
    activeLeft: Float,
    activeRight: Float,
) {
    val activeBegin = max(start, activeLeft)
    val activeEnd = min(end, activeRight)
    if (activeBegin >= activeEnd) {
        drawSliderDrawable(range.inactiveTrackStyle, start, end)
        return
    }
    if (start < activeBegin) {
        drawSliderDrawable(range.inactiveTrackStyle, start, (activeBegin - 1f).coerceAtLeast(start))
    }
    drawSliderDrawable(range.activeTrackStyle, activeBegin, activeEnd)
    if (activeEnd < end) {
        drawSliderDrawable(range.inactiveTrackStyle, (activeEnd + 1f).coerceAtMost(end), end)
    }
}

private fun DrawScope.drawTickMarks(
    activeStyle: SliderDrawable?,
    inactiveStyle: SliderDrawable?,
    state: SliderState,
    trackLength: Float,
    trackStart: Float,
    isRtl: Boolean,
) {
    if (activeStyle == null && inactiveStyle == null) return
    val activeStart = state.activeStart.roundToInt()
    val activeEnd = state.activeEnd.roundToInt()
    for (value in state.range.minValue.roundToInt()..state.range.maxValue.roundToInt()) {
        val style = (if (value in activeStart..activeEnd) activeStyle else inactiveStyle) ?: continue
        drawSliderDrawable(style, centerX = value.toFloat().position(state.range, trackLength, trackStart, isRtl))
    }
}

private fun DrawScope.drawSliderDrawable(drawable: SliderDrawable, centerX: Float) {
    val left = centerX - drawable.width / 2f
    drawSliderDrawable(drawable, left, left + drawable.width)
}

private fun DrawScope.drawSliderDrawable(drawable: SliderDrawable, start: Float, end: Float) {
    if (end <= start) return
    val top = (size.height - drawable.height) / 2f
    val width = end - start
    val height = drawable.height
    val stroke = drawable.stroke
    val fillInset = if (stroke != null) 1f else 0f
    drawRoundRect(
        color = drawable.color,
        topLeft = Offset(start + fillInset, top + fillInset),
        size = Size((width - 2 * fillInset).coerceAtLeast(0f), (height - 2 * fillInset).coerceAtLeast(0f)),
        cornerRadius = CornerRadius(
            drawable.radius.shrinkBy(height, stroke?.width ?: 0f),
            drawable.radius.shrinkBy(width, stroke?.width ?: 0f),
        ),
    )
    if (stroke != null) {
        val inset = stroke.width / 2f
        drawRoundRect(
            color = stroke.color,
            topLeft = Offset(start + inset, top + inset),
            size = Size((width - stroke.width).coerceAtLeast(0f), (height - stroke.width).coerceAtLeast(0f)),
            cornerRadius = CornerRadius(drawable.radius, drawable.radius),
            style = Stroke(width = stroke.width, pathEffect = stroke.pathEffect),
        )
    }
}

private fun Float.shrinkBy(size: Float, strokeWidth: Float): Float =
    this - if (this >= size / 2f) strokeWidth / 2f else 0f

private fun DrawScope.drawThumbText(text: String, style: SliderTextStyle, centerX: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = style.fontSize
        letterSpacing = style.letterSpacing
        color = style.color
        typeface = style.typeface
    }
    val bounds = android.graphics.Rect().also { paint.getTextBounds(text, 0, text.length, it) }
    drawContext.canvas.nativeCanvas.drawText(
        text,
        centerX - paint.measureText(text) / 2f + style.offsetX,
        size.height / 2f + bounds.height() / 2f + style.offsetY,
        paint,
    )
}
