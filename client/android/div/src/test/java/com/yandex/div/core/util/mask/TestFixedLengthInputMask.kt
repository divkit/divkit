package com.yandex.div.core.util.mask

internal class TestFixedLengthInputMask(
    initialMaskData: MaskData
) : FixedLengthInputMask(initialMaskData) {
    fun publicCalculateInsertableSubstring(substring: String, start: Int): String =
        calculateInsertableSubstring(substring, start)

    fun publicCollectValueRange(start: Int, end: Int): String =
        collectValueRange(start, end)

    fun publicReplaceChars(substring: String, start: Int, count: Int? = null) =
        replaceChars(substring, start, count)
}
