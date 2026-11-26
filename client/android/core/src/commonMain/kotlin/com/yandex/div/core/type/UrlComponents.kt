package com.yandex.div.core.type

/**
 * Reads components of urls that were already validated by [UrlParser].
 */
internal object UrlComponents {

    fun scheme(url: String): String = url.substring(0, url.indexOf(':'))

    fun authority(url: String): String? {
        val start = url.indexOf(':') + 1
        if (!url.startsWith("//", start)) {
            return null
        }
        val end = url.indexOfAny(charArrayOf('/', '?', '#'), start + 2).orEndOf(url)
        return decode(url, start + 2, end, convertPlus = false)
    }

    fun queryParameter(url: String, name: String): String? {
        val queryStart = url.indexOf('?')
        val fragmentStart = url.indexOf('#')
        if (queryStart < 0 || fragmentStart in 0 until queryStart) {
            return null
        }
        val encodedName = encodeQueryParameterName(name)
        val queryEnd = fragmentStart.orEndOf(url)
        var start = queryStart + 1
        while (start <= queryEnd) {
            val end = url.indexOf('&', start).takeIf { it in 0 until queryEnd } ?: queryEnd
            val separator = url.indexOf('=', start).takeIf { it in 0 until end } ?: end
            if (separator - start == encodedName.length && url.startsWith(encodedName, start)) {
                return decode(url, minOf(separator + 1, end), end, convertPlus = true)
            }
            start = end + 1
        }
        return null
    }

    private fun encodeQueryParameterName(name: String): String {
        if (name.all { it.isAllowedInQueryParameterName() }) {
            return name
        }
        return buildString(name.length) {
            for (byte in name.encodeToByteArray()) {
                val value = byte.toInt() and 0xFF
                val char = value.toChar()
                if (char.isAllowedInQueryParameterName()) {
                    append(char)
                } else {
                    append('%')
                    append(HEX_DIGITS[value / HEX])
                    append(HEX_DIGITS[value % HEX])
                }
            }
        }
    }

    private fun Char.isAllowedInQueryParameterName(): Boolean =
        this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9' || this in "_-!.~'()*"

    private fun decode(input: String, start: Int, end: Int, convertPlus: Boolean): String {
        val result = StringBuilder(end - start)
        var index = start
        while (index < end) {
            val char = input[index]
            if (char == '%' && index + ESCAPE_LENGTH <= end) {
                val bytes = ByteArray((end - index) / ESCAPE_LENGTH)
                var count = 0
                while (index + ESCAPE_LENGTH <= end && input[index] == '%') {
                    bytes[count++] = escapedByte(input, index)
                    index += ESCAPE_LENGTH
                }
                result.append(bytes.decodeToString(0, count))
            } else {
                result.append(if (convertPlus && char == '+') ' ' else char)
                index++
            }
        }
        return result.toString()
    }

    private fun escapedByte(input: String, index: Int): Byte {
        return (input[index + 1].digitToInt(HEX) * HEX + input[index + 2].digitToInt(HEX)).toByte()
    }

    private fun Int.orEndOf(value: String): Int = if (this < 0) value.length else this

    private const val ESCAPE_LENGTH = 3
    private const val HEX = 16
    private const val HEX_DIGITS = "0123456789ABCDEF"
}
