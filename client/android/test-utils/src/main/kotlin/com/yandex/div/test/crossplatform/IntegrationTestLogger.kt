package com.yandex.div.test.crossplatform

import com.yandex.div.json.ParsingErrorLogger

class IntegrationTestLogger : ParsingErrorLogger {

    private val _messages = mutableListOf<String>()
    val messages: List<String> get() = _messages

    override fun logError(e: Exception) {
        e.cause?.message?.let { _messages.add(it) }
    }

    fun logErrorDirectly(e: Throwable) {
        generateSequence(e) { it.cause }
            .mapNotNull { it.message }
            .forEach { _messages.add(it) }
    }
}
