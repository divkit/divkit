package com.yandex.div.json

internal class TemplateParsingErrorLogger(
    private val logger: ParsingErrorLogger,
    private val templateId: String
) : ParsingErrorLogger {

    override fun logError(e: Exception) = logger.logTemplateError(e, templateId)
}
