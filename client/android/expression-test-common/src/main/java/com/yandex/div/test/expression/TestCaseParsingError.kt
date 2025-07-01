package com.yandex.div.test.expression

import org.json.JSONException
import org.json.JSONObject

class TestCaseParsingError(
    private val fileName: String,
    private val json: JSONObject?,
    private val error: Exception,
) {
    override fun toString() = "${fileName}/${error.message?:"?"}"

    @Suppress("NewApi")
    fun throwError(): Nothing {
        if (json == null) {
            throw JSONException("$fileName parsing failed!", error)
        }
        throw JSONException(
            "Test case parsing failed: $fileName, $json",
            error
        )
    }
}
