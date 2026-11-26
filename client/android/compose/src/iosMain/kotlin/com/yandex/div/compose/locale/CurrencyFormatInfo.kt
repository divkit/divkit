@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.yandex.div.compose.locale

import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.canonicalLanguageIdentifierFromString
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

internal actual data class CurrencyFormatInfo actual constructor(
    actual val languageTag: String,
    actual val decimalSeparator: Char,
    actual val groupingSeparator: Char,
    actual val groupingSize: Int,
    actual val maxFractionDigits: Int,
) {
    actual constructor(languageTag: String?) : this(
        if (languageTag == null) NSLocale.currentLocale else NSLocale(localeIdentifier = languageTag),
    )

    private constructor(
        locale: NSLocale,
        formatter: NSNumberFormatter = NSNumberFormatter().apply {
            this.locale = locale
            numberStyle = NSNumberFormatterCurrencyStyle
        },
    ) : this(
        languageTag = NSLocale.canonicalLanguageIdentifierFromString(locale.localeIdentifier),
        decimalSeparator = formatter.decimalSeparator.firstOrNull() ?: '.',
        groupingSeparator = formatter.groupingSeparator.firstOrNull() ?: ',',
        groupingSize = if (formatter.usesGroupingSeparator) formatter.groupingSize.toInt() else 0,
        maxFractionDigits = formatter.maximumFractionDigits.toInt(),
    )
}
