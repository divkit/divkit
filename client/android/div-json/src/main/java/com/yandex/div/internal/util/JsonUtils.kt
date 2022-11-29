package com.yandex.div.internal.util

import org.json.JSONArray
import org.json.JSONObject

inline fun <reified T> JSONObject.forEach(action: (String, T) -> Unit) {
    val keys = keys()
    for (key in keys) {
        val value = get(key)
        if (value is T) action(key, value)
    }
}


inline fun <reified T> JSONArray.forEach(action: (Int, T) -> Unit) {
    val length = length()
    for (i in 0 until length) {
        val value = get(i)
        if (value is T) action(i, value)
    }
}

inline fun <reified T> JSONObject.forEachNullable(action: (String, T?) -> Unit) {
    val keys = keys()
    for (key in keys) {
        val value = opt(key)
        if (value is T?) action(key, value)
    }
}

inline fun <reified T> JSONArray.forEachNullable(action: (Int, T?) -> Unit) {
    val length = length()
    for (i in 0 until length) {
        val value = opt(i)
        if (value is T?) action(i, value)
    }
}

inline fun <R> JSONArray.map(mapping: (Any) -> R): List<R> {
    val length = length()
    val result = ArrayList<R>(length)
    for (i in 0 until length) {
        result.add(mapping(get(i)))
    }
    return result
}

inline fun <R> JSONArray.mapNotNull(mapping: (Any) -> R?): List<R> {
    val length = length()
    val result = ArrayList<R>(length)
    for (i in 0 until length) {
        mapping(get(i))?.let { result.add(it) }
    }
    return result
}

@Suppress("UNCHECKED_CAST")
fun <R : Any> JSONArray.asList(): List<R> = mapNotNull { it as? R }

/**
 * Gets optional string value for the given key.
 *
 * Unlike [JSONObject.optString] this method does not convert not string values to string.
 */
fun JSONObject.getStringOrEmpty(name: String): String {
    val value = opt(name)
    return if (value is String) value else ""
}

fun JSONObject.getStringOrNull(key: String): String? {
    val value = opt(key)
    return if (value is String) value else null
}

fun JSONObject.summary(indentSpaces: Int = 0): String {
    return JsonPrinter(indentSpaces = indentSpaces, nestingLimit = 1).print(this)
}

fun JSONArray.summary(indentSpaces: Int = 0): String {
    return JsonPrinter(indentSpaces = indentSpaces, nestingLimit = 1).print(this)
}

fun JSONObject.isEmpty(): Boolean = length() == 0

fun JSONArray.isEmpty(): Boolean = length() == 0
