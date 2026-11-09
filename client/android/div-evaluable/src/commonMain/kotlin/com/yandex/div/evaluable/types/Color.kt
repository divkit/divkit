package com.yandex.div.evaluable.types

import kotlin.jvm.JvmInline

@JvmInline
public value class Color(public val value: Int) {

    /**
     * Copied from android.graphics.Color.
     * Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     */
    public fun alpha(): Int = value ushr 24

    /**
     * Copied from android.graphics.Color.
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    public fun red(): Int = value shr 16 and 0xFF

    /**
     * Copied from android.graphics.Color.
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    public fun green(): Int = value shr 8 and 0xFF

    /**
     * Copied from android.graphics.Color.
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    public fun blue(): Int = value and 0xFF

    override fun toString(): String = "#$stringValue"

    public fun toEncodedString(): String = "%23$stringValue"

    private val stringValue get() = value.toHexString(HexFormat.UpperCase)

    public companion object {
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
        public fun argb(alpha: Int, red: Int, green: Int, blue: Int): Color =
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
        public fun rgb(red: Int, green: Int, blue: Int): Color = argb(0xff, red, green, blue)

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
        public fun parse(colorString: String): Color {
            require(colorString.isNotEmpty()) { "Failed to parse Color: string is empty" }
            require(colorString[0] == '#') { "Failed to parse Color: $colorString" }
            val normalizedColorString = when (colorString.length) {
                4 -> {
                    val r: Char = colorString[1]
                    val g: Char = colorString[2]
                    val b: Char = colorString[3]
                    charArrayOf('f', 'f', r, r, g, g, b, b).concatToString()
                }

                5 -> {
                    val a: Char = colorString[1]
                    val r: Char = colorString[2]
                    val g: Char = colorString[3]
                    val b: Char = colorString[4]
                    charArrayOf(a, a, r, r, g, g, b, b).concatToString()
                }

                7 -> {
                    "ff" + colorString.substring(1)
                }

                9 -> {
                    colorString.substring(1)
                }

                else -> {
                    throw IllegalArgumentException("Failed to parse Color: $colorString")
                }
            }
            return Color(normalizedColorString.toLong(16).toInt())
        }
    }
}
