package com.yandex.div.compose.utils.gradient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toColor
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toPx
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius

@Composable
internal fun DivLinearGradient.observeLinearGradient(): Brush? {
    val angle = angle.observedValue().toFloat()
    val points = colorMap?.map { it.position.observedFloatValue() to it.color.observedColorValue() }
    val colorMap = resolveColorMap(points, colors) ?: return null
    return LinearGradientBrush(angle, colorMap.colors, colorMap.positions)
}

@Composable
internal fun DivRadialGradient.observeRadialGradient(): Brush? {
    val points = colorMap?.map { it.position.observedFloatValue() to it.color.observedColorValue() }
    val colorMap = resolveColorMap(points, colors) ?: return null
    val centerX = centerX.observeCenter()
    val centerY = centerY.observeCenter()
    val radius = radius.observeRadius()
    return RadialGradientBrush(centerX, centerY, radius, colorMap.colors, colorMap.positions)
}

private class ColorMap(val colors: IntArray, val positions: FloatArray?)

@Composable
private fun resolveColorMap(
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
    val colors = fallbackColors.observeGradientColors() ?: return null
    return ColorMap(
        colors = IntArray(colors.size) { colors[it].toArgb() },
        positions = null,
    )
}

@Composable
private fun ExpressionList<Int>?.observeGradientColors(): List<Color>? {
    val expressionColors = this ?: return null
    return expressionColors.observedValue().map { it.toColor() }
}

@Composable
private fun DivRadialGradientCenter.observeCenter(): RadialGradientBrush.Center {
    return when (this) {
        is DivRadialGradientCenter.Fixed ->
            RadialGradientBrush.Center.Fixed(
                value.value.observedValue().toDp(value.unit.observedValue()).toPx()
            )
        is DivRadialGradientCenter.Relative ->
            RadialGradientBrush.Center.Relative(value.value.observedFloatValue())
    }
}

@Composable
private fun DivRadialGradientRadius.observeRadius(): RadialGradientBrush.Radius {
    return when (this) {
        is DivRadialGradientRadius.FixedSize ->
            RadialGradientBrush.Radius.Fixed(
                value.value.observedValue().toDp(unit = value.unit.observedValue()).toPx()
            )
        is DivRadialGradientRadius.Relative ->
            RadialGradientBrush.Radius.Relative(value.value.observedValue())
    }
}
