package com.yandex.div.compose.views.input.mask

import com.yandex.div.compose.locale.CurrencyFormatInfo

internal class CurrencyInputMask(format: CurrencyFormatInfo) : InputMask() {
    private val decimalSeparator = format.decimalSeparator
    private val groupingSeparator = format.groupingSeparator
    private val groupingSize = format.groupingSize
    private val maxFractionDigits = format.maxFractionDigits

    /** The raw variable holds the decimal separator of the locale or, as the View renderer writes it, the point. */
    override fun sanitize(raw: String): String {
        return digitsAround(raw, if (raw.contains(decimalSeparator)) decimalSeparator else DECIMAL_POINT)
    }

    /** An edit holds the decimal separator of the locale only: a typed point or comma was mapped to it. */
    override fun sanitizeEdit(text: String): String = digitsAround(text, decimalSeparator)

    /** A typed point or comma is the decimal separator of any locale, as in the View renderer. */
    override fun typedChar(char: Char): Char {
        return if (char == DECIMAL_POINT || char == DECIMAL_COMMA) decimalSeparator else char
    }

    // The raw variable stores the decimal point of any locale, as the View renderer does.
    override fun toVariableValue(raw: String): String = raw.replace(decimalSeparator, DECIMAL_POINT)

    /**
     * The digits of [text] around its first [separator], at most [maxFractionDigits] of them after it.
     * Every other character, the grouping separators among them, is dropped.
     */
    private fun digitsAround(text: String, separator: Char): String {
        val separatorIndex = if (maxFractionDigits > 0) text.indexOf(separator) else -1
        if (separatorIndex < 0) return text.filter(Char::isDigit)
        val integer = text.substring(0, separatorIndex).filter(Char::isDigit)
        val fraction = text.substring(separatorIndex + 1).filter(Char::isDigit).take(maxFractionDigits)
        return integer + decimalSeparator + fraction
    }

    override fun pattern(raw: String): String {
        val separatorIndex = raw.indexOf(decimalSeparator)
        val integerLength = if (separatorIndex < 0) raw.length else separatorIndex
        val integer = DIGIT.toString().repeat(integerLength)
        // The slots are all alike, so grouping them from the end is grouping from the start, reversed.
        val grouped = if (groupingSize == 0) integer else {
            integer.chunked(groupingSize).joinToString(groupingSeparator.toString()).reversed()
        }
        if (separatorIndex < 0) return grouped
        return grouped + decimalSeparator + DIGIT.toString().repeat(raw.length - separatorIndex - 1)
    }

    override fun isDynamic(patternChar: Char): Boolean = patternChar == DIGIT || patternChar == decimalSeparator
}

private const val DIGIT = '#'
private const val DECIMAL_POINT = '.'
private const val DECIMAL_COMMA = ','
