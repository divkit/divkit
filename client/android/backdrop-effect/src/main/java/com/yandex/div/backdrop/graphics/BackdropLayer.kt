package com.yandex.div.backdrop.graphics

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.yandex.div.core.Disposable

internal sealed class BackdropLayer(
    protected val view: View
) : Disposable {
    abstract fun setCornerRadius(radius: Float)
    abstract fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float)
    abstract fun setBlurEffect(radius: Float)
    abstract fun setRefractionEffect(height: Float, strength: Float, chromaticAberration: Boolean)
    abstract fun setColorAdjustment(brightness: Float, contrast: Float, saturation: Float)
    abstract fun capture(backdropView: View)
    abstract fun draw(canvas: Canvas, paint: Paint)
    abstract fun recycle()
    abstract override fun close()
}
