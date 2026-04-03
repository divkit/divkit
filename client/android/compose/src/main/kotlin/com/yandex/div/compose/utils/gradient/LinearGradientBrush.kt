package com.yandex.div.compose.utils.gradient

import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class LinearGradientBrush(
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