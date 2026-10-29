package com.yandex.div.internal.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import com.yandex.div.render.common.gradient.GradientGeometry
import com.yandex.div.render.common.gradient.GradientGeometry.RelativeRadius

typealias CordX = Float
typealias CordY = Float

internal class RadialGradientDrawable(
        var radius: Radius,
        var centerX: Center,
        var centerY: Center,
        var colors: IntArray,
        var positions: FloatArray? = null,
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
                positions = positions,
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
                positions: FloatArray?,
                width: Int,
                height: Int
        ): RadialGradient {
            fun Center.value(parentSize: Int) = when (this) {
                is Center.Fixed -> GradientGeometry.absoluteCenter(
                    value = value,
                    isRelative = false,
                    parentSize = parentSize.toFloat(),
                )
                is Center.Relative -> GradientGeometry.absoluteCenter(
                    value = value,
                    isRelative = true,
                    parentSize = parentSize.toFloat(),
                )
            }

            val absoluteCenterX = centerX.value(width)
            val absoluteCenterY = centerY.value(height)

            val radiusPx = when (radius) {
                is Radius.Fixed -> radius.value
                is Radius.Relative -> GradientGeometry.relativeRadius(
                    type = when (radius.type) {
                        Radius.Relative.Type.NEAREST_CORNER -> RelativeRadius.NEAREST_CORNER
                        Radius.Relative.Type.FARTHEST_CORNER -> RelativeRadius.FARTHEST_CORNER
                        Radius.Relative.Type.NEAREST_SIDE -> RelativeRadius.NEAREST_SIDE
                        Radius.Relative.Type.FARTHEST_SIDE -> RelativeRadius.FARTHEST_SIDE
                    },
                    centerX = absoluteCenterX,
                    centerY = absoluteCenterY,
                    width = width.toFloat(),
                    height = height.toFloat(),
                )
            }

            return RadialGradient(
                absoluteCenterX,
                absoluteCenterY,
                if (radiusPx > 0.0f) radiusPx else MIN_GRADIENT_RADIUS,
                colors,
                positions,
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
