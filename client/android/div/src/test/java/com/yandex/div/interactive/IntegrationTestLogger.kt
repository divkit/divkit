package com.yandex.div.interactive

import com.yandex.div.json.ParsingErrorLogger

class IntegrationTestLogger : ParsingErrorLogger {

    private val _messages = mutableListOf<String>()
    val messages: List<String> get() = _messages

    override fun logError(e: Exception) {
        e.cause?.message?.let { _messages.add(it) }
    }

    fun logErrorDirectly(e: Throwable) {
        e.message?.let { _messages.add(it) }
    }

    fun clear() = _messages.clear()
}
