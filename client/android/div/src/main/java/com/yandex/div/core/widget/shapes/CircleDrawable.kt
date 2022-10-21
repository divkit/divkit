package com.yandex.div.core.widget.shapes

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import androidx.core.graphics.toRect
import com.yandex.div.core.util.Assert

class CircleDrawable(private val params: Params) : Drawable() {

    data class Params(
        @Px val radius: Float,
        val color: Int,
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

    override fun getIntrinsicHeight(): Int {
        return 2 * params.radius.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return 2 * params.radius.toInt()
    }

    private val rect = RectF(0f, 0f, params.radius * 2, params.radius * 2)

    init {
        bounds = rect.toRect()
    }

    override fun draw(canvas: Canvas) {
        mainPaint.color = params.color
        rect.set(bounds)
        canvas.drawCircle(rect.centerX(), rect.centerY(), params.radius, mainPaint)
        if (strokePaint != null) {
            canvas.drawCircle(rect.centerX(), rect.centerY(), params.radius, strokePaint)
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
