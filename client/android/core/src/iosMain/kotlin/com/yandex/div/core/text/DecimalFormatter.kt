@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.core.text

import com.yandex.div.core.annotations.InternalApi
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSNumberFormatterRoundHalfEven

@InternalApi
public actual class DecimalFormatter actual constructor(
    pattern: String,
    symbols: DecimalSymbols,
) {
    init {
        // Foundation accepts malformed patterns in the subset used by DivKit's decimalFormat.
        require(pattern.any { it !in DECIMAL_PATTERN_SYMBOLS } || pattern.isValidDecimalPattern()) {
            "Malformed decimal format pattern: $pattern"
        }
    }

    private val formatter = NSNumberFormatter().apply {
        locale = symbols.locale
        numberStyle = NSNumberFormatterDecimalStyle
        positiveFormat = pattern
        negativeFormat = "-$pattern"
        roundingMode = NSNumberFormatterRoundHalfEven
    }

    public actual fun format(value: Long): String =
        formatNumber(NSNumber(longLong = value))

    public actual fun format(value: Double): String {
        // Preserve the JVM implementation's narrowing before decimal rounding.
        return formatNumber(NSNumber(double = value.toFloat().toDouble()))
    }

    private fun formatNumber(value: NSNumber): String =
        checkNotNull(formatter.stringFromNumber(value)) {
            "Unable to format number."
        }
}

private val DECIMAL_PATTERN_SYMBOLS = setOf('#', '0', ',', '.')

private fun String.isValidDecimalPattern(): Boolean {
    if (isEmpty()) return true
    val decimalPoint = indexOf('.')
    val grouping = lastIndexOf(',')
    if (decimalPoint != lastIndexOf('.') || grouping == lastIndex) return false
    if (grouping >= 0 && decimalPoint >= 0 && grouping >= decimalPoint - 1) return false

    val digits = replace(",", "")
    val firstRequired = digits.indexOf('0')
    if (firstRequired < 0) return true
    val lastRequired = digits.lastIndexOf('0')
    if ('#' in digits.substring(firstRequired, lastRequired + 1)) return false

    val pointWithoutGrouping = digits.indexOf('.')
    return if (pointWithoutGrouping < 0) {
        lastRequired == digits.lastIndex
    } else {
        pointWithoutGrouping in firstRequired - 1..lastRequired + 1
    }
}
