package com.yandex.div.internal.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

typealias CordX = Float
typealias CordY = Float

internal class RadialGradientDrawable(
        var radius: Radius,
        var centerX: Center,
        var centerY: Center,
        var colors: IntArray
) : Drawable() {

    private val paint = Paint()
    private var rect = RectF()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        bounds.let {
            paint.shader = createRadialGradient(
                radius = radius,
                centerX = centerX,
                centerY = centerY,
                colors = colors,
                width = bounds.width(),
                height = bounds.height()
            )
            rect.set(bounds)
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int = paint.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    companion object {

        private const val MIN_GRADIENT_RADIUS = 0.01f

        fun createRadialGradient(
                radius: Radius,
                centerX: Center,
                centerY: Center,
                colors: IntArray,
                width: Int,
                height: Int
        ): RadialGradient {
            fun Center.value(parentSize: Int) = when (this) {
                is Center.Fixed -> this.value
                is Center.Relative -> this.value * parentSize
            }

            val absoluteCenterX = centerX.value(width)
            val absoluteCenterY = centerY.value(height)

            val rightCord = width.toFloat()
            val leftCord = 0.0f
            val topCord = 0.0f
            val bottomCord = height.toFloat()

            fun distTo(x: CordX, y: CordY): Float =
                sqrt((absoluteCenterX - x).pow(2) + (absoluteCenterY - y).pow(2))

            fun distToVerticalSide(x: CordX): Float = abs(absoluteCenterX - x)
            fun distToHorizontalSide(y: CordY): Float = abs(absoluteCenterY - y)

            val distancesToCorners by lazy {
                arrayOf(
                    distTo(leftCord, topCord),
                    distTo(rightCord, topCord),
                    distTo(rightCord, bottomCord),
                    distTo(leftCord, bottomCord),
                )
            }
            val distancesToSides by lazy {
                arrayOf(
                    distToVerticalSide(leftCord),
                    distToVerticalSide(rightCord),
                    distToHorizontalSide(bottomCord),
                    distToHorizontalSide(topCord),
                )
            }

            val radiusPx = when (radius) {
                is Radius.Fixed -> radius.value
                is Radius.Relative -> {
                    when (radius.type) {
                        Radius.Relative.Type.NEAREST_CORNER -> distancesToCorners.minOrNull()!!
                        Radius.Relative.Type.FARTHEST_CORNER -> distancesToCorners.maxOrNull()!!
                        Radius.Relative.Type.NEAREST_SIDE -> distancesToSides.minOrNull()!!
                        Radius.Relative.Type.FARTHEST_SIDE -> distancesToSides.maxOrNull()!!
                    }
                }
            }

            return RadialGradient(
                absoluteCenterX,
                absoluteCenterY,
                if (radiusPx > 0.0f) radiusPx else MIN_GRADIENT_RADIUS,
                colors,
                null,
                Shader.TileMode.CLAMP
            )
        }

    }

    sealed class Radius {
        data class Relative(val type: Type) : Radius() {
            enum class Type {
                NEAREST_CORNER, FARTHEST_CORNER, NEAREST_SIDE, FARTHEST_SIDE
            }
        }

        data class Fixed(val value: Float) : Radius()
    }

    sealed class Center {
        data class Relative(val value: Float) : Center()
        data class Fixed(val value: Float) : Center()
    }
}
