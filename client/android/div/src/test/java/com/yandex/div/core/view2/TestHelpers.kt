package com.yandex.div.core.view2

import com.yandex.div.internal.Assert

inline fun disableAssertions(crossinline block: () -> Unit) {
    Assert.setEnabled(false)
    try {
        block()
    } finally {
        Assert.setEnabled(true)
    }
}
