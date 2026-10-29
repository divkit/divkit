package com.yandex.div.compose.utils.gradient

import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import com.yandex.div.render.common.gradient.GradientGeometry
import com.yandex.div.render.common.text.AnimatedTextGradientMath

@Immutable
internal data class LinearGradientBrush(
    private val angle: Int,
    private val colorMap: ColorMap,
    private val animationPhase: Float? = null,
) : ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val width = size.width
        val height = size.height
        return GradientGeometry.linearEndpoints(
            angleDegrees = angle.toDouble(),
            width = width,
            height = height,
        ) { startX, startY, endX, endY ->
            LinearGradient(
                startX,
                startY,
                endX,
                endY,
                colorMap.colors,
                colorMap.positions,
                Shader.TileMode.CLAMP,
            )
        }.apply {
            animationPhase?.let { phase ->
                val matrix = AnimatedTextGradientMath.linearTranslation(
                    angleDegrees = angle.toFloat(),
                    width = width,
                    height = height,
                    phase = phase,
                ) { x, y -> Matrix().apply { setTranslate(x, y) } }
                setLocalMatrix(matrix)
            }
        }
    }
}
