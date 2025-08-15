package com.yandex.div.core.util.mask

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

internal class CurrencyInputMask(
    locale: Locale,
    private val onError: ((Exception) -> Unit)
) : BaseInputMask(MaskData("", emptyList(), false)) {
    private val currencyKey = '¤'

    private val separators = listOf('.', ',')

    private var currencyFormatter: NumberFormat = NumberFormat
        .getCurrencyInstance(locale)
        .clearFormatter()

    private val decimalFormatSymbols: DecimalFormatSymbols
        get() = (currencyFormatter as DecimalFormat).decimalFormatSymbols

    private val String.withNbsp: String
        get() = replace(' ', ' ')

    fun updateCurrencyParams(locale: Locale) {
        val currentValue = rawValue.replace(decimalFormatSymbols.decimalSeparator, '.')

        currencyFormatter = NumberFormat.getCurrencyInstance(locale).clearFormatter()

        val newValue = currentValue.replace('.', decimalFormatSymbols.decimalSeparator)

        applyChangeFrom(newValue)
    }

    private fun invalidateMaskDataForFormatted(forValue: Number) {
        val formatted = currencyFormatter.format(forValue)
        val maskPattern = formatPattern(formatted)

        val decoding = listOf(
            MaskKey(
                key = '#',
                filter = "\\d",
                placeholder = '0'
            ),
            MaskKey(
                key = decimalFormatSymbols.decimalSeparator,
                filter = "[${decimalFormatSymbols.decimalSeparator}]",
                placeholder = decimalFormatSymbols.decimalSeparator
            )
        )

        updateMaskData(MaskData(maskPattern, decoding, maskData.alwaysVisible), false)
    }

    override fun overrideRawValue(newRawValue: String) {
        val parsed = currencyFormatter.parse(newRawValue) ?: 0

        invalidateMaskDataForFormatted(parsed)

        super.overrideRawValue(newRawValue)
    }

    override fun applyChangeFrom(newValue: String, position: Int?) {
        val diff = TextDiff.build(value, newValue.withNbsp)

        val decimalSeparator = decimalFormatSymbols.decimalSeparator
        val oldSeparatorIndex = value.indexOfLast { it == decimalSeparator }
        val newSeparatorIndex = newValue.indexOfLast { it == decimalSeparator }
        val needInvalidateMask = oldSeparatorIndex != newSeparatorIndex ||
                (oldSeparatorIndex == -1 && newSeparatorIndex == -1)

        val clearedValue = newValue.toValidFormat(diff)

        val numberValue = currencyFormatter.parse(
            when {
                clearedValue.startsWith(decimalSeparator) ->
                    decimalFormatSymbols.zeroDigit + clearedValue
                clearedValue.isBlank() -> decimalFormatSymbols.zeroDigit.toString()
                else -> clearedValue
            }
        ) ?: 0

        cleanup(diff)

        if (needInvalidateMask) {
            invalidateMaskDataForFormatted(numberValue)
        }

        replaceChars(clearedValue, 0)

        cursorPosition =
            if (value.length > diff.start && value[diff.start] == decimalFormatSymbols.groupingSeparator) {
                position ?: cursorPosition
            } else {
                abs(value.length - (newValue.length - (position ?: cursorPosition)))
            }.coerceAtMost(value.length)
    }

    private fun NumberFormat.clearFormatter(): NumberFormat {
        (this as? DecimalFormat)?.apply {
            val pattern = toPattern().filter { it != currencyKey }.trim()
            applyPattern(pattern)
        }

        return this
    }

    private fun String.toValidFormat(diff: TextDiff): String {
        val separatorChar = decimalFormatSymbols.decimalSeparator

        var separatorOutOfDiffIndex: Int = -1

        var index = 0

        while (index < this.length) {
            if (this[index] == separatorChar && !inDiff(diff, index)) {
                separatorOutOfDiffIndex = index

                break
            }

            index++
        }

        val replaceCharInDiff =
            if (diff.added == 1 && diff.removed == 0) {
                val diffChar = this[diff.start]

                if (diffChar in separators) diff.start else -1
            } else -1

        val maxSeparatorOffset = currencyFormatter.maximumFractionDigits

        var leftToInsert = maxSeparatorOffset

        if (separatorOutOfDiffIndex != -1) {
            index = separatorOutOfDiffIndex

            while (index < length) {
                if (this[index].isDigit() && !inDiff(diff, index)) leftToInsert--
                index++
            }
        } else {
            var oldSeparatorLeft = false

            forEachIndexed { i, char ->
                val inDiff = inDiff(diff, i)

                when {
                    char == separatorChar -> {
                        oldSeparatorLeft = true
                    }
                    !inDiff && oldSeparatorLeft && char.isDigit() -> {
                        leftToInsert--
                    }
                }
            }
        }

        val containsSeparator = contains(separatorChar) || replaceCharInDiff != -1

        return buildString {
            index = this@toValidFormat.length - 1

            var separatorInserted = false

            while (index >= 0) {
                val char = this@toValidFormat[index]

                val canInsertSeparator = length <= maxSeparatorOffset

                if (char.isDigit()) {
                    if (inDiff(diff, index) && !separatorInserted && containsSeparator) {
                        if (leftToInsert > 0) {
                            append(char)

                            leftToInsert--
                        }
                    } else {
                        append(char)
                    }
                } else if (canInsertSeparator && separatorOutOfDiffIndex == -1 && index == replaceCharInDiff) {
                    append(separatorChar)

                    separatorInserted = true
                } else if (canInsertSeparator && char == separatorChar && (separatorOutOfDiffIndex == index || separatorOutOfDiffIndex == -1)) {
                    append(separatorChar)

                    separatorInserted = true
                    separatorOutOfDiffIndex = index
                }

                index--
            }
        }
            .reversed()
            .let {
                index = it.indexOfFirst { char -> char != decimalFormatSymbols.zeroDigit }

                if (index <= 0) return@let it

                val firstNotZeroChar = it[index]

                if (firstNotZeroChar == decimalFormatSymbols.decimalSeparator) {
                    it.drop(index - 1)
                } else {
                    it.drop(index)
                }
            }
    }

    private fun inDiff(diff: TextDiff, index: Int): Boolean =
        diff.start <= index && index < diff.start + diff.added

    private fun formatPattern(pattern: String): String {
        return buildString {
            pattern.forEach { char ->
                when {
                    char.isDigit() -> append('#')
                    else -> append(char)
                }
            }
        }
    }

    override fun onException(exception: Exception) {
        onError.invoke(exception)
    }
}
