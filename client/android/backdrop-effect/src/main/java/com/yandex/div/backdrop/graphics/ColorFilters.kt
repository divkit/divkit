package com.yandex.div.backdrop.graphics

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

/**
 * Adapted from the AndroidLiquidGlass library by Kyant0.
 * https://github.com/Kyant0/AndroidLiquidGlass/blob/kmp/backdrop/src/commonMain/kotlin/com/kyant/backdrop/effects/ColorFilter.kt
 */
internal object ColorFilters {

    @Suppress("UnnecessaryVariable")
    fun colorControlColorFilter(
        brightness: Float = 0.0f,
        contrast: Float = 1.0f,
        saturation: Float = 1.0f,
    ): ColorMatrixColorFilter {

        val invSat = 1.0f - saturation
        val r = 0.213f * invSat
        val g = 0.715f * invSat
        val b = 0.072f * invSat

        val c = contrast
        val t = (0.5f - c * 0.5f + brightness) * 255f
        val s = saturation

        val cr = c * r
        val cg = c * g
        val cb = c * b
        val cs = c * s

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                cr + cs, cg, cb, 0f, t,
                cr, cg + cs, cb, 0f, t,
                cr, cg, cb + cs, 0f, t,
                0f, 0f, 0f, 1f, 0f
            )
        )
        return ColorMatrixColorFilter(colorMatrix)
    }
}
