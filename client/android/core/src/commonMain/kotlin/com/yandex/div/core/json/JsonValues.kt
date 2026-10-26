@file:JvmName("JsonValues")
@file:Suppress("TooManyFunctions")

package com.yandex.div.core.json

import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import org.json.JSONArray
import org.json.JSONObject
import kotlin.jvm.JvmName
import kotlinx.serialization.json.JsonArray as KtxArray
import kotlinx.serialization.json.JsonElement as KtxElement
import kotlinx.serialization.json.JsonObject as KtxObject

internal fun Any?.asJsonValue(): Any? = when (this) {
    null, JSONObject.NULL -> null
    is JSONObject -> OrgJsonObject(this)
    is JSONArray -> OrgJsonArray(this)
    is Boolean, is String, is Int, is Double -> this
    is Long -> if (this in Int.MIN_VALUE..Int.MAX_VALUE) toInt() else this
    is Byte, is Short -> toInt()
    is Number -> toString().toNumberValue() ?: toDouble()
    else -> toString()
}

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

internal fun JSONObject.toKtxJsonObject(): KtxObject {
    val source = this
    return buildJsonObject {
        source.keys().forEach { key ->
            put(key, source.opt(key).toKtxJsonElement())
        }
    }
}

internal fun JSONArray.toKtxJsonArray(): KtxArray {
    val source = this
    return buildJsonArray {
        for (index in 0 until source.length()) {
            add(source.opt(index).toKtxJsonElement())
        }
    }
}

private fun Any?.toKtxJsonElement(): KtxElement = when (this) {
    null, JSONObject.NULL -> JsonNull
    is JSONObject -> toKtxJsonObject()
    is JSONArray -> toKtxJsonArray()
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(requireFinite(this))
    else -> JsonPrimitive(toString())
}

internal fun KtxObject.toOrgJsonObject(): JSONObject {
    val result = JSONObject()
    forEach { (key, element) ->
        result.put(key, element.toOrgJsonValue())
    }
    return result
}

internal fun KtxArray.toOrgJsonArray(): JSONArray {
    val result = JSONArray()
    forEach { element ->
        result.put(element.toOrgJsonValue())
    }
    return result
}

private fun KtxElement.toOrgJsonValue(): Any = when (this) {
    is KtxObject -> toOrgJsonObject()
    is KtxArray -> toOrgJsonArray()
    is JsonPrimitive -> when (val value = toPrimitiveValue()) {
        null -> JSONObject.NULL
        is Number -> requireFinite(value)
        else -> value
    }
}

private fun JsonPrimitive.toPrimitiveValue(): Any? {
    if (this is JsonNull) return null
    if (isString) return content
    return content.toBooleanStrictOrNull()
        ?: content.toNumberValue()
        ?: content
}

private fun String.toNumberValue(): Number? {
    return toIntOrNull() ?: toLongOrNull() ?: toDoubleOrNull()
}
