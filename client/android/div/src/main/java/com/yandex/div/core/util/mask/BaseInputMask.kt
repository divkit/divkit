package com.yandex.div.core.util.mask

import java.util.regex.PatternSyntaxException
import kotlin.math.min

internal abstract class BaseInputMask(
    initialMaskData: MaskData
) {
    protected var maskData: MaskData = initialMaskData
        private set

    protected val filters = mutableMapOf<Char, Regex>()

    protected lateinit var destructedValue: List<MaskChar>

    var cursorPosition: Int = 0
        protected set

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

    protected val firstEmptyHolderIndex: Int
        get() {
            return destructedValue.indexOfFirst { maskChar ->
                maskChar is MaskChar.Dynamic && maskChar.char == null
            }.let { if (it != -1) it else destructedValue.size }
        }

    init {
        updateMaskData(initialMaskData)
    }

    open fun updateMaskData(newMaskData: MaskData, restoreValue: Boolean = true) {
        val previousRawValue = if (maskData != newMaskData && restoreValue) rawValue else null

        maskData = newMaskData

        filters.clear()

        maskData.decoding.forEach { maskKey ->
            try {
                maskKey.filter?.let { filters[maskKey.key] = Regex(it) }
            } catch (e: PatternSyntaxException) {
                onException(e)
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

    open fun overrideRawValue(newRawValue: String) {
        clearRange(0, destructedValue.size)

        replaceChars(newRawValue, 0)

        cursorPosition = min(cursorPosition, value.length)
    }

    open fun applyChangeFrom(newValue: String, position: Int? = null) {
        val textDiff = TextDiff.build(value, newValue)
            .let {
                if (position != null) {
                    TextDiff(
                        (position - it.added).coerceAtLeast(0),
                        it.added,
                        it.removed
                    )
                } else {
                    it
                }
            }

        val tailStart = replaceBodyTail(textDiff, newValue)

        calculateCursorPosition(textDiff, tailStart)
    }

    protected fun replaceBodyTail(textDiff: TextDiff, newValue: String): Int {
        val body = buildBodySubstring(textDiff, newValue)
        val tail = buildTailSubstring(textDiff)

        cleanup(textDiff)

        val fehi = firstEmptyHolderIndex

        val maxShift = calculateMaxShift(tail, fehi)

        replaceChars(body, fehi, maxShift)

        val tailStart = firstEmptyHolderIndex

        replaceChars(tail, tailStart)

        return tailStart
    }

    private fun buildBodySubstring(textDiff: TextDiff, newValue: String): String {
        return newValue.substring(textDiff.start, textDiff.start + textDiff.added)
    }

    private fun buildTailSubstring(textDiff: TextDiff): String {
        return collectValueRange(textDiff.start + textDiff.removed, destructedValue.size - 1)
    }

    private fun calculateMaxShift(string: String, start: Int): Int {
        return if (filters.size <= 1) {
            var dynamicLeft = 0

            var index = start

            while (index < destructedValue.size) {
                if (destructedValue[index] is MaskChar.Dynamic) dynamicLeft++
                index++
            }

            dynamicLeft - string.length
        } else {
            val initialInsertableSubstring = calculateInsertableSubstring(string, start)

            var index = 0

            while (index < destructedValue.size && initialInsertableSubstring == calculateInsertableSubstring(string, start + index)) {
                index++
            }

            index - 1
        }.coerceAtLeast(0)
    }

    protected fun cleanup(textDiff: TextDiff) {
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

        clearRange(textDiff.start, destructedValue.size)
    }

    protected fun clearRange(start: Int, end: Int) {
        var index = start

        while (index < end && index < destructedValue.size) {
            val holder = destructedValue[index]

            if (holder is MaskChar.Dynamic) holder.char = null

            index++
        }
    }

    protected fun calculateCursorPosition(
        textDiff: TextDiff,
        tailStart: Int
    ) {
        val fehi = firstEmptyHolderIndex

        val positionByDiff = if (textDiff.start < fehi) {
            min(firstHolderAfter(tailStart), value.length)
        } else {
            fehi
        }

        cursorPosition = positionByDiff
    }

    protected fun calculateInsertableSubstring(substring: String, start: Int): String {
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

    protected fun collectValueRange(start: Int, end: Int): String {
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

    protected fun replaceChars(substring: String, start: Int, count: Int? = null) {
        val trimmedSubstring = calculateInsertableSubstring(substring, start).let {
            if (count != null) it.take(count) else it
        }

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

    protected fun firstHolderAfter(start: Int): Int {
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

    abstract fun onException(exception: Exception)

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
