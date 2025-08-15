package com.yandex.divkit.demo.div

import org.json.JSONArray
import org.json.JSONObject

/**
 * Use for parsing json when it's unknown to be {} or [].
 */
sealed class JsonResult {
    data class Array(val data: JSONArray) : JsonResult()
    data class Object(val data: JSONObject) : JsonResult()
    object Malformed : JsonResult()
}

fun String.parseJson(): JsonResult =
        when (first()) {
            '{' -> JsonResult.Object(JSONObject(this))
            '[' -> JsonResult.Array(JSONArray(this))
            else -> JsonResult.Malformed
        }
