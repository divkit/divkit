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
        val strokeWidth: Float? = null
    )

    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = params.color
    }
    private val strokePaint = if (params.strokeColor != null && params.strokeWidth != null)
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = params.strokeColor
            strokeWidth = params.strokeWidth
        }
    else
        null

    private val rect = RectF(0f, 0f, params.width, params.height)
    init {
        bounds = rect.toRect()
    }

    override fun getIntrinsicHeight(): Int {
        return params.height.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return params.width.toInt()
    }

    override fun draw(canvas: Canvas) {
        mainPaint.color = params.color
        rect.set(bounds)
        canvas.drawRoundRect(rect, params.radius, params.radius, mainPaint)
        if (strokePaint != null) {
            canvas.drawRoundRect(rect, params.radius, params.radius, strokePaint)
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
