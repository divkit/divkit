package com.yandex.div.internal.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal interface SingleIndicatorDrawer {

    fun draw(canvas: Canvas, x: Float, y: Float, itemSize: IndicatorParams.ItemSize, color: Int)

    fun drawSelected(canvas: Canvas, rect: RectF)

}

internal fun getIndicatorDrawer(style: IndicatorParams.Style): SingleIndicatorDrawer = when (style.shape) {
    is IndicatorParams.Shape.RoundedRect -> RoundedRect(style)
    is IndicatorParams.Shape.Circle -> Circle(style)
}
