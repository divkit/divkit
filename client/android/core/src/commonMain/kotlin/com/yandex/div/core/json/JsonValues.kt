@file:JvmName("JsonValues")
@file:Suppress("TooManyFunctions")

package com.yandex.div.core.json

import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmName
import kotlinx.serialization.json.JsonArray as KtxArray
import kotlinx.serialization.json.JsonElement as KtxElement
import kotlinx.serialization.json.JsonObject as KtxObject

internal fun KtxElement?.asJsonValue(): Any? = when (this) {
    null -> null
    is KtxObject -> KtxJsonObject(this)
    is KtxArray -> KtxJsonArray(this)
    is JsonPrimitive -> toPrimitiveValue()
}

internal fun Any?.asInt(): Int? = when (this) {
    is Int -> this
    is Double -> if (isFinite()) toInt() else null
    is Number -> toInt()
    is String -> toNumberValue()?.asInt()
    else -> null
}

internal fun Any?.asLong(): Long? = when (this) {
    is Long -> this
    is Double -> if (isFinite()) toLong() else null
    is Number -> toLong()
    is String -> toNumberValue()?.asLong()
    else -> null
}

internal fun Any?.asFloat(): Float? = when (this) {
    is Float -> this
    is Number -> toFloat()
    is String -> toFloatOrNull()
    else -> null
}

internal fun Any?.asDouble(): Double? = when (this) {
    is Double -> this
    is Number -> toDouble()
    is String -> toDoubleOrNull()
    else -> null
}

internal fun Any?.asBoolean(): Boolean? = when (this) {
    is Boolean -> this
    is String -> toBooleanStrictOrNull()
    else -> null
}

internal fun Any?.asString(): String? = when (this) {
    is String -> this
    is Boolean, is Number -> toString()
    else -> null
}

internal fun requireFinite(value: Double): Double {
    require(value.isFinite()) { "JSON does not allow non-finite numbers: $value" }
    return value
}

internal fun requireFinite(value: Float): Float {
    require(value.isFinite()) { "JSON does not allow non-finite numbers: $value" }
    return value
}

internal fun requireFinite(value: Number): Number {
    return when (value) {
        is Float -> requireFinite(value)
        is Double -> requireFinite(value)
        else -> value
    }
}

internal inline fun <reified T : Any> JsonObject.requireValue(key: String, transform: (Any) -> T?): T {
    val value = get(key)
    return transform(value) ?: throw typeMismatch(key, value, T::class.simpleName)
}

internal inline fun <reified T : Any> JsonObject.requireValue(key: String, fallback: T, transform: (Any) -> T?): T {
    val value = opt(key) ?: return fallback
    return transform(value) ?: throw typeMismatch(key, value, T::class.simpleName)
}

internal inline fun <reified T : Any> JsonArray.requireValue(index: Int, transform: (Any) -> T?): T {
    val value = get(index)
    return transform(value) ?: throw typeMismatch(index, value, T::class.simpleName)
}

internal inline fun <reified T : Any> JsonArray.requireValue(index: Int, fallback: T, transform: (Any) -> T?): T {
    val value = opt(index) ?: return fallback
    return transform(value) ?: throw typeMismatch(index, value, T::class.simpleName)
}

internal fun JsonPrimitive.toPrimitiveValue(): Any? {
    if (this is JsonNull) return null
    if (isString) return content
    return content.toBooleanStrictOrNull()
        ?: content.toNumberValue()
        ?: content
}

internal fun String.toNumberValue(): Number? {
    return toIntOrNull() ?: toLongOrNull() ?: toDoubleOrNull()
}
