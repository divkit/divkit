package com.yandex.div.compose.utils.gradient

import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Immutable
internal data class LinearGradientBrush(
    private val angle: Int,
    private val colorMap: ColorMap
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
            colorMap.colors,
            colorMap.positions,
            Shader.TileMode.CLAMP
        )
    }

    private fun Float.snapToZero(sensitivity: Float = 0.0001f): Float {
        return if (abs(this) <= sensitivity) 0f else this
    }
}
