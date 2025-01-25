package com.yandex.div.test.expression

import org.json.JSONException
import org.json.JSONObject

class TestCaseParsingError(
    private val name: String?,
    private val fileName: String,
    private val json: JSONObject?,
    private val error: Exception,
) {
    override fun toString() = name ?: "${fileName}/${error.message?:"?"}"

    @Suppress("NewApi")
    fun throwError(): Nothing {
        if (json == null) {
            throw JSONException("$fileName parsing failed!", error)
        }
        val caseNameOrEmpty = name ?: ""
        throw JSONException(
            "Case $caseNameOrEmpty parsing failed! (file: $fileName , json: $json)",
            error
        )
    }
}
