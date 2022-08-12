package com.yandex.div.json

import org.json.JSONArray
import org.json.JSONObject

/**
 * Wrapper class that can hold [JSONObject] or [JSONArray] instance.
 */
sealed class Json {
    abstract fun dump(): String
}

class JsonObject(val value: JSONObject) : Json() {
    override fun dump() = value.toString()
}

class JsonArray(val value: JSONArray) : Json() {
    override fun dump() = value.toString()
}
