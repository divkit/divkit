package com.yandex.test.screenshot.tasks

import com.yandex.test.util.Logger
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max

internal class ImageComparator(private val logger: Logger) {

    fun compareImages(lhs: File, rhs: File, imagePath: String): Boolean {
        return try {
            logger.i("Comparing $imagePath")
            compareImagesImpl(lhs, rhs)
            logger.i("Comparison passed")
            true
        } catch (e: Exception) {
            logger.e("Comparison failed", e)
            false
        }
    }

    private fun compareImagesImpl(lhs: File, rhs: File) {
        val lhsImage = ImageIO.read(lhs)
        val rhsImage = ImageIO.read(rhs)

        if (lhsImage.width != rhsImage.width || lhsImage.height != rhsImage.height) {
            throw RuntimeException("Images has different size: left(${lhsImage.size()}), right(${rhsImage.size()})")
        }

        for (width in 0 until lhsImage.width) {
            for (height in 0 until lhsImage.height) {
                val delta = componentsSquareDelta(lhsImage.getRGB(width, height), rhsImage.getRGB(width, height))
                if (delta > MAX_COMPONENTS_DELTA) {
                    val color1 = Integer.toHexString(lhsImage.getRGB(width, height))
                    val color2 = Integer.toHexString(rhsImage.getRGB(width, height))
                    throw RuntimeException("Too big delta $delta at ($width, $height). New: $color1, old: $color2")
                }
            }
        }
    }

    private fun componentsSquareDelta(rgb1: Int, rgb2: Int): Int {
        val component11 = (rgb1 shr 24) and 0xFF
        val component12 = (rgb1 shr 16) and 0xFF
        val component13 = (rgb1 shr 8) and 0xFF
        val component14 = rgb1 and 0xFF
        val component21 = (rgb2 shr 24) and 0xFF
        val component22 = (rgb2 shr 16) and 0xFF
        val component23 = (rgb2 shr 8) and 0xFF
        val component24 = rgb2 and 0xFF
        return (component11 - component21) * (component11 - component21) +
                (component12 - component22) * (component12 - component22) +
                (component13 - component23) * (component13 - component23) +
                (component14 - component24) * (component14 - component24)
    }

    fun createDiff(lhs: File, rhs: File, diff: File, imagePath: String) {
        try {
            logger.i("Creating diff for $imagePath")
            createDiffImpl(lhs, rhs, diff)
            logger.i("Diff saved to ${diff.absolutePath}")
        } catch (e: Exception) {
            logger.e("l: $lhs r: $rhs d: $diff d.canWrite: ${diff.canWrite()}", e)
        }
    }

    private fun createDiffImpl(lhs: File, rhs: File, diff: File) {
        val lhsImage = ImageIO.read(lhs)
        val rhsImage = ImageIO.read(rhs)

        // store diff image into bigger one
        val diffImage = if (lhsImage.height >= rhsImage.height) lhsImage else rhsImage
        val height = max(lhsImage.height, rhsImage.height)

        for (x in 0 until lhsImage.width) {
            for (y in 0 until height) {
                val color1 = lhsImage.getRGBSafe(x, y)
                val color2 = rhsImage.getRGBSafe(x, y)
                val color = (color1 xor color2)
                var alpha = color shr 24
                if (alpha == 0) alpha = color1 shr 24  // same alpha, so restoring original value
                diffImage.setRGB(x, y, (color and 0xFFFFFF) or (alpha shl 24))
            }
        }

        diff.mkdirs()
        ImageIO.write(diffImage, "png", diff)
    }

    private fun BufferedImage.size(): String = "${width}x${height}"

    private fun BufferedImage.getRGBSafe(x: Int, y: Int) = if (y < height) getRGB(x, y) else 0

    companion object {

        /**
         * Empirical value for difference in pixel components due to compression artifacts
         * (r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2 <= MAX, so 12 means maximum components delta 2 is allowed
         */
        private const val MAX_COMPONENTS_DELTA = 12
    }
}
