package com.yandex.div.compose.utils.gradient

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import com.yandex.div.render.common.gradient.AnimatedGradientMath
import com.yandex.div.render.common.gradient.GradientGeometry
import com.yandex.div.render.common.gradient.GradientGeometry.RelativeRadius

@Immutable
internal data class RadialGradientBrush(
    private val centerX: Center,
    private val centerY: Center,
    private val radius: Radius,
    private val colors: List<Color>,
    private val colorStops: List<Float>?,
    private val animationPhase: Float?,
) : ShaderBrush() {

    @Immutable
    sealed class Center {
        @Immutable
        data class Fixed(val value: Float) : Center()

        @Immutable
        data class Relative(val fraction: Float) : Center()
    }

    @Immutable
    sealed class Radius {
        @Immutable
        data class Fixed(val value: Float) : Radius()

        @Immutable
        data class Relative(val type: RelativeRadius) : Radius()
    }

    override fun createShader(size: Size): Shader = withGeometry(size) { center, radius ->
        RadialGradientShader(
            center = center,
            radius = radius,
            colors = colors,
            colorStops = colorStops,
            tileMode = TileMode.Clamp,
        )
    }

    internal inline fun <T> withGeometry(size: Size, consume: (center: Offset, radius: Float) -> T): T {
        val width = size.width
        val height = size.height

        val centerX = centerX.absoluteValue(width)
        val centerY = centerY.absoluteValue(height)

        val radius = when (radius) {
            is Radius.Fixed -> radius.value
            is Radius.Relative -> GradientGeometry.relativeRadius(
                type = radius.type,
                centerX = centerX,
                centerY = centerY,
                width = width,
                height = height,
            )
        }

        val translationX = animationPhase
            ?.let { AnimatedGradientMath.radialTranslation(width = width, phase = it) }
            ?: 0f
        return consume(Offset(centerX + translationX, centerY), radius.coerceAtLeast(MIN_RADIUS))
    }

    private fun Center.absoluteValue(parentSize: Float): Float = when (this) {
        is Center.Fixed -> GradientGeometry.absoluteCenter(
            value = value,
            isRelative = false,
            parentSize = parentSize,
        )

        is Center.Relative -> GradientGeometry.absoluteCenter(
            value = fraction,
            isRelative = true,
            parentSize = parentSize,
        )
    }
}

private const val MIN_RADIUS = 0.01f
