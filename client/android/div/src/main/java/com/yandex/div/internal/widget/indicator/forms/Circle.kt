package com.yandex.div.internal.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal class Circle(private val params: IndicatorParams.Style) : SingleIndicatorDrawer {

    private val paint = Paint()
    private val rect = RectF()

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        itemSize: IndicatorParams.ItemSize,
        color: Int,
        strokeWidth: Float,
        strokeColor: Int
    ) {
        val circleSize = itemSize as IndicatorParams.ItemSize.Circle
        paint.color = color
        rect.apply {
            left = x - circleSize.radius
            top = y - circleSize.radius
            right = x + circleSize.radius
            bottom = y + circleSize.radius
        }
        canvas.drawCircle(rect.centerX(), rect.centerY(), circleSize.radius, paint)
    }

    override fun drawSelected(canvas: Canvas, rect: RectF) {
        paint.color = params.activeShape.color

        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint)
    }
}
