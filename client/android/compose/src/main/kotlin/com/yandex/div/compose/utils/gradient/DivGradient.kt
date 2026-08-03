package com.yandex.div.compose.utils.gradient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.gradient.RadialGradientBrush.Center
import com.yandex.div.compose.utils.gradient.RadialGradientBrush.Radius
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toColor
import com.yandex.div.compose.utils.toPx
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius

@Composable
internal fun DivLinearGradient.observeLinearGradient(): Brush? {
    val points = colorMap?.map { it.position.observedFloatValue() to it.color.observedColorValue() }
    val colorMap = observedColorMap(points, colors) ?: return null
    return LinearGradientBrush(
        angle = angle.observedIntValue(),
        colorMap = colorMap
    )
}

@Composable
internal fun DivRadialGradient.observeRadialGradient(): Brush? {
    val points = colorMap?.map { it.position.observedFloatValue() to it.color.observedColorValue() }
    val colorMap = observedColorMap(points, colors) ?: return null
    return RadialGradientBrush(
        centerX = centerX.observeCenter(),
        centerY = centerY.observeCenter(),
        radius = radius.observeRadius(),
        colorMap = colorMap
    )
}

@Composable
private fun observedColorMap(
    points: List<Pair<Float, Color>>?,
    fallbackColors: ExpressionList<Int>?,
): ColorMap? {
    if (points != null) {
        val sorted = points.sortedBy { it.first }
        return ColorMap(
            colors = IntArray(sorted.size) { sorted[it].second.toArgb() },
            positions = FloatArray(sorted.size) { sorted[it].first },
        )
    }

    val colors = fallbackColors?.observedValue()
        ?.map { it.toColor() }
        ?: return null
    return ColorMap(
        colors = IntArray(colors.size) { colors[it].toArgb() },
        positions = null,
    )
}

@Composable
private fun DivRadialGradientCenter.observeCenter(): Center {
    return when (this) {
        is DivRadialGradientCenter.Fixed ->
            Center.Fixed(value.value.observedPxValue(value.unit))

        is DivRadialGradientCenter.Relative ->
            Center.Relative(value.value.observedFloatValue())
    }
}

@Composable
private fun DivRadialGradientRadius.observeRadius(): Radius {
    return when (this) {
        is DivRadialGradientRadius.FixedSize ->
            Radius.Fixed(value.observedValue().toPx())

        is DivRadialGradientRadius.Relative ->
            Radius.Relative(value.value.observedValue())
    }
}
