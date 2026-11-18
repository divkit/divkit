package com.yandex.div.core.text

import com.yandex.div.core.annotations.InternalApi
import java.text.DecimalFormat as JavaDecimalFormat

@InternalApi
public actual class DecimalFormatter actual constructor(
    pattern: String,
    symbols: DecimalSymbols
) {

    private val decimalFormat = JavaDecimalFormat(pattern, symbols.value)

    public actual fun format(value: Long): String {
        return decimalFormat.format(value)
    }

    public actual fun format(value: Double): String {
        // Preserve the existing conversion before decimal rounding.
        return decimalFormat.format(value.toFloat())
    }
}
