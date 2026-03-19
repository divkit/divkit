package com.yandex.div.compose

import org.junit.Assert.fail

class TestReporter : DivReporter() {

    var failOnError = true

    var lastError: String? = null
        private set

    override fun reportError(message: String) {
        lastError = message

        if (failOnError) {
            fail(message)
        }
    }

    override fun reportError(message: String?, throwable: Throwable) {
        val error = format(message, throwable)
        lastError = error

        if (failOnError) {
            fail(error)
        }
    }
}

private fun format(message: String?, throwable: Throwable?): String? {
    if (message != null) {
        return message
    }
    if (throwable != null) {
        return format(throwable.message, throwable.cause)
    }
    return null
}
