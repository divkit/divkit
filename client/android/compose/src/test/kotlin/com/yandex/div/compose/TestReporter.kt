package com.yandex.div.compose

import org.junit.Assert.fail

class TestReporter : DivReporter() {

    var failOnError = true

    var lastError: String? = null
        private set

    var lastWarning: String? = null
        private set

    override fun reportWarning(message: String) {
        lastWarning = message
    }

    override fun reportError(message: String) {
        lastError = message

        if (failOnError) {
            fail(message)
        }
    }

    override fun reportError(e: Exception) {
        lastError = e.message

        if (failOnError) {
            fail(format(e))
        }
    }
}

private fun format(e: Throwable): String {
    return generateSequence(e) { it.cause }
        .mapNotNull { it.message }
        .joinToString("\n")
}
