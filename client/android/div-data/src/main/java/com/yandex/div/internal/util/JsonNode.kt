package com.yandex.div.internal.util

import org.json.JSONArray
import org.json.JSONObject

/**
 * Wrapper class that can hold [JSONObject] or [JSONArray] instance.
 */
sealed class JsonNode {
    abstract fun dump(): String
}

class JsonObject(val value: JSONObject) : JsonNode() {
    override fun dump() = value.toString()
}

class JsonArray(val value: JSONArray) : JsonNode() {
    override fun dump() = value.toString()
}
