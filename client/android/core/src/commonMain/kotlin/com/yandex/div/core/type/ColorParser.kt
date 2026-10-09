package com.yandex.div.core.type


internal object ColorParser {

    private const val HEX_RADIX = 16

    fun parse(colorString: String): Color {
        require(colorString.isNotEmpty()) { "Failed to parse Color: color string is empty" }
        require(colorString[0] == '#') { "Failed to parse Color: $colorString" }

        val normalizedColorString = when (colorString.length) {
            4 -> {
                val r: Char = colorString[1]
                val g: Char = colorString[2]
                val b: Char = colorString[3]
                String(charArrayOf('F', 'F', r, r, g, g, b, b))
            }

            5 -> {
                val a: Char = colorString[1]
                val r: Char = colorString[2]
                val g: Char = colorString[3]
                val b: Char = colorString[4]
                String(charArrayOf(a, a, r, r, g, g, b, b))
            }

            7 -> {
                "FF" + colorString.substring(1)
            }

            9 -> {
                colorString.substring(1)
            }

            else -> {
                throw IllegalArgumentException("Failed to parse Color: $colorString")
            }
        }

        val isHexadecimalString = normalizedColorString.all { digit ->
            digit in '0'..'9' || digit in 'a'..'f' || digit in 'A'..'F'
        }
        if (!isHexadecimalString) {
            throw NumberFormatException("Failed to parse Color: $colorString")
        }

        return Color.create(normalizedColorString.toLong(HEX_RADIX).toInt())
    }
}
