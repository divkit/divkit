package com.yandex.div.compose

import org.junit.Assert.fail

class TestReporter : DivReporter() {

    override fun reportError(message: String) {
        fail(message)
    }

    override fun reportError(message: String, e: Throwable) {
        fail("$message\n${e.message}")
    }
}
