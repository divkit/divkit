package com.yandex.div.evaluable.function

/**
 * Percent-encoding of `encodeUri` and `decodeUri` functions.
 */
internal object UriCodec {

    private const val UNRESERVED_MARKS = "-_.!~*'()"

    // A percent sign followed by two hex digits.
    private const val ESCAPE_LENGTH = 3
    private const val HEX_RADIX = 16

    /**
     * Percent-encodes UTF-8 bytes of all characters except unreserved ones, like `encodeURIComponent` in JavaScript.
     */
    fun encode(value: String): String {
        val bytes = value.encodeToByteArray()
        return buildString(bytes.size) {
            for (byte in bytes) {
                // Bytes of multibyte sequences become non-ASCII chars, which are never unreserved.
                val char = byte.toInt().toChar()
                if (char.isUnreserved()) {
                    append(char)
                } else {
                    append('%').append(byte.toHexString(HexFormat.UpperCase))
                }
            }
        }
    }

    /**
     * Decodes percent-encoded UTF-8 bytes and replaces `+` with a space, like `URLDecoder` in Java.
     * Malformed UTF-8 sequences are replaced with U+FFFD. Error messages are the ones of `URLDecoder`,
     * which `decodeUri` used before.
     */
    fun decode(value: String): String {
        val result = StringBuilder(value.length)
        // Allocated at the first escape, so it fits the bytes of any later run of escapes too.
        var bytes: ByteArray? = null
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char != '%') {
                result.append(if (char == '+') ' ' else char)
                index++
                continue
            }
            val buffer = bytes ?: ByteArray((value.length - index) / ESCAPE_LENGTH).also { bytes = it }
            var count = 0
            while (index + ESCAPE_LENGTH <= value.length && value[index] == '%') {
                buffer[count++] = decodeEscapedByte(value.substring(index + 1, index + ESCAPE_LENGTH))
                index += ESCAPE_LENGTH
            }
            require(index == value.length || value[index] != '%') {
                "URLDecoder: Incomplete trailing escape (%) pattern"
            }
            result.append(buffer.decodeToString(0, count))
        }
        return result.toString()
    }

    /**
     * Parses an escape like `URLDecoder` does with `Integer.parseInt(hex, 16)`: a sign is allowed,
     * a negative value is rejected.
     */
    private fun decodeEscapedByte(hex: String): Byte {
        val value = when (hex[0]) {
            '+' -> hex[1].escapeDigit(hex)
            '-' -> -hex[1].escapeDigit(hex)
            else -> hex[0].escapeDigit(hex) * HEX_RADIX + hex[1].escapeDigit(hex)
        }
        require(value >= 0) { "URLDecoder: Illegal hex characters in escape (%) pattern - negative value" }
        return value.toByte()
    }

    private fun Char.escapeDigit(hex: String): Int {
        return digitToIntOrNull(HEX_RADIX) ?: throw IllegalArgumentException(
            "URLDecoder: Illegal hex characters in escape (%) pattern - For input string: \"$hex\""
        )
    }

    private fun Char.isUnreserved(): Boolean {
        return this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9' || this in UNRESERVED_MARKS
    }
}
