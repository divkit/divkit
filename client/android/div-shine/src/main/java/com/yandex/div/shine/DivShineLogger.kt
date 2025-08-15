package com.yandex.div.shine

import com.yandex.div.json.ParsingErrorLogger
import java.lang.Exception

interface DivShineLogger: ParsingErrorLogger {
    companion object {
        val STUB = object : DivShineLogger {
            override fun logError(e: Exception) = Unit
        }
    }
}