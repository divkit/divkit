package com.yandex.div.core.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.yandex.div.core.widget.indicator.IndicatorParams

class RoundedRect(private val params: IndicatorParams.Style) : SingleIndicatorDrawer {
    private val paint = Paint().apply {
        color = params.color
    }
    private val rect = RectF(0f, 0f, params.normalWidth, params.normalHeight)

    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float, cornerRadius: Float, color: Int) {
        paint.color = color
        rect.apply {
            left = x - width / 2f
            top = y - height / 2f
            right = x + width / 2f
            bottom = y + height / 2f
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }

    override fun drawSelected(canvas: Canvas, rect: RectF, cornerRadius: Float) {
        paint.color = params.selectedColor

        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }
}
