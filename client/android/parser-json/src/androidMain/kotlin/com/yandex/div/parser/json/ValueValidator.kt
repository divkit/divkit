package com.yandex.div.parser.json

import com.yandex.div.internal.parser.ValueValidator

internal fun interface ValueValidator<T> : ValueValidator<T> {
    override fun isValid(value: T): Boolean
}
