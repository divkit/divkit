package com.yandex.div.data

import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger

internal class ErrorsCollectorEnvironment(
    origin: ParsingEnvironment,
): ParsingEnvironment {
    private val originLogger = origin.logger
    private val errors = mutableListOf<Exception>()

    override val templates = origin.templates
    override val logger = ParsingErrorLogger { e ->
        errors.add(e)
        originLogger.logError(e)
    }

    fun collectErrors(): List<Exception> {
        return errors.toList()
    }
}

internal fun ParsingEnvironment.withErrorsCollector() = ErrorsCollectorEnvironment(this)

internal fun ParsingEnvironment.collectErrors(): List<Exception> {
    return if (this is ErrorsCollectorEnvironment) {
        this.collectErrors()
    } else {
        emptyList()
    }
}
