package com.yandex.div.compose.locale

/** Currency input rules for the resolved [languageTag]; a zero [groupingSize] disables grouping. */
internal expect class CurrencyFormatInfo {

    /** A null [languageTag] uses the current platform default locale. */
    constructor(languageTag: String?)

    constructor(
        languageTag: String,
        decimalSeparator: Char,
        groupingSeparator: Char,
        groupingSize: Int,
        maxFractionDigits: Int,
    )

    val languageTag: String
    val decimalSeparator: Char
    val groupingSeparator: Char
    val groupingSize: Int
    val maxFractionDigits: Int
}
