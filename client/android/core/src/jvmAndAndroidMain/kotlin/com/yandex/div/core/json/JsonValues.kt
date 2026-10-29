@file:JvmName("JsonValuesJvm")

package com.yandex.div.core.json

import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import org.json.JSONArray
import org.json.JSONObject
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
