package com.yandex.div.internal.util

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public fun Number.toBoolean(): Boolean? {
    return when(this) {
        is Double -> this.toBoolean()
        else -> toInt().toBoolean()
    }
}

@InternalApi
public fun Int.toBoolean(): Boolean? {
    return when(this) {
        0 -> false
        1 -> true
        else -> null
    }
}

private fun Double.toBoolean(): Boolean? {
    return when(this) {
        0.0 -> false
        1.0 -> true
        else -> null
    }
}
