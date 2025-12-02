package com.yandex.div.interactive

import com.yandex.div.json.ParsingErrorLogger

class IntegrationTestLogger : ParsingErrorLogger {

    private val _messages = mutableListOf<String>()
    val messages: List<String> get() = _messages

    override fun logError(e: Exception) {
        e.cause?.message?.let { _messages.add(it) }
    }

    fun logErrorDirectly(e: Throwable) {
        collectChainMessages(e)
    }

    private fun collectChainMessages(t: Throwable) {
        generateSequence(t) { it.cause }
            .mapNotNull { it.message }
            .forEach { _messages.add(it) }
    }

    fun clear() = _messages.clear()
}
