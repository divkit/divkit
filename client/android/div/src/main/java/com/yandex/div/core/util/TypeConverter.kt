package com.yandex.div.core.util

import com.yandex.div.internal.KAssert

inline fun Long.toIntSafely(): Int {
    val shiftedValue = this.shr(31)
    if (shiftedValue == 0L || shiftedValue == -1L) {
        return this.toInt()
    }
    KAssert.fail { "Unable convert '$this' to Int" }
    return if (this > 0L) {
        Int.MAX_VALUE
    } else {
        Int.MIN_VALUE
    }
}
