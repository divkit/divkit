package com.yandex.div.compose.utils.gradient

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import com.yandex.div.render.common.gradient.AnimatedGradientMath
import com.yandex.div.render.common.gradient.GradientGeometry

@Immutable
internal data class LinearGradientBrush(
    private val angle: Int,
    private val colors: List<Color>,
    private val colorStops: List<Float>?,
    private val animationPhase: Float?,
) : ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val width = size.width
        val height = size.height
        val translation = animationPhase?.let { phase ->
            AnimatedGradientMath.linearTranslation(
                angleDegrees = angle.toFloat(),
                width = width,
                height = height,
                phase = phase,
            ) { x, y -> Offset(x, y) }
        } ?: Offset.Zero
        return GradientGeometry.linearEndpoints(
            angleDegrees = angle.toDouble(),
            width = width,
            height = height,
        ) { startX, startY, endX, endY ->
            LinearGradientShader(
                from = Offset(startX, startY) + translation,
                to = Offset(endX, endY) + translation,
                colors = colors,
                colorStops = colorStops,
                tileMode = TileMode.Clamp,
            )
        }
    }
}
