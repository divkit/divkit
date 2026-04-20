package com.yandex.div.compose

import org.junit.Assert.fail

class TestReporter : DivReporter() {
    private val _errors = mutableListOf<String>()
    private val _warnings = mutableListOf<String>()

    var failOnError = true

    val errors: List<String>
        get() = _errors

    val warnings: List<String>
        get() = _warnings

    val lastError: String?
        get() = errors.lastOrNull()

    val lastWarning: String?
        get() = warnings.lastOrNull()

    override fun reportWarning(message: String) {
        _warnings.add(message)
    }

    override fun reportError(message: String) {
        _errors.add(message)

        if (failOnError) {
            fail(message)
        }
    }

    override fun reportError(e: Exception) {
        _errors.add(e.message ?: format(e))

        if (failOnError) {
            fail(format(e))
        }
    }

    fun popErrors(): List<String> = errors.toList().also { _errors.clear() }
}

private fun format(e: Throwable): String {
    return generateSequence(e) { it.cause }
        .mapNotNull { it.message }
        .joinToString("\n")
}
