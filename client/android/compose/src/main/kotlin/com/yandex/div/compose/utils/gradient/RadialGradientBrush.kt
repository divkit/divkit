package com.yandex.div.compose.utils.gradient

import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import com.yandex.div2.DivRadialGradientRelativeRadius
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Immutable
internal data class RadialGradientBrush(
    private val centerX: Center,
    private val centerY: Center,
    private val radius: Radius,
    private val colorMap: ColorMap
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
        data class Relative(val type: DivRadialGradientRelativeRadius.Value) : Radius()
    }

    override fun createShader(size: Size): Shader {
        val width = size.width
        val height = size.height

        val centerX = when (centerX) {
            is Center.Fixed -> centerX.value
            is Center.Relative -> centerX.fraction * width
        }
        val centerY = when (centerY) {
            is Center.Fixed -> centerY.value
            is Center.Relative -> centerY.fraction * height
        }

        val radius = when (radius) {
            is Radius.Fixed -> radius.value
            is Radius.Relative -> when (radius.type) {
                DivRadialGradientRelativeRadius.Value.NEAREST_CORNER -> minOf(
                    distTo(centerX, centerY, 0f, 0f), distTo(centerX, centerY, width, 0f),
                    distTo(centerX, centerY, width, height), distTo(centerX, centerY, 0f, height),
                )

                DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER -> maxOf(
                    distTo(centerX, centerY, 0f, 0f), distTo(centerX, centerY, width, 0f),
                    distTo(centerX, centerY, width, height), distTo(centerX, centerY, 0f, height),
                )

                DivRadialGradientRelativeRadius.Value.NEAREST_SIDE -> minOf(
                    distToVerticalSide(centerX, 0f), distToVerticalSide(centerX, width),
                    distToHorizontalSide(centerY, 0f), distToHorizontalSide(centerY, height),
                )

                DivRadialGradientRelativeRadius.Value.FARTHEST_SIDE -> maxOf(
                    distToVerticalSide(centerX, 0f), distToVerticalSide(centerX, width),
                    distToHorizontalSide(centerY, 0f), distToHorizontalSide(centerY, height),
                )
            }
        }

        return RadialGradient(
            centerX,
            centerY,
            radius.coerceAtLeast(MIN_GRADIENT_RADIUS),
            colorMap.colors,
            colorMap.positions,
            Shader.TileMode.CLAMP
        )
    }

    private fun distTo(centerX: Float, centerY: Float, x: Float, y: Float): Float {
        return sqrt((centerX - x).pow(2) + (centerY - y).pow(2))
    }

    private fun distToVerticalSide(centerX: Float, x: Float): Float {
        return abs(centerX - x)
    }

    private fun distToHorizontalSide(centerY: Float, y: Float): Float {
        return abs(centerY - y)
    }

    private companion object {
        const val MIN_GRADIENT_RADIUS = 0.01f
    }
}
