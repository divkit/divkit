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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
            val halfWidth = width / 2.0f
            val halfHeight = height / 2.0f
            val angleRad = angle.toRadian()
            val gradientWidth = abs(width * cos(angleRad)) + abs(height * sin(angleRad))
            val widthDelta = (cos(angleRad) * gradientWidth / 2.0f).snap(to = 0.0f)
            val heightDelta = (sin(angleRad) * gradientWidth / 2.0f).snap(to = 0.0f)
            return LinearGradient(halfWidth - widthDelta, halfHeight + heightDelta,
                halfWidth + widthDelta, halfHeight - heightDelta,
                colors, positions, Shader.TileMode.CLAMP)
        }

        private fun Float.toRadian() = (this * PI / 180f).toFloat()
    }
}

private fun Float.snap(to: Float, sensitivity: Float = 0.0001f): Float {
    return if (abs(to - this) <= sensitivity) to else this
}
