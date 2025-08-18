package com.yandex.div.internal.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal class RoundedRect(private val params: IndicatorParams.Style) : SingleIndicatorDrawer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
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
        val rectSize = itemSize as IndicatorParams.ItemSize.RoundedRect
        paint.color = color
        rect.apply {
            left = x - rectSize.itemWidth / 2f
            top = y - rectSize.itemHeight / 2f
            right = x + rectSize.itemWidth / 2f
            bottom = y + rectSize.itemHeight / 2f

            if (strokeWidth > 0f) {
                // to fit supplied sizes we need to shrink rect by half of stroke so final
                // stroke will perfectly fit bounds
                val shrinkBy = strokeWidth / 2f
                left += shrinkBy
                top += shrinkBy
                right -= shrinkBy
                bottom -= shrinkBy
            }
        }
        canvas.drawRoundRect(rect, rectSize.cornerRadius, rectSize.cornerRadius, paint)
        if (strokeColor != Color.TRANSPARENT && strokeWidth != 0f) {
            strokePaint.apply {
                this.color = strokeColor
                this.strokeWidth = strokeWidth
            }

            canvas.drawRoundRect(rect, rectSize.cornerRadius, rectSize.cornerRadius, strokePaint)
        }
    }

    override fun drawSelected(canvas: Canvas, rect: RectF) {
        val shape = params.activeShape as IndicatorParams.Shape.RoundedRect
        val rectSize = shape.itemSize
        paint.color = params.activeShape.color

        canvas.drawRoundRect(rect, rectSize.cornerRadius, rectSize.cornerRadius, paint)
        if (shape.strokeColor != Color.TRANSPARENT && shape.strokeWidth != 0f) {
            strokePaint.apply {
                color = shape.strokeColor
                strokeWidth = shape.strokeWidth
            }
            canvas.drawRoundRect(rect, rectSize.cornerRadius, rectSize.cornerRadius, strokePaint)
        }
    }
}
