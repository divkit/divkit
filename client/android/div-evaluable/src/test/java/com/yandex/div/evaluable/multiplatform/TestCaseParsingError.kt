package com.yandex.div.evaluable.multiplatform

import org.json.JSONException
import org.json.JSONObject
import java.io.File

class TestCaseParsingError(
    private val name: String?,
    private val file: File,
    private val json: JSONObject?,
    private val error: Exception,
) {
    override fun toString() = name ?: "${file.name}/${error.message?:"?"}"

    fun throwError(): Nothing {
        if (json == null) {
            throw JSONException("$file parsing failed!", error)
        }
        val caseNameOrEmpty = name ?: ""
        throw JSONException(
            "Case $caseNameOrEmpty parsing failed! (file: $file , json: $json)",
            error
        )
    }
}