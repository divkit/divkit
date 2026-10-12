package com.yandex.div.core.json

import org.json.JSONArray
import org.json.JSONObject


internal fun JSONObject.copy(): JSONObject {
    val result = JSONObject()
    keys().forEach { key ->
        result.put(key, opt(key))
    }
    return result
}

internal fun JSONArray.copy(): JSONArray {
    val result = JSONArray()
    for (index in 0 until length()) {
        result.put(opt(index) ?: JSONObject.NULL)
    }
    return result
}
