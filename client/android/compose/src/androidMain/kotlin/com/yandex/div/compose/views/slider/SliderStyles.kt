package com.yandex.div.compose.views.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.text.FontParams
import com.yandex.div.compose.text.rememberFontFamily
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.toPx
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSlider
import com.yandex.div2.DivStroke
import com.yandex.div2.DivStrokeStyle

@Composable
internal fun DivSlider.observeSliderStyles(
    range: SliderRange,
    hasSecondary: Boolean
): SliderStyles? {
    val activeTrack = trackActiveStyle.observeDrawable() ?: return null
    val inactiveTrack = trackInactiveStyle.observeDrawable() ?: return null
    val thumb = thumbStyle.observeDrawable() ?: return null
    return SliderStyles(
        thumb = thumb,
        secondaryThumb = if (hasSecondary) {
            (thumbSecondaryStyle ?: thumbStyle).observeDrawable()
        } else {
            null
        },
        activeTrack = activeTrack,
        inactiveTrack = inactiveTrack,
        activeTickMark = tickMarkActiveStyle?.observeDrawable(),
        inactiveTickMark = tickMarkInactiveStyle?.observeDrawable(),
        thumbText = thumbTextStyle?.rememberTextPainter(),
        secondaryThumbText =
            if (hasSecondary) thumbSecondaryTextStyle?.rememberTextPainter() else null,
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
private fun DivDrawable.observeDrawable(): SliderDrawable? {
    when (val drawable = this) {
        is DivDrawable.Shape -> {
            val shapeDrawable = drawable.value
            return when (val shape = shapeDrawable.shape) {
                is DivShape.RoundedRectangle -> shape.value.observeDrawable(shapeDrawable)
                is DivShape.Circle -> shape.value.observeDrawable(shapeDrawable)
            }
        }
    }
}

@Composable
private fun DivRoundedRectangleShape.observeDrawable(drawable: DivShapeDrawable): SliderDrawable? {
    val color = (backgroundColor ?: drawable.color)?.observedColorValue()
    if (color == null) {
        reportError("Drawable color not defined")
        return null
    }
    return SliderDrawable(
        width = itemWidth.observedValue().toPx(),
        height = itemHeight.observedValue().toPx(),
        color = color,
        radius = cornerRadius.observedValue().toPx(),
        stroke = (stroke ?: drawable.stroke)?.observeStroke()
    )
}

@Composable
private fun DivCircleShape.observeDrawable(drawable: DivShapeDrawable): SliderDrawable? {
    val color = (backgroundColor ?: drawable.color)?.observedColorValue()
    if (color == null) {
        reportError("Drawable color not defined")
        return null
    }

    val radius = radius.observedValue().toPx()
    return SliderDrawable(
        width = radius * 2f,
        height = radius * 2f,
        color = color,
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
            is DivStrokeStyle.Dashed -> PathEffect.dashPathEffect(
                floatArrayOf(
                    6.dp.toPx(),
                    2.dp.toPx()
                )
            )

            is DivStrokeStyle.Solid -> null
        }
    )
}

@Composable
private fun DivSlider.TextStyle.rememberTextPainter(): SliderTextPainter {
    val fontParams = FontParams(
        family = fontFamily?.observedValue(),
        weight = fontWeight?.observedValue(),
        weightValue = fontWeightValue?.observedIntValue(),
        variationSettings = fontVariationSettings?.observedValue(),
    )
    val style = SliderTextStyle(
        fontSize = fontSize.observedPxValue(fontSizeUnit),
        letterSpacing = letterSpacing.observedFloatValue() / fontSize.observedValue()
            .coerceAtLeast(1L),
        fontFamily = rememberFontFamily(fontParams),
        fontWeight = fontParams.weight ?: FontWeight.Normal,
        offsetX = offset?.x?.observedPxValue() ?: 0f,
        offsetY = offset?.y?.observedPxValue() ?: 0f,
        color = textColor.observedValue()
    )
    return LocalDivContext.current.component.sliderTextPainterFactory.rememberPainter(style)
}
