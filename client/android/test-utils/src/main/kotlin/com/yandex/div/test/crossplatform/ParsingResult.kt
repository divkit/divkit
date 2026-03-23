package com.yandex.div.test.crossplatform

import org.json.JSONObject

sealed class ParsingResult<out T> {

    class Success<out T>(val value: T) : ParsingResult<T>() {
        override fun toString() = value.toString()
    }

    class Error(
        private val fileName: String,
        private val json: JSONObject? = null,
        private val error: Exception
    ) : ParsingResult<Nothing>() {
        fun throwException(): Nothing {
            if (json == null) {
                throw Exception("$fileName parsing failed", error)
            }
            throw Exception(
                "Test case parsing failed: $fileName, $json",
                error
            )
        }

        override fun toString() = "$fileName/${error.message ?: "?"}"
    }

    fun getOrThrow(): T {
        when (this) {
            is Success -> return value
            is Error -> throwException()
        }
    }
}
