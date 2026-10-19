package com.yandex.div.parser.json

import com.yandex.div.json.ParsingErrorLogger

class ThrowingErrorLogger : ParsingErrorLogger {
    override fun logError(e: Exception) = throw LoggedException(e)
}

class LoggedException(cause: Exception) : RuntimeException(cause)
