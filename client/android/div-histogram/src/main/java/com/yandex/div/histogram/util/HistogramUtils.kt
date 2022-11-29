package com.yandex.div.histogram.util

import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.internal.KAssert
import org.json.JSONObject

object HistogramUtils {

    /**
     * Calculates the size in bytes of string encoded to utf-8.
     * @see https://www.rfc-editor.org/rfc/rfc3629#section-3
     */
    fun calculateUtf8StringByteSize(str: String): Int {
        var byteSize = 0
        for (ch in str) {
            byteSize += getUtf8CharByteSize(ch)
        }
        return byteSize
    }

    /**
     * Gets the number of bytes that character takes when encoded to utf-8.
     * @see https://www.rfc-editor.org/rfc/rfc3629#section-3
     */
    fun getUtf8CharByteSize(ch: Char): Int {
        return when {
            /**
             * Characters whose code points are greater than U+FFFF are called
             * supplementary characters.
             * The Java platform uses the UTF-16 representation in char arrays and in
             * the String and StringBuffer classes. In this representation,
             * supplementary characters are represented as a pair of char values,
             * the first from the high-surrogates range, (\uD800-\uDBFF),
             * the second from the low-surrogates range (\uDC00-\uDFFF).
             */
            ch.isHighSurrogate() -> 4
            ch.isLowSurrogate() -> 0
            ch.code < 0x80 -> 1
            ch.code < 0x800 -> 2
            ch.code < 0x10000 -> 3
            else -> {
                KAssert.fail { "Unsupported character: '$ch'" }
                4
            }
        }
    }

    /**
     * Determines whether histogram should be recorded.
     */
    fun shouldRecordHistogram(
        @HistogramCallType callType: String,
        configuration: HistogramRecordConfiguration
    ): Boolean {
        return when (callType) {
            HistogramCallType.CALL_TYPE_COLD -> configuration.isColdRecordingEnabled
            HistogramCallType.CALL_TYPE_COOL -> configuration.isCoolRecordingEnabled
            HistogramCallType.CALL_TYPE_WARM -> configuration.isWarmRecordingEnabled
            else -> {
                KAssert.fail { "Unknown histogram call type: $callType" }
                false
            }
        }
    }

    /**
     * Calculates the size in bytes of json encoded to utf-8.
     */
    fun calculateUtf8JsonByteSize(json: JSONObject): Int {
        return JSONUtf8BytesCalculator.calculateUtf8JsonBytes(json)
    }
}
