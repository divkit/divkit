package com.yandex.div.core.util.mask

import java.util.regex.PatternSyntaxException
import kotlin.math.min

internal class MaskHelper(
    initialMaskData: MaskData,
    private val onError: ((Exception) -> Unit)? = null
) {
    private var maskData: MaskData = initialMaskData

    private val filters = mutableMapOf<Char, Regex>()

    private lateinit var destructedValue: List<MaskChar>

    init {
        updateMaskData(initialMaskData)
    }

    var cursorPosition: Int = 0
        private set

    val rawValue: String
        get() = collectValueRange(0, destructedValue.size - 1)

    val value: String
        get() {
            val stringBuilder = StringBuilder()

            destructedValue.takeWhile { char ->
                return@takeWhile when {
                    char is MaskChar.Static -> {
                        stringBuilder.append(char.char)

                        true
                    }
                    char is MaskChar.Dynamic && char.char != null -> {
                        stringBuilder.append(char.char)

                        true
                    }
                    maskData.alwaysVisible -> {
                        stringBuilder.append((char as MaskChar.Dynamic).placeholder)

                        true
                    }
                    else -> false
                }
            }

            return stringBuilder.toString()
        }

    private val firstEmptyHolderIndex: Int
        get() {
            return destructedValue.indexOfFirst { maskChar ->
                maskChar is MaskChar.Dynamic && maskChar.char == null
            }.let { if (it != -1) it else destructedValue.size }
        }

    fun updateMaskData(newMaskData: MaskData) {
        val previousRawValue = if (maskData != newMaskData) rawValue else null

        maskData = newMaskData

        maskData.decoding.forEach { maskKey ->
            filters.remove(maskKey.key)

            try {
                maskKey.filter?.let { filters[maskKey.key] = Regex(it) }
            } catch (e: PatternSyntaxException) {
                onError?.invoke(e)
            }
        }

        destructedValue = maskData.pattern.map { maskChar ->
            val mappingItem = maskData.decoding.firstOrNull { it.key == maskChar }

            if (mappingItem != null)
                MaskChar.Dynamic(null, filters[mappingItem.key], mappingItem.placeholder)
            else
                MaskChar.Static(maskChar)
        }

        if (previousRawValue != null) {
            overrideRawValue(previousRawValue)
        }
    }

    fun overrideRawValue(newRawValue: String) {
        clearRange(0, destructedValue.size - 1)

        replaceChars(newRawValue, 0)

        cursorPosition = min(min(cursorPosition, destructedValue.size - 1), value.length)
    }

    fun applyChangeFrom(newValue: String) {
        val textDiff = TextDiff.build(value, newValue)

        val body = buildBodySubstring(textDiff, newValue)
        val tail = buildTailSubstring(textDiff)

        cleanup(textDiff)

        replaceChars(body, firstEmptyHolderIndex)

        val tailStart = firstEmptyHolderIndex

        replaceChars(tail, tailStart)

        calculateCursorPosition(textDiff, tailStart)
    }

    private fun buildBodySubstring(textDiff: TextDiff, newValue: String): String {
        return newValue.substring(textDiff.start, textDiff.start + textDiff.added)
    }

    private fun buildTailSubstring(textDiff: TextDiff): String {
        return collectValueRange(textDiff.start + textDiff.removed, destructedValue.size - 1)
    }

    private fun cleanup(textDiff: TextDiff) {
        if (textDiff.added == 0 && textDiff.removed == 1) {
            var index = textDiff.start

            while (index >= 0) {
                val maskChar = destructedValue[index]

                if (maskChar is MaskChar.Dynamic && maskChar.char != null) {
                    maskChar.char = null

                    break
                } else {
                    index--
                }
            }
        }

        clearRange(textDiff.start, destructedValue.size - 1)
    }

    private fun clearRange(start: Int, end: Int) {
        var index = start

        while (index <= end) {
            val holder = destructedValue[index]

            if (holder is MaskChar.Dynamic) holder.char = null

            index++
        }
    }

    private fun calculateCursorPosition(textDiff: TextDiff, tailStart: Int) {
        val fehi = firstEmptyHolderIndex

        cursorPosition = if (textDiff.start < fehi) {
            min(nextHolderAfter(tailStart), value.length)
        } else {
            fehi
        }
    }

    internal fun calculateInsertableSubstring(substring: String, start: Int): String {
        val charsCanBeInsertedStringBuilder = StringBuilder()

        var index = start

        val moveToAndGetNextHolderFilter: () -> Regex? = {
            while (index < destructedValue.size && destructedValue[index] !is MaskChar.Dynamic) {
                index++
            }

            (destructedValue.getOrNull(index) as? MaskChar.Dynamic)?.filter
        }

        substring.forEach { char ->
            val maskCharFilter = moveToAndGetNextHolderFilter()

            when {
                maskCharFilter == null -> Unit
                maskCharFilter.matches(char.toString()) -> {
                    charsCanBeInsertedStringBuilder.append(char)

                    index++
                }
            }
        }

        return charsCanBeInsertedStringBuilder.toString()
    }

    internal fun collectValueRange(start: Int, end: Int): String {
        val tailStringBuilder = StringBuilder()

        var index = start

        while (index <= end) {
            val maskChar = destructedValue[index]

            if (maskChar is MaskChar.Dynamic) {
                if (maskChar.char != null) {
                    tailStringBuilder.append(maskChar.char)
                }
            }

            index++
        }

        return tailStringBuilder.toString()
    }

    internal fun replaceChars(substring: String, start: Int) {
        val trimmedSubstring = calculateInsertableSubstring(substring, start)

        var index = start

        var insertableCharIndex = 0

        while (index < destructedValue.size && insertableCharIndex < trimmedSubstring.length) {
            val maskChar = destructedValue[index]
            val char = trimmedSubstring[insertableCharIndex]

            if (maskChar is MaskChar.Dynamic) {
                maskChar.char = char

                insertableCharIndex++
            }

            index++
        }
    }

    private fun nextHolderAfter(start: Int): Int {
        var index = start

        while (index < destructedValue.size) {
            val holder = destructedValue[index]

            if (holder is MaskChar.Dynamic) {
                break
            } else {
                index++
            }
        }

        return index
    }

    class MaskKey(
        val key: Char,
        val filter: String?,
        val placeholder: Char
    )

    data class MaskData(
        val pattern: String,
        val decoding: List<MaskKey>,
        val alwaysVisible: Boolean
    )

    sealed class MaskChar {
        data class Static(val char: Char) : MaskChar()

        data class Dynamic(var char: Char?, val filter: Regex?, val placeholder: Char) : MaskChar()
    }
}
