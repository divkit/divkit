package com.yandex.div.core.view2

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import androidx.core.graphics.withTranslation
import com.yandex.div.core.view2.divs.widgets.BitmapEffectHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object ShadowCache {
    private const val EDGE_OFFSET = 1
    /**
     * Region transparency:
     * - 0x000000 - transparent
     * - 0x000001 - visible
     * - else - also transparent
     */
    private const val REGIONS_COLOR = 1

    private val paint = Paint()

    private val shadowMap = mutableMapOf<ShadowCacheKey, NinePatch>()

    fun getShadow(radii: FloatArray, blur: Float, effectHelper: BitmapEffectHelper): NinePatch {
        return shadowMap.getOrPut(ShadowCacheKey(radii, blur)) {
            createNewShadow(radii, blur, effectHelper).toNinePatch()
        }
    }

    /*
     * Simplified shadow creation stages:
     * 1. Draw shadow rect on first bitmap.
     *    Even if you need 500x200 shadow with corner radius 16 and blur 8, you should draw
     *    (radius * 2) + blur = (16 * 2) + 8 = 40x40 rectangle. Draw shadow
     *    with offset equals blur. So allocated bitmap should be
     *    rectangleSize + (blur * 2) = 40 + (8 * 2) = 56x56
     * 2. Now just blur this bitmap with any algorithm to second bitmap.
     * 3. Create NinePatch from:
     *    - second bitmap, THAT we will adjust
     *    - NinePatch chunk which describes HOW adjust
     * 4. Draw this NinePatch with offset and target size
     *
     * Shadow uses only alpha channel, so you must set shadow color with paint on draw
     */
    private fun createNewShadow(radii: FloatArray, blur: Float, effectHelper: BitmapEffectHelper): Bitmap {
        var rectWidth = maxOf(radii[1] + radii[2], radii[5] + radii[6])
        var rectHeight = maxOf(radii[0] + radii[7], radii[3] + radii[4])
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            rectHeight += blur
            rectWidth += blur
        }
        val inBitmap = Bitmap.createBitmap(
            rectWidth.toInt() + 2 * blur.toInt(),
            rectHeight.toInt() + 2 * blur.toInt(),
            Bitmap.Config.ALPHA_8
        )

        inBitmap.drawNewShadow(rectWidth, rectHeight, radii, blur)

        if (blur == 0f) return inBitmap

        val outBitmap = effectHelper.blurBitmap(inBitmap, blur, isShadow = true)
        inBitmap.recycle()
        return outBitmap
    }

    private fun Bitmap.drawNewShadow(
        rectWidth: Float,
        rectHeight: Float,
        radii: FloatArray,
        blur: Float,
    ) {
        val roundRectShape = RoundRectShape(radii, null, null)

        roundRectShape.resize(rectWidth, rectHeight)

        Canvas().apply {
            setBitmap(this@drawNewShadow)
            withTranslation(blur, blur) {
                roundRectShape.draw(this, paint)
            }
        }
    }

    private fun Bitmap.toNinePatch() =
        NinePatch(this, createNinePatchChunk(width, height))

    private fun createNinePatchChunk(
        width: Int,
        height: Int
    ): ByteArray {
        val top = (height / 2) - EDGE_OFFSET
        val left = (width / 2) - EDGE_OFFSET
        val bottom = (height / 2) + EDGE_OFFSET
        val right = (width / 2) + EDGE_OFFSET

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

        //Create buffer with fixed allocated size
        val buffer = ByteBuffer.allocate(allocationSize).order(ByteOrder.nativeOrder())

        // Was deserialized: $value.toBoolean()
        buffer.put(0x01.toByte())
        // X size block. 1 byte for left, 1 byte for right
        buffer.put(0x02.toByte())
        // Y size block. 1 byte for top, 1 byte for bottom
        buffer.put(0x02.toByte())
        // Regions block size
        buffer.put(regionsCount.toByte())

        // Some magic 8 bytes
        buffer.putInt(0)
        buffer.putInt(0)

        // Paddings
        // Left
        buffer.putInt(0)
        // Right
        buffer.putInt(0)
        // Top
        buffer.putInt(0)
        // Bottom
        buffer.putInt(0)

        // Skip 4 bytes
        buffer.putInt(0)

        // Axes
        // Left
        buffer.putInt(left)
        // Right
        buffer.putInt(right)
        // Top
        buffer.putInt(top)
        // Bottom
        buffer.putInt(bottom)

        // Regions
        repeat(regionsCount) {
            buffer.putInt(REGIONS_COLOR)
        }

        return buffer.array()
    }

    class ShadowCacheKey(
        val radii: FloatArray,
        val blur: Float
    ) {
        /**
         * The system implementation compares values by reference, not by value.
         * Therefore, we write custom one
         */
        override fun equals(other: Any?) =
            if (other !is ShadowCacheKey)
                false
            else
                blur == other.blur && radii.contentEquals(other.radii)

        override fun hashCode(): Int {
            var result = radii.contentHashCode()
            result = 31 * result + blur.hashCode()
            return result
        }
    }
}
