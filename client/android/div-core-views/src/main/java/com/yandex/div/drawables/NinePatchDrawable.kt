package com.yandex.div.drawables

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import java.nio.ByteBuffer
import java.nio.ByteOrder

class NinePatchDrawable : Drawable() {

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
        NinePatch(bitmap, getChunkByteArray(bitmap.width, bitmap.height, bottom, left, right, top))

    private fun getChunkByteArray(
        imageWidth: Int,
        imageHeight: Int,
        bottomInset: Int,
        leftInset: Int,
        rightInset: Int,
        topInset: Int
    ): ByteArray {
        val topAxis = topInset
        val leftAxis = leftInset
        val bottomAxis = imageHeight - bottomInset
        val rightAxis = imageWidth - rightInset

        /**
         * 1 | 2 | 3
         * --+---+--
         * 4 | 5 | 6
         * --+---+--
         * 7 | 8 | 9
         */
        val regionsCount = 9

        /**
         * Map data - 1
         * Some magic bytes - 2
         * Paddings - 4
         * Skip - 1
         * Axes - 4
         * Regions - 9
         *
         * ...and multiply it all by 4 to get the allocation size
         */
        val allocationSize = (1 + 2 + 4 + 1 + 4 + 9) * 4

        return ByteBuffer.allocate(allocationSize)
            .order(ByteOrder.nativeOrder())
            .apply {
                // Was deserialized: $value.toBoolean()
                put(0x01.toByte())
                // X size block. 1 byte for left, 1 byte for right
                put(0x02.toByte())
                // Y size block. 1 byte for top, 1 byte for bottom
                put(0x02.toByte())
                // Regions block size
                put(regionsCount.toByte())

                // Some magic 8 bytes
                putInt(0)
                putInt(0)

                // Paddings
                // Left
                putInt(0)
                // Right
                putInt(0)
                // Top
                putInt(0)
                // Bottom
                putInt(0)

                // Skip 4 bytes
                putInt(0)

                // Axes
                // Left
                putInt(leftAxis)
                // Right
                putInt(rightAxis)
                // Top
                putInt(topAxis)
                // Bottom
                putInt(bottomAxis)

                // Regions
                repeat(regionsCount) {
                    putInt(REGIONS_COLOR)
                }
            }
            .array()
    }

    private companion object {
        /**
         * Region transparency:
         * - 0x000000 - transparent
         * - 0x000001 - visible
         * - else - also transparent
         */
        private const val REGIONS_COLOR = 1
    }

}