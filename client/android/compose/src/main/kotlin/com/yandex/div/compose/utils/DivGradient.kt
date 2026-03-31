package com.yandex.div.compose.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import com.yandex.div2.DivLinearGradient
import kotlin.collections.map
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun DivLinearGradient.observeLinearGradient(): Brush? {
    val colors = observeColors() ?: return null
    val angle = angle.observedValue().toFloat()

    val colorMap = colorMap
    val positions: FloatArray?
    val colorsArray: IntArray

    if (colorMap != null) {
        val stops = colorMap.map { point ->
            point.position.observedValue().toFloat() to point.color.observedValue().toColor()
        }
        positions = FloatArray(stops.size) { stops[it].first }
        colorsArray = IntArray(stops.size) { stops[it].second.toArgb() }
    } else {
        positions = null
        colorsArray = IntArray(colors.size) { colors[it].toArgb() }
    }

    return LinearGradientBrush(angle, colorsArray, positions)
}

@Composable
private fun DivLinearGradient.observeColors(): List<Color>? {
    val expressionColors = colors ?: return null
    return expressionColors.observedValue().map { it.toColor() }
}

private class LinearGradientBrush(
    private val angle: Float,
    private val colors: IntArray,
    private val positions: FloatArray?,
) : ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val width = size.width
        val height = size.height
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val angleRad = (angle * Math.PI / 180.0).toFloat()
        val gradientLength = abs(width * cos(angleRad)) + abs(height * sin(angleRad))
        val widthDelta = (cos(angleRad) * gradientLength / 2f).snapToZero()
        val heightDelta = (sin(angleRad) * gradientLength / 2f).snapToZero()

        return LinearGradient(
            halfWidth - widthDelta,
            halfHeight + heightDelta,
            halfWidth + widthDelta,
            halfHeight - heightDelta,
            colors, positions, Shader.TileMode.CLAMP,
        )
    }

    private fun Float.snapToZero(sensitivity: Float = 0.0001f): Float {
        return if (abs(this) <= sensitivity) 0f else this
    }
}