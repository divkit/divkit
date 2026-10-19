package com.yandex.div.parser.json.logger

import com.yandex.div.json.ParsingErrorLogger

internal class TemplateParsingErrorLogger(
    private val logger: ParsingErrorLogger,
    private val templateId: String
) : ParsingErrorLogger {

    override fun logError(e: Exception) = logger.logTemplateError(e, templateId)
}
