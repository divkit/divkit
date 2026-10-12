package com.yandex.div.internal.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.graphics.Insets
import com.yandex.div.core.images.toNinePatch

internal class NinePatchDrawable : Drawable() {

    var bottom: Int = 0
        set(value) {
            field = value
            invalidateSelf()
        }
    var left: Int = 0
        set(value) {
            field = value
            invalidateSelf()
        }
    var right: Int = 0
        set(value) {
            field = value
            invalidateSelf()
        }
    var top: Int = 0
        set(value) {
            field = value
            invalidateSelf()
        }

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            ninePatch = value?.let { buildNinePatchFromBitmap(it) }
            invalidateSelf()
        }

    private var ninePatch: NinePatch? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    override fun setAlpha(value: Int) {
        paint.alpha = value
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun getOpacity(): Int = paint.alpha

    override fun draw(canvas: Canvas) {
        ninePatch?.draw(canvas, Rect(0, 0, bounds.width(), bounds.height()), paint)
    }

    private fun buildNinePatchFromBitmap(bitmap: Bitmap) =
        bitmap.toNinePatch(Insets.of(left, top, right, bottom))
}
