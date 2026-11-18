package com.yandex.div.compose.views.background

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntRect
import coil3.BitmapImage
import kotlin.math.roundToInt

internal class NinePatchImage(private val image: BitmapImage, center: IntRect) {

    private val bounds = IntRect(0, 0, image.width, image.height)

    // Invalid slice boundaries stretch the whole image
    private val stretch = center.takeIf { !it.isEmpty && bounds.intersect(it) == it } ?: bounds

    private val xs = intArrayOf(0, stretch.left, stretch.right, bounds.right)
    private val ys = intArrayOf(0, stretch.top, stretch.bottom, bounds.bottom)
    private val dstX = FloatArray(xs.size)
    private val dstY = FloatArray(ys.size)

    private val patches = buildList {
        for (row in 0 until ys.lastIndex) {
            for (col in 0 until xs.lastIndex) {
                val rect = IntRect(xs[col], ys[row], xs[col + 1], ys[row + 1])
                if (!rect.isEmpty) add(Patch(row, col, image.crop(rect)))
            }
        }
    }

    private val paint = Paint().apply {
        isAntiAlias = false
        filterQuality = FilterQuality.Low
    }

    fun draw(scope: DrawScope) = with(scope) {
        val scale = image.densityScale(drawContext.canvas)
        xs.updateDestination(dstX, size.width.roundToInt(), scale)
        ys.updateDestination(dstY, size.height.roundToInt(), scale)
        for (index in patches.indices) {
            val (row, col, bitmap) = patches[index]
            val width = dstX[col + 1] - dstX[col]
            val height = dstY[row + 1] - dstY[row]
            if (width <= 0f || height <= 0f) {
                continue
            }
            withTransform({
                translate(dstX[col], dstY[row])
                scale(width / bitmap.width, height / bitmap.height, Offset.Zero)
            }) {
                drawContext.canvas.drawImageRect(bitmap, paint = paint)
            }
        }
    }

    private data class Patch(val row: Int, val column: Int, val bitmap: ImageBitmap)
}

private fun IntArray.updateDestination(destination: FloatArray, target: Int, scale: Float) {
    val first = this[1]
    val last = this[3] - this[2]
    val fixed = first + last
    if (fixed * scale > target) {
        // The borders share all available space and the middle disappears
        val split = target.toFloat() * first / fixed
        destination[1] = split
        destination[2] = split
    } else {
        destination[1] = first * scale
        destination[2] = target - last * scale
    }
    destination[destination.lastIndex] = target.toFloat()
}
