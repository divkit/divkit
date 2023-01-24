package com.yandex.div.internal.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal class RoundedRect(private val params: IndicatorParams.Style) : SingleIndicatorDrawer {

    private val paint = Paint()
    private val shape = params.inactiveShape as IndicatorParams.Shape.RoundedRect
    private val rect = RectF(0f, 0f, shape.itemSize.width, shape.itemSize.height)

    override fun draw(canvas: Canvas, x: Float, y: Float, itemSize: IndicatorParams.ItemSize, color: Int) {
        val rectSize = itemSize as IndicatorParams.ItemSize.RoundedRect
        paint.color = color
        rect.apply {
            left = x - rectSize.itemWidth / 2f
            top = y - rectSize.itemHeight / 2f
            right = x + rectSize.itemWidth / 2f
            bottom = y + rectSize.itemHeight / 2f
        }
        canvas.drawRoundRect(rect, rectSize.cornerRadius, rectSize.cornerRadius, paint)
    }

    override fun drawSelected(canvas: Canvas, rect: RectF) {
        val rectSize = params.activeShape.itemSize as IndicatorParams.ItemSize.RoundedRect
        paint.color = params.activeShape.color

        canvas.drawRoundRect(rect, rectSize.cornerRadius, rectSize.cornerRadius, paint)
    }
}
