package com.yandex.div.compose.views.slider

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.max
import kotlin.math.min

internal class SliderStyles(
    val thumb: SliderDrawable,
    val secondaryThumb: SliderDrawable?,
    val activeTrack: SliderDrawable,
    val inactiveTrack: SliderDrawable,
    val activeTickMark: SliderDrawable?,
    val inactiveTickMark: SliderDrawable?,
    val thumbText: SliderTextPainter?,
    val secondaryThumbText: SliderTextPainter?,
    val ranges: List<SliderVisualRange>,
    private val range: SliderRange
) {
    val hasTickMarks: Boolean = activeTickMark != null || inactiveTickMark != null

    val maxTickOrThumbWidth: Float = maxOf(
        thumb.width,
        secondaryThumb?.width ?: 0f,
        activeTickMark?.width ?: 0f,
        inactiveTickMark?.width ?: 0f
    )

    val desiredHeight: Float = maxOf(
        thumb.height,
        secondaryThumb?.height ?: 0f,
        activeTrack.height,
        inactiveTrack.height,
        activeTickMark?.height ?: 0f,
        inactiveTickMark?.height ?: 0f,
        ranges.maxOfOrNull { max(it.activeTrackStyle.height, it.inactiveTrackStyle.height) } ?: 0f
    )

    val desiredWidth: Float = run {
        val tickCount = (range.span + 1f).toInt().coerceAtLeast(1)
        maxOf(
            thumb.width,
            secondaryThumb?.width ?: 0f,
            activeTrack.width * tickCount,
            inactiveTrack.width * tickCount
        )
    }

    private var cachedTrackLength = Float.NaN
    private var cachedTrackStart = Float.NaN
    private var cachedRtl: Boolean? = null
    private var cachedRangeBounds: List<SliderRangeBounds> = emptyList()

    fun rangeBounds(
        trackLength: Float,
        trackStart: Float,
        isRtl: Boolean
    ): List<SliderRangeBounds> {
        if (trackLength == cachedTrackLength && trackStart == cachedTrackStart && cachedRtl == isRtl) {
            return cachedRangeBounds
        }
        cachedRangeBounds = ranges.map {
            val start = it.start.position(range, trackLength, trackStart, isRtl)
            val end = it.end.position(range, trackLength, trackStart, isRtl)
            SliderRangeBounds(it, min(start, end), max(start, end))
        }.sortedBy { it.start }
        cachedTrackLength = trackLength
        cachedTrackStart = trackStart
        cachedRtl = isRtl
        return cachedRangeBounds
    }
}

internal class SliderRangeBounds(
    val range: SliderVisualRange,
    val start: Float,
    val end: Float,
)

internal class SliderDrawable(
    val width: Float,
    val height: Float,
    val color: Color,
    val radius: Float,
    val stroke: SliderStroke?,
)

internal class SliderStroke(
    val color: Color,
    val width: Float,
    val pathEffect: PathEffect?,
)

@Immutable
internal data class SliderTextStyle(
    val fontSize: Float,
    val letterSpacing: Float,
    val fontFamily: FontFamily,
    val fontWeight: FontWeight,
    val offsetX: Float,
    val offsetY: Float,
    val color: Int,
)

internal class SliderVisualRange(
    val start: Float,
    val end: Float,
    val marginStart: Float,
    val marginEnd: Float,
    val activeTrackStyle: SliderDrawable,
    val inactiveTrackStyle: SliderDrawable,
)
