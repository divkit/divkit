package com.yandex.div.core.view2

fun disableDivkitAssert(block: () -> Unit) {
    com.yandex.div.core.util.Assert.setEnabled(false)
    try {
        block()
    } finally {
        com.yandex.div.core.util.Assert.setEnabled(true)
    }
}
