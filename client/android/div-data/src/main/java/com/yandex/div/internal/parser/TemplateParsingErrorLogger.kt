package com.yandex.div.internal.parser

import com.yandex.div.json.ParsingErrorLogger

internal class TemplateParsingErrorLogger(
    private val logger: ParsingErrorLogger,
    private val templateId: String
) : ParsingErrorLogger {

    override fun logError(e: Exception) = logger.logTemplateError(e, templateId)
}
