package com.yandex.div.core.text

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public expect class DecimalFormatter(pattern: String, symbols: DecimalSymbols) {

    public fun format(value: Long): String

    public fun format(value: Double): String
}
