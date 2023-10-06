package com.yandex.div.internal.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import androidx.core.graphics.toRect
import com.yandex.div.internal.Assert

class RoundedRectDrawable(private val params: Params) : Drawable() {

    data class Params(
        @Px val width: Float,
        @Px val height: Float,
        val color: Int,
        @Px val radius: Float,
        val strokeColor: Int? = null,
        @Px val strokeWidth: Float? = null
    )

    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = params.color
    }

    private val strokePaint: Paint?
    private val strokeOffset: Float
    private val offset: Float

    private val radiusX = params.radius.considerSize(params.height)
    private val radiusY = params.radius.considerSize(params.width)

    private fun Float.considerSize(size: Float) = this - if (this >= size / 2) strokeOffset else 0f

    private val rect = RectF(0f, 0f, params.width, params.height)

    init {
        if (params.strokeColor != null && params.strokeWidth != null) {
            strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = params.strokeColor
                strokeWidth = params.strokeWidth
            }
            strokeOffset = params.strokeWidth / 2
            offset = 1f
        } else {
            strokePaint = null
            strokeOffset = 0f
            offset = 0f
        }

        bounds = rect.toRect()
    }

    override fun getIntrinsicHeight(): Int {
        return params.height.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return params.width.toInt()
    }

    override fun draw(canvas: Canvas) {
        setRectWithOffset(offset)
        canvas.drawRoundRect(rect, radiusX, radiusY, mainPaint)
        strokePaint?.let {
            setRectWithOffset(strokeOffset)
            canvas.drawRoundRect(rect, params.radius, params.radius, it)
        }
    }

    private fun setRectWithOffset(offset: Float) {
        with (bounds) {
            rect.set(left + offset, top + offset, right - offset, bottom - offset)
        }
    }

    override fun setAlpha(alpha: Int) {
        Assert.fail("Setting alpha is not implemented")
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        Assert.fail("Setting color filter is not implemented")
    }

    override fun getOpacity() = PixelFormat.OPAQUE
}
