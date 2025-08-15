package com.yandex.test.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 * Utility class for color processing.
 *
 * See ABRO implementation.
 */
object ColorUtils {
    private const val PERCEPTION_R = 0.299f
    private const val PERCEPTION_G = 0.587f
    private const val PERCEPTION_B = 0.114f
    private const val LIGHT_COLOR_BRIGHTNESS = 128

    private const val SIMILAR_COLOR_DISTANCE = 100
    private const val FONT_COLOR_THRESHOLD = 170
    private const val MINIMUM_COLOR_DISTANCE = 20.0
    private const val CONTRAST_LIGHT_ITEM_THRESHOLD = 3f

    /**
     * A helper function to detect whether a color is dark or not. According to returned value,
     * an activebar is painted with a contrasting color (bright or dark bar).
     */
    fun isDarkColor(@ColorInt color: Int): Boolean {
        return isDarkColor(color, ColorAlgorithm.CONTRAST)
    }

    /**
     * A helper function to detect whether a color is dark or not. According to returned value,
     * an activebar is painted with a contrasting color (bright or dark bar).
     *
     * @param colorAlgorithm to use in detection
     */
    fun isDarkColor(@ColorInt color: Int, colorAlgorithm: ColorAlgorithm): Boolean {
        return when (colorAlgorithm) {
            ColorAlgorithm.PERCEIVED_BRIGHTNESS -> isDarkColorUsingPerceivedBrightness(color)
            ColorAlgorithm.CONTRAST -> isDarkColorUsingContrast(color)
            else -> throw IllegalArgumentException()
        }
    }

    /**
     * Returns toned color.
     *
     * @param mainColor main color to tone
     * @return Toned color
     */
    fun getToneColorInt(@ColorInt mainColor: Int): Int {
        val red = Color.red(mainColor)
        val green = Color.green(mainColor)
        val blue = Color.blue(mainColor)
        val tone = (red + green + blue).toFloat() / 3.0f
        return Math.round(tone)
    }

    /**
     * Returns a dark or light color for text so that it is in contrast to its bg color.
     */
    @ColorInt
    fun getTextColorForBackground(
        context: Context,
        @ColorInt bgColor: Int,
        @ColorRes lightColorId: Int,
        @ColorRes darkColorId: Int
    ): Int {
        val textColorId = if (shouldTextBeLight(bgColor)) lightColorId else darkColorId
        return context.resources.getColor(textColorId)
    }

    /**
     * @return true if text should be light for provided bg color.
     */
    fun shouldTextBeLight(@ColorInt bgColor: Int): Boolean {
        return (getToneColorInt(bgColor) < FONT_COLOR_THRESHOLD
                && (Color.red(bgColor) < FONT_COLOR_THRESHOLD
                || Color.green(bgColor) < FONT_COLOR_THRESHOLD))
    }

    /**
     * Detects the [Perceived brightness](http://alienryderflex.com/hsp.html)
     * of a given color.
     *
     * @return the value of the brightness, between 0 and 255.
     */
    fun getPerceivedBrightness(@ColorInt color: Int): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Math.sqrt(r * r * PERCEPTION_R + g * g * PERCEPTION_G + (b * b * PERCEPTION_B).toDouble())
            .toInt()
    }

    /**
     * Calculates the contrast between the given color and white, using the algorithm provided by
     * the WCAG v2 in http://www.w3.org/TR/WCAG20/#contrast-ratiodef.
     */
    fun getContrastForColor(@ColorInt color: Int): Float {
        val bgR = getRelativeLuminance(Color.red(color))
        val bgG = getRelativeLuminance(Color.green(color))
        val bgB = getRelativeLuminance(Color.blue(color))
        val bgL = 0.2126f * bgR + 0.7152f * bgG + 0.0722f * bgB
        return Math.abs(1.05f / (bgL + 0.05f))
    }

    private fun getRelativeLuminance(luminance: Int): Float {
        val bg = luminance / 255f
        return if (bg < 0.03928f) bg / 12.92f else Math.pow((bg + 0.055f) / 1.055f.toDouble(), 2.4).toFloat()
    }

    /**
     * Defines whether two colors are similar, regardless alpha.
     *
     * @param color1
     * @param color2
     * @return true if the given colors are similar.
     */
    fun areColorsSimilar(color1: Int, color2: Int): Boolean {
        val distance = getColorDistance(color1, color2)
        return distance < SIMILAR_COLOR_DISTANCE
    }

    /**
     * Calculates a distance between two colors, regardless alpha.
     * TODO(weird, polikarpov, achirtsov): now it is a stub, put a real algorithm.
     *
     * @param color1 First color to calculate distance.
     * @param color2 Second color to calculate distance.
     *
     * @return Distance between two colors.
     */
    fun getColorDistance(color1: Int, color2: Int): Double {
        val deltaRed = Color.red(color1) - Color.red(color2)
        val deltaGreen = Color.green(color1) - Color.green(color2)
        val deltaBlue = Color.blue(color1) - Color.blue(color2)
        return Math.sqrt(deltaRed * deltaRed + deltaGreen * deltaGreen + (deltaBlue * deltaBlue).toDouble())
    }

    /**
     * Combine a translucent foreground color with an opaque background color.
     *
     * @param backgroundColor Background color. Alpha is ignored.
     * @param foregroundColor Translucent foreground color.
     * @return Blended color.
     */
    fun alphaBlend(backgroundColor: Int, foregroundColor: Int): Int {
        val alpha = Color.alpha(foregroundColor).toDouble()
        val weight0 = alpha / 0xFF
        val weight1 = 1.0 - weight0
        val r = weight0 * Color.red(foregroundColor) + weight1 * Color.red(backgroundColor)
        val g = weight0 * Color.green(foregroundColor) + weight1 * Color.green(backgroundColor)
        val b = weight0 * Color.blue(foregroundColor) + weight1 * Color.blue(backgroundColor)
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    /**
     * Replace alpha in given color.
     */
    fun replaceAlpha(alpha: Int, color: Int): Int {
        return Color.argb(
            alpha,
            Color.red(color), Color.green(color), Color.blue(color)
        )
    }

    /**
     * Crossfade colors with given progress ignoring colors' alpha
     *
     * @param backgroundColor
     * @param foregroundColor
     * @param progress
     * @return result color
     */
    fun crossFade(backgroundColor: Int, foregroundColor: Int, progress: Float): Int {
        val backgroundAlpha = (255 * progress).toInt()
        val foregroundAlpha = 255 - backgroundAlpha
        return alphaBlend(
            replaceAlpha(backgroundAlpha, backgroundColor),
            replaceAlpha(foregroundAlpha, foregroundColor)
        )
    }

    /**
     * Removes the alpha channel of the given color and returns the processed value.
     */
    @ColorInt
    fun removeTransparencyFromColor(@ColorInt color: Int): Int {
        return color or -0x1000000
    }

    /**
     * Replace alpha with 0 making the color fully transparent.
     */
    @ColorInt
    fun makeTransparent(@ColorInt color: Int): Int {
        return color and 0x00FFFFFF
    }

    /**
     * @param color A color to compare with the white.
     * @return Whether a color is close to white (i.e. [.getColorDistance] is
     * less than [.MINIMUM_COLOR_DISTANCE]).
     */
    fun isColorCloseToWhite(color: Int): Boolean {
        return getColorDistance(color, Color.WHITE) < MINIMUM_COLOR_DISTANCE
    }

    /**
     * Using perceived brightness algorithm.
     */
    private fun isDarkColorUsingPerceivedBrightness(@ColorInt color: Int): Boolean {
        return getPerceivedBrightness(color) < LIGHT_COLOR_BRIGHTNESS
    }

    /**
     * Using contrast algorithm.
     */
    private fun isDarkColorUsingContrast(@ColorInt color: Int): Boolean {
        return getContrastForColor(color) >= CONTRAST_LIGHT_ITEM_THRESHOLD
    }

    /**
     * Cresting color filter from color resource.
     */
    fun createFilter(resources: Resources, @ColorRes colorId: Int): ColorFilter {
        return PorterDuffColorFilter(
            resources.getColor(colorId),
            PorterDuff.Mode.SRC_IN
        )
    }

    sealed class ColorAlgorithm {
        object PERCEIVED_BRIGHTNESS: ColorAlgorithm()
        object CONTRAST: ColorAlgorithm()
    }
}
