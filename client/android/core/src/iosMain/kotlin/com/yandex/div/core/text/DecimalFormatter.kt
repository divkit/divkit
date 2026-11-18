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
