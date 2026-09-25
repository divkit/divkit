package com.yandex.div.core.view2

import com.yandex.div.internal.Assert

inline fun disableAssertions(crossinline block: () -> Unit) {
    val previousValue = Assert.isEnabled
    Assert.isEnabled = false
    try {
        block()
    } finally {
        Assert.isEnabled = previousValue
    }
}
