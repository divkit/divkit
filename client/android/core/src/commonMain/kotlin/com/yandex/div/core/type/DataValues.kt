@file:JvmName("DataValues")

package com.yandex.div.core.type

import com.yandex.div.core.json.JsonElement
import com.yandex.div.core.json.requireFinite
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmName

/**
 * Normalizes a value to the type that JSON-backed containers return for it.
 */
internal fun Any?.toDataValue(): Any? = when (this) {
    null, is String, is Boolean, is Int, is DataObject, is DataArray -> this
    is Long -> if (this in Int.MIN_VALUE..Int.MAX_VALUE) toInt() else this
    is Float -> requireFinite(this).toString().toDouble()
    is Double -> requireFinite(this)
    else -> throw IllegalArgumentException("Unsupported value type: ${this::class.qualifiedName}")
}

internal fun DataObject.toJsonString(): String = buildString { appendObject(this@toJsonString) }

internal fun DataArray.toJsonString(): String = buildString { appendArray(this@toJsonString) }

private fun StringBuilder.appendObject(value: DataObject) {
    append('{')
    value.forEachIndexed { index, (key, item) ->
        if (index > 0) append(',')
        append(JsonPrimitive(key))
        append(':')
        appendValue(item)
    }
    append('}')
}

private fun StringBuilder.appendArray(value: DataArray) {
    append('[')
    value.forEachIndexed { index, item ->
        if (index > 0) append(',')
        appendValue(item)
    }
    append(']')
}

private fun StringBuilder.appendValue(value: Any?) {
    when (value) {
        null -> append("null")
        is String -> append(JsonPrimitive(value))
        is Boolean -> append(value)
        is Number -> append(JsonPrimitive(value))
        is JsonElement -> append(value)
        is DataObject -> appendObject(value)
        is DataArray -> appendArray(value)
        else -> append(JsonPrimitive(value.toString()))
    }
}
