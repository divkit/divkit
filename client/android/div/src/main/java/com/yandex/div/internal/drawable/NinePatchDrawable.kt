package com.yandex.div.internal.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val HEADER_WORDS = 8
private const val REGION_COUNT = 9
private const val NO_COLOR = 1

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

    private fun buildNinePatchFromBitmap(bitmap: Bitmap): NinePatch {
        // Android's nine-patch chunk has an eight-word header, four slice coordinates and nine
        // color hints.
        val chunk = ByteBuffer
            .allocate((HEADER_WORDS + 4 + REGION_COUNT) * Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
        chunk.put(1) // deserialized chunk
        chunk.put(2) // X divisions
        chunk.put(2) // Y divisions
        chunk.put(REGION_COUNT.toByte())
        repeat(HEADER_WORDS - 1) { chunk.putInt(0) } // array offsets and content padding
        chunk.putInt(left)
        chunk.putInt(bitmap.width - right)
        chunk.putInt(top)
        chunk.putInt(bitmap.height - bottom)
        repeat(REGION_COUNT) { chunk.putInt(NO_COLOR) }
        return NinePatch(bitmap, chunk.array())
    }
}
