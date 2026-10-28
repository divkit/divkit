package com.yandex.div.render.common.images

import android.graphics.Bitmap
import android.graphics.NinePatch
import androidx.core.graphics.Insets
import com.yandex.div.core.annotations.InternalApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val REGIONS_COLOR = 1

@InternalApi
public fun Bitmap.toNinePatch(insets: Insets): NinePatch {
    return NinePatch(this, getChunkByteArray(width, height, insets))
}

private fun getChunkByteArray(
    imageWidth: Int,
    imageHeight: Int,
    insets: Insets
): ByteArray {
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
            putInt(insets.left)
            // Right
            putInt(imageWidth - insets.right)
            // Top
            putInt(insets.top)
            // Bottom
            putInt(imageHeight - insets.bottom)

            // Regions
            repeat(regionsCount) {
                putInt(REGIONS_COLOR)
            }
        }
        .array()
}
