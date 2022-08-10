package com.yandex.div.histogram.util

import org.json.JSONArray
import org.json.JSONObject

/**
 * Util class for calculating the byte size of [JSONObject] without serializing it to string.
 */
internal class JSONUtf8BytesCalculator {

    private var bytesSize = 0

    /**
     * Adds the size of array brackets.
     */
    private fun array(): JSONUtf8BytesCalculator {
        bytesSize += ARRAY_BRACKETS_BYTES
        return this
    }

    /**
     * Adds the size of object brackets.
     */
    private fun `object`(): JSONUtf8BytesCalculator {
        bytesSize += OBJECT_BRACES_BYTES
        return this
    }

    /**
     * Adds size of value according to value type.
     */
    private fun value(value: Any?): JSONUtf8BytesCalculator {
        if (value is JSONArray) {
            calculateUtf8JsonArrayBytes(value, this)
            return this
        } else if (value is JSONObject) {
            calculateUtf8JsonBytes(value, this)
            return this
        }
        if (value == null || value === JSONObject.NULL) {
            bytesSize += NULL_BYTES
        } else if (value is Boolean) {
            bytesSize += if (value) TRUE_BYTES else FALSE_BYTES
        } else if (value is Number) {
            bytesSize += HistogramUtils.calculateUtf8StringByteSize(JSONObject.numberToString(value))
        } else {
            string(value.toString())
        }
        return this
    }

    /**
     * Adds the size of string literal.
     */
    private fun string(value: String) {
        bytesSize += QUOTES_BYTES
        for (ch in value) {
            /*
            * From RFC 4627, "All Unicode characters may be placed within the
            * quotation marks except for the characters that must be escaped:
            * quotation mark, reverse solidus, and the control characters
            * (U+0000 through U+001F)."
            */
            bytesSize += when (ch) {
                '"', '\\', '/', '\t', '\b', '\n', '\r' -> ESCAPED_CHARACTERS_BYTES
                else -> if (ch.code <= 0x1F) {
                    HistogramUtils.calculateUtf8StringByteSize(String.format("\\u%04x", ch.code))
                } else {
                    HistogramUtils.getUtf8CharByteSize(ch)
                }
            }
        }
    }

    /**
     * Adds the size of key.
     */
    private fun key(name: String): JSONUtf8BytesCalculator {
        string(name)
        return this
    }

    /**
     * Adds the size of key-value separator.
     */
    private fun keyValueSeparator(): JSONUtf8BytesCalculator {
        bytesSize += KEY_VALUE_SEPARATOR_BYTES
        return this
    }

    /**
     * Adds the size of entries separators.
     */
    private fun entriesSeparator(entries: Int): JSONUtf8BytesCalculator {
        if (entries <= 1) {
            return this
        }
        bytesSize += ENTRIES_SEPARATOR_BYTES * (entries - 1)
        return this
    }

    internal companion object {

        private const val OBJECT_BRACES_BYTES = 2  // {}
        private const val ARRAY_BRACKETS_BYTES = 2  // []
        private const val QUOTES_BYTES = 2  // ""
        private const val ENTRIES_SEPARATOR_BYTES = 1  // ,
        private const val KEY_VALUE_SEPARATOR_BYTES = 1  // :
        private const val NULL_BYTES = 4  // null
        private const val ESCAPED_CHARACTERS_BYTES = 2  // '"', '\\', '/', '\t', '\b', '\n', '\r'
        private const val TRUE_BYTES = 4  // true
        private const val FALSE_BYTES = 5  // false

        fun calculateUtf8JsonBytes(json: JSONObject): Int {
            val jsonUtf8BytesCalculator = JSONUtf8BytesCalculator()
            calculateUtf8JsonBytes(json, jsonUtf8BytesCalculator)
            return jsonUtf8BytesCalculator.bytesSize;
        }

        private fun calculateUtf8JsonBytes(json: JSONObject,
                                           jsonUtf8BytesCalculator: JSONUtf8BytesCalculator
        ) {
            jsonUtf8BytesCalculator.`object`().entriesSeparator(json.length())
            json.keys().forEach {
                jsonUtf8BytesCalculator.key(it).keyValueSeparator().value(json.get(it))
            }
        }

        private fun calculateUtf8JsonArrayBytes(json: JSONArray,
                                                jsonUtf8BytesCalculator: JSONUtf8BytesCalculator
        ) {
            jsonUtf8BytesCalculator.array().entriesSeparator(json.length())
            for (i in 0 until json.length()) {
                jsonUtf8BytesCalculator.value(json.get(i))
            }
        }
    }
}
