package com.yandex.div.internal.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div.render.common.gradient.GradientGeometry

/**
 * Gradient drawable with different angle. (In android linear gradient drawable angle is multiple 45)
 */
internal class LinearGradientDrawable(
    private val angle: Float,
    private val colormap: Colormap
) : Drawable() {

    private val paint = Paint()
    private var rect = RectF()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        bounds.let {
            paint.shader = createLinearGradient(
                angle,
                colormap.colors,
                colormap.positions,
                bounds.width(),
                bounds.height()
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

        fun createLinearGradient(
            angle: Float,
            colors: IntArray,
            positions: FloatArray?,
            width: Int,
            height: Int
        ): LinearGradient {
            return GradientGeometry.linearEndpoints(
                angleDegrees = angle.toDouble(),
                width = width.toFloat(),
                height = height.toFloat(),
            ) { startX, startY, endX, endY ->
                LinearGradient(startX, startY, endX, endY, colors, positions, Shader.TileMode.CLAMP)
            }
        }
    }
}
