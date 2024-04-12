package com.yandex.div.internal.util

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public fun Number.toBoolean(): Boolean? {
    return when (toInt()) {
        0 -> false
        1 -> true
        else -> null
    }
}

@InternalApi
public fun Int.toBoolean(): Boolean {
    return when (this) {
        0 -> false
        1 -> true
        else -> throw IllegalArgumentException("Unable to convert $this to boolean")
    }
}
