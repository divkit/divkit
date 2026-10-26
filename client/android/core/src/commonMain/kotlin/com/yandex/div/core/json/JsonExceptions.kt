@file:JvmName("JsonExceptions")

package com.yandex.div.core.json

import kotlin.jvm.JvmName
import kotlinx.serialization.json.JsonArray as KtxArray
import kotlinx.serialization.json.JsonElement as KtxElement
import kotlinx.serialization.json.JsonNull as KtxNull
import kotlinx.serialization.json.JsonObject as KtxObject
import kotlinx.serialization.json.JsonPrimitive as KtxPrimitive

internal fun malformedJson(cause: Throwable?): RuntimeException {
    return IllegalArgumentException("Malformed JSON: ${cause?.message}", cause)
}

internal fun unexpectedRootObject(element: KtxElement): RuntimeException {
    return IllegalArgumentException("Expected JSON object, got ${element.typeName()}")
}

internal fun unexpectedRootArray(element: KtxElement): RuntimeException {
    return IllegalArgumentException("Expected JSON array, got ${element.typeName()}")
}

internal fun missingValue(key: String): RuntimeException {
    return NoSuchElementException("Value for key '$key' is missing or null")
}

internal fun missingValue(index: Int): RuntimeException {
    return NoSuchElementException("Value at index $index is missing or null")
}

internal fun unsupportedValue(value: Any): RuntimeException {
    return IllegalArgumentException("Unsupported JSON value type: ${value::class.simpleName}")
}

internal fun typeMismatch(key: String, value: Any, expected: String?): RuntimeException {
    return ClassCastException("Value for key '$key' of type ${value.typeName()} cannot be read as $expected")
}

internal fun typeMismatch(index: Int, value: Any, expected: String?): RuntimeException {
    return ClassCastException("Value at index $index of type ${value.typeName()} cannot be read as $expected")
}

private fun Any.typeName(): String = when (this) {
    is Number -> "number"
    is Boolean -> "boolean"
    is String -> "string"
    is JsonObject -> "object"
    is JsonArray -> "array"
    else -> this::class.simpleName ?: "unknown"
}

private fun KtxElement.typeName(): String = when (this) {
    is KtxObject -> "object"
    is KtxArray -> "array"
    is KtxNull -> "null"
    is KtxPrimitive -> when {
        isString -> "string"
        content == "true" || content == "false" -> "boolean"
        else -> "number"
    }
}
