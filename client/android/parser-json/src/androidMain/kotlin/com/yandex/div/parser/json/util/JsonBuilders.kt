package com.yandex.div.parser.json.util

import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import org.json.JSONArray
import org.json.JSONObject

internal fun JsonObject.Builder.putValue(key: String, value: Any?) = put(key, wrapOrgJson(value))

internal fun JsonArray.Builder.addValue(value: Any?) = add(wrapOrgJson(value))

private fun wrapOrgJson(value: Any?): Any? = when (value) {
    is JSONObject -> JsonObject(value)
    is JSONArray -> JsonArray(value)
    else -> value
}
