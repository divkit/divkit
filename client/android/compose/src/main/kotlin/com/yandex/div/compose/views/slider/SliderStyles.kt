package com.yandex.div.compose.views.slider

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toPx
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSlider
import com.yandex.div2.DivStroke
import com.yandex.div2.DivStrokeStyle
import kotlin.math.max
import kotlin.math.min

internal class SliderStyles(
    val thumb: SliderDrawable,
    val secondaryThumb: SliderDrawable?,
    val activeTrack: SliderDrawable,
    val inactiveTrack: SliderDrawable,
    val activeTickMark: SliderDrawable?,
    val inactiveTickMark: SliderDrawable?,
    val thumbText: SliderTextStyle?,
    val secondaryThumbText: SliderTextStyle?,
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

    fun rangeBounds(trackLength: Float, trackStart: Float, isRtl: Boolean): List<SliderRangeBounds> {
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

internal class SliderTextStyle(
    val fontSize: Float,
    val letterSpacing: Float,
    val typeface: Typeface,
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

@Composable
internal fun DivSlider.observeSliderStyles(range: SliderRange, hasSecondary: Boolean): SliderStyles {
    val activeTrack = trackActiveStyle.observeDrawable()
    val inactiveTrack = trackInactiveStyle.observeDrawable()
    return SliderStyles(
        thumb = thumbStyle.observeDrawable(),
        secondaryThumb = if (hasSecondary) (thumbSecondaryStyle ?: thumbStyle).observeDrawable() else null,
        activeTrack = activeTrack,
        inactiveTrack = inactiveTrack,
        activeTickMark = tickMarkActiveStyle?.observeDrawable(),
        inactiveTickMark = tickMarkInactiveStyle?.observeDrawable(),
        thumbText = thumbTextStyle?.observeTextStyle(),
        secondaryThumbText = thumbSecondaryTextStyle?.observeTextStyle(),
        ranges = ranges?.map {
            it.observeRange(
                activeTrack,
                inactiveTrack,
                minValue,
                maxValue
            )
        }.orEmpty(),
        range = range
    )
}

@Composable
private fun DivSlider.Range.observeRange(
    defaultActiveTrackStyle: SliderDrawable,
    defaultInactiveTrackStyle: SliderDrawable,
    minValue: Expression<Long>,
    maxValue: Expression<Long>
): SliderVisualRange {
    val (marginStart, marginEnd) = margins.observeHorizontalInsets()
    return SliderVisualRange(
        start = (start ?: minValue).observedValue().toFloat(),
        end = (end ?: maxValue).observedValue().toFloat(),
        marginStart = marginStart.toPx(),
        marginEnd = marginEnd.toPx(),
        activeTrackStyle = trackActiveStyle?.observeDrawable() ?: defaultActiveTrackStyle,
        inactiveTrackStyle = trackInactiveStyle?.observeDrawable() ?: defaultInactiveTrackStyle
    )
}

@Composable
private fun DivDrawable.observeDrawable(): SliderDrawable {
    val shapeDrawable = (this as DivDrawable.Shape).value
    return when (val shape = shapeDrawable.shape) {
        is DivShape.RoundedRectangle -> shape.value.observeDrawable(shapeDrawable)
        is DivShape.Circle -> shape.value.observeDrawable(shapeDrawable)
    }
}

@Composable
private fun DivRoundedRectangleShape.observeDrawable(drawable: DivShapeDrawable): SliderDrawable {
    return SliderDrawable(
        width = itemWidth.observedValue().toPx(),
        height = itemHeight.observedValue().toPx(),
        color = (backgroundColor ?: drawable.color).observedColorValue(),
        radius = cornerRadius.observedValue().toPx(),
        stroke = (stroke ?: drawable.stroke)?.observeStroke()
    )
}

@Composable
private fun DivCircleShape.observeDrawable(drawable: DivShapeDrawable): SliderDrawable {
    val radius = radius.observedValue().toPx()
    return SliderDrawable(
        width = radius * 2f,
        height = radius * 2f,
        color = (backgroundColor ?: drawable.color).observedColorValue(),
        radius = radius,
        stroke = (stroke ?: drawable.stroke)?.observeStroke()
    )
}

@Composable
private fun DivStroke.observeStroke(): SliderStroke {
    return SliderStroke(
        color = color.observedColorValue(),
        width = width.observedPxValue(unit),
        pathEffect = when (style) {
            is DivStrokeStyle.Dashed -> PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 2.dp.toPx()))
            is DivStrokeStyle.Solid -> null
        }
    )
}

@Composable
private fun DivSlider.TextStyle.observeTextStyle(): SliderTextStyle {
    val size = fontSize.observedPxValue(fontSizeUnit)
    val weight = fontWeightValue?.observedIntValue()
        ?: fontWeight?.observedValue()?.toTypefaceWeight()
        ?: Typeface.NORMAL
    return SliderTextStyle(
        fontSize = size,
        letterSpacing = letterSpacing.observedFloatValue() / fontSize.observedValue().coerceAtLeast(1L),
        typeface = Typeface.create(fontFamily?.observedValue(), weight),
        offsetX = offset?.x?.observedPxValue() ?: 0f,
        offsetY = offset?.y?.observedPxValue() ?: 0f,
        color = textColor.observedValue()
    )
}

private fun DivFontWeight.toTypefaceWeight(): Int = when (this) {
    DivFontWeight.LIGHT,
    DivFontWeight.REGULAR,
    DivFontWeight.MEDIUM -> Typeface.NORMAL
    DivFontWeight.BOLD -> Typeface.BOLD
}
