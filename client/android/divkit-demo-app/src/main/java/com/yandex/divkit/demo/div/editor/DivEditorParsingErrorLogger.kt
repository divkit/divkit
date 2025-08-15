package com.yandex.divkit.demo.div.editor

import com.yandex.div.json.ParsingErrorLogger

/**
 * This keep all parsing exceptions to be reported later.
 */
class DivEditorParsingErrorLogger : ParsingErrorLogger {

    private val parsingExceptions = mutableListOf<Exception>()

    override fun logError(e: Exception) {
        ParsingErrorLogger.LOG.logError(e)
        parsingExceptions.add(e)
    }

    fun reset() = parsingExceptions.clear()

    fun parsingExceptions(): List<Exception> = parsingExceptions
}
