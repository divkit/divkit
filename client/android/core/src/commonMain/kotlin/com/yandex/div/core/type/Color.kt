package com.yandex.div.core.type

@JvmInline
public value class Color private constructor(
    private val value: Int
) {

    private val colorString
        get() = value.toHexString(hexFormat)

    /**
     * Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     */
    public fun alpha(): Int = value ushr ALPHA_SHIFT

    /**
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    public fun red(): Int = value shr RED_SHIFT and COMPONENT_MASK

    /**
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    public fun green(): Int = value shr GREEN_SHIFT and COMPONENT_MASK

    /**
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    public fun blue(): Int = value and COMPONENT_MASK

    override fun toString(): String = "#$colorString"

    public fun toEncodedString(): String = "%23$colorString"

    public companion object {

        private const val ALPHA_SHIFT = 24
        private const val RED_SHIFT = 16
        private const val GREEN_SHIFT = 8
        private const val COMPONENT_MASK = 0xFF
        private const val OPAQUE_ALPHA = 0xFF

        private val hexFormat = HexFormat {
            upperCase = true
        }

        @JvmStatic
        public fun create(color: Int): Color {
            return Color(color)
        }

        @JvmStatic
        public fun create(color: Color): Color {
            return Color(color.value)
        }

        /**
         * Return a color-int from alpha, red, green, blue components.
         * These component values should be ([0..255]), but there is no
         * range check performed, so if they are out of range, the
         * returned color is undefined.
         * @param alpha Alpha component ([0..255]) of the color
         * @param red Red component ([0..255]) of the color
         * @param green Green component ([0..255]) of the color
         * @param blue Blue component ([0..255]) of the color
         */
        @JvmStatic
        public fun argb(alpha: Int, red: Int, green: Int, blue: Int): Color {
            return Color(alpha shl ALPHA_SHIFT or (red shl RED_SHIFT) or (green shl GREEN_SHIFT) or blue)
        }

        /**
         * Return a color-int from alpha, red, green, blue components.
         * These component values should be ([0..255]), but there is no
         * range check performed, so if they are out of range, the
         * returned color is undefined.
         * @param red Red component ([0..255]) of the color
         * @param green Green component ([0..255]) of the color
         * @param blue Blue component ([0..255]) of the color
         */
        @JvmStatic
        public fun rgb(red: Int, green: Int, blue: Int): Color = argb(OPAQUE_ALPHA, red, green, blue)

        /**
         * Parse the color string, and return the corresponding color-int.
         * If the string cannot be parsed, throws an IllegalArgumentException
         * exception. Supported formats are:
         *
         *  * `#RGB`
         *  * `#ARGB`
         *  * `#RRGGBB`
         *  * `#AARRGGBB`
         */
        @JvmStatic
        public fun parse(colorString: String): Color {
            return ColorParser.parse(colorString)
        }
    }
}
