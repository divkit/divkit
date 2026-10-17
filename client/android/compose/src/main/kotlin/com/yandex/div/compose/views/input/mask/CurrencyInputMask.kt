package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivCurrencyInputMask
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun DivCurrencyInputMask.rememberCurrencyInputMask(): InputMask {
    val localeTag = locale?.observedValue()
    val reporter = LocalComponent.current.reporter

    return remember(localeTag) {
        val locale = if (localeTag == null) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(localeTag).also {
                val finalTag = it.toLanguageTag()
                if (finalTag != localeTag) {
                    reporter.reportWarning("Original locale tag '$localeTag' is not equals to final one '$finalTag'")
                }
            }
        }
        CurrencyInputMask(locale)
    }
}

internal class CurrencyInputMask(locale: Locale) : InputMask() {
    private val formatter = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    private val decimalSeparator = formatter.decimalFormatSymbols.decimalSeparator
    private val groupingSeparator = formatter.decimalFormatSymbols.groupingSeparator
    private val groupingSize = if (formatter.isGroupingUsed) formatter.groupingSize else 0
    private val maxFractionDigits = formatter.maximumFractionDigits

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
