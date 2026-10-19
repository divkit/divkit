package com.yandex.div.parser.json

import com.yandex.div.internal.parser.ListValidator

internal fun interface ListValidator<T> : ListValidator<T> {
    override fun isValid(value: List<T>): Boolean
}
