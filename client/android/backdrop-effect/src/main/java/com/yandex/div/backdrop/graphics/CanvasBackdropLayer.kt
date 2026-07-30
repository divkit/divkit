package com.yandex.div.backdrop.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.yandex.div.backdrop.util.getCoordinateOffset
import com.yandex.div.core.util.bitmap.blur.LowApiBitmapEffectHelper

internal class CanvasBackdropLayer(view: View) : BackdropLayer(view) {

    private var blurRadius: Float = 0.0f

    private val blurHelper by lazy(LazyThreadSafetyMode.NONE) { LowApiBitmapEffectHelper(view.context) }
    private var backdropBitmap: Bitmap? = null

    override fun setCornerRadius(radius: Float) = Unit

    override fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) = Unit

    override fun setBlurEffect(radius: Float) {
        blurRadius = radius
    }

    override fun setRefractionEffect(height: Float, strength: Float, chromaticAberration: Boolean) = Unit

    override fun setColorAdjustment(brightness: Float, contrast: Float, saturation: Float) = Unit

    override fun capture(backdropView: View) {
        backdropBitmap = getBackdropBitmap(view)?.applyCanvas {
            val (dx, dy) = view.getCoordinateOffset(backdropView)
            translate(-dx.toFloat(), -dy.toFloat())
            backdropView.draw(this)
        }?.let {
            blurHelper.blurBitmap(it, blurRadius)
        }
    }

    private fun getBackdropBitmap(view: View): Bitmap? {
        if (view.width == 0 || view.height == 0) return null

        val bitmap = backdropBitmap ?: return createBitmap(view.width, view.height)

        if (bitmap.width != view.width || bitmap.height != view.height) {
            bitmap.recycle()
            return createBitmap(view.width, view.height)
        }

        return bitmap
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        backdropBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
        }
    }

    override fun recycle() {
        backdropBitmap?.recycle()
        backdropBitmap = null
    }

    override fun close() = Unit
}
