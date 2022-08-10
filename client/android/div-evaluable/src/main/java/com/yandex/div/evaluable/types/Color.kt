package com.yandex.div.evaluable.types

import kotlin.jvm.Throws

@JvmInline
value class Color(val value: Int) {

    /**
     * Copied from android.graphics.Color.
     * Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     */
    fun alpha() = value ushr 24

    /**
     * Copied from android.graphics.Color.
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    fun red()= value shr 16 and 0xFF

    /**
     * Copied from android.graphics.Color.
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    fun green() = value shr 8 and 0xFF

    /**
     * Copied from android.graphics.Color.
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    fun blue() = value and 0xFF



    override fun toString(): String {
        return "#" + Integer.toHexString(value).padStart(8, '0').uppercase()
    }

    companion object {
        /**
         * Copied from android.graphics.Color.
         * Return a color-int from alpha, red, green, blue components.
         * These component values should be ([0..255]), but there is no
         * range check performed, so if they are out of range, the
         * returned color is undefined.
         * @param alpha Alpha component ([0..255]) of the color
         * @param red Red component ([0..255]) of the color
         * @param green Green component ([0..255]) of the color
         * @param blue Blue component ([0..255]) of the color
         */
        fun argb(alpha: Int, red: Int, green: Int, blue: Int) =
                Color(alpha shl 24 or (red shl 16) or (green shl 8) or blue)

        /**
         * Copied from android.graphics.Color.
         * Return a color-int from alpha, red, green, blue components.
         * These component values should be ([0..255]), but there is no
         * range check performed, so if they are out of range, the
         * returned color is undefined.
         * @param red Red component ([0..255]) of the color
         * @param green Green component ([0..255]) of the color
         * @param blue Blue component ([0..255]) of the color
         */
        fun rgb(red: Int, green: Int, blue: Int) = argb(0xff, red, green, blue)

        /**
         * Parse the color string, and return the corresponding color-int.
         * If the string cannot be parsed, throws an IllegalArgumentException
         * exception. Supported formats are:
         *
         *
         *  * `#RGB`
         *  * `#ARGB`
         *  * `#RRGGBB`
         *  * `#AARRGGBB`
         */
        @Throws(
            IllegalArgumentException::class,
            NumberFormatException::class
        )
        fun parse(colorString: String): Color {
            require(colorString.isNotEmpty()) { "Expected color string, actual string is empty" }
            require(colorString[0] == '#') { "Unknown color $colorString" }
            val normalizedColorString = when (colorString.length) {
                4 -> {
                    val r: Char = colorString[1]
                    val g: Char = colorString[2]
                    val b: Char = colorString[3]
                    String(charArrayOf('f', 'f', r, r, g, g, b, b))
                }
                5 -> {
                    val a: Char = colorString[1]
                    val r: Char = colorString[2]
                    val g: Char = colorString[3]
                    val b: Char = colorString[4]
                    String(charArrayOf(a, a, r, r, g, g, b, b))
                }
                7 -> {
                    "ff" + colorString.substring(1)
                }
                9 -> {
                    colorString.substring(1)
                }
                else -> {
                    throw IllegalArgumentException("Unknown color $colorString")
                }
            }
            return Color(normalizedColorString.toLong(16).toInt())
        }
    }
}
