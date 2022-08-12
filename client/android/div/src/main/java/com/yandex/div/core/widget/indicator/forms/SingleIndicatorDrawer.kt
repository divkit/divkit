package com.yandex.div.core.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.RectF

interface SingleIndicatorDrawer {

    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float, cornerRadius: Float, color: Int)

    fun drawSelected(canvas: Canvas, rect: RectF, cornerRadius: Float)
}
