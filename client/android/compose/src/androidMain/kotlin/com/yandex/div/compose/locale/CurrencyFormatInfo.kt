package com.yandex.div.compose.locale

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

internal actual data class CurrencyFormatInfo actual constructor(
    actual val languageTag: String,
    actual val decimalSeparator: Char,
    actual val groupingSeparator: Char,
    actual val groupingSize: Int,
    actual val maxFractionDigits: Int,
) {
    actual constructor(languageTag: String?) : this(
        if (languageTag == null) Locale.getDefault() else Locale.forLanguageTag(languageTag),
    )

    private constructor(
        locale: Locale,
        formatter: DecimalFormat = NumberFormat.getCurrencyInstance(locale) as DecimalFormat,
        symbols: DecimalFormatSymbols = formatter.decimalFormatSymbols,
    ) : this(
        languageTag = locale.toLanguageTag(),
        decimalSeparator = symbols.decimalSeparator,
        groupingSeparator = symbols.groupingSeparator,
        groupingSize = if (formatter.isGroupingUsed) formatter.groupingSize else 0,
        maxFractionDigits = formatter.maximumFractionDigits,
    )
}
