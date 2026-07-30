package com.yandex.div.backdrop.graphics

import android.graphics.Canvas
import android.graphics.Paint

internal sealed class HighlightLayer {
    abstract fun setSize(width: Float, height: Float)
    abstract fun setCornerRadius(radius: Float)
    abstract fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float)
    abstract fun setHighlightEffect(rimWidth: Float, alpha: Float, color: Int, angle: Float, falloff: Float)
    abstract fun setHighlightEffect(rimWidth: Float, alpha: Float, intensity: Float, angle: Float)
    abstract fun draw(canvas: Canvas, paint: Paint)
}
