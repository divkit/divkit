package com.yandex.test.screenshot.tasks

import com.yandex.test.util.Logger
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

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
            throw RuntimeException(
                "Images has different size: left(${lhsImage.size()}), right(${rhsImage.size()})"
            )
        }

        val width = lhsImage.width
        val height = lhsImage.height

        val geometricDistanceSum = (0 until width).sumOf { x ->
            (0 until height).sumOf { y ->
                normalizeGeometricDistance(
                    geometricDistance(lhsImage.getRGB(x, y), rhsImage.getRGB(x, y))
                )
            }
        }

        val actualDifference = geometricDistanceSum / width / height

        if (actualDifference > THRESHOLD) {
            throw RuntimeException(
                "Difference exceeds threshold: actual: $actualDifference, threshold: $THRESHOLD"
            )
        }
    }

    private fun geometricDistance(rgb1: Int, rgb2: Int): Double {
        val geometricDistanceSquared = (24 downTo 0 step 8) // a, r, g, b, each component is 8 bit wide
            .sumOf { componentShift ->
                val component1 = (rgb1 shr componentShift) and 0xFF
                val component2 = (rgb2 shr componentShift) and 0xFF

                (component1 - component2).toDouble().pow(2)
            }

        return sqrt(geometricDistanceSquared)
    }

    private fun normalizeGeometricDistance(distance: Double): Double {
        return distance / 0xFF / sqrt(3.0) // Max distance is sqrt(3 * (0xFF - 0)^2)
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
        private const val THRESHOLD = 1e-4 // Scaled from 0 to 1
    }
}
