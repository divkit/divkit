@file:JvmName("Json")
@file:Suppress("TooManyFunctions")

package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.serialization.json.Json as KtxJson
import kotlinx.serialization.json.JsonArray as KtxArray
import kotlinx.serialization.json.JsonObject as KtxObject

@ExperimentalApi
public fun JsonObject(): JsonObject {
    return KtxJsonObject(KtxObject(emptyMap()))
}

@ExperimentalApi
public fun JsonObject(source: String): JsonObject {
    val element = runCatching { KtxJson.parseToJsonElement(source) }.getOrElse { e -> throw malformedJson(e) }
    val `object` = runCatching { element.jsonObject }.getOrElse { throw unexpectedRootObject(element) }
    return KtxJsonObject(`object`)
}

@InternalApi
public fun JsonObject(source: JSONObject): JsonObject {
    return OrgJsonObject(source)
}

@InternalApi
public fun JsonObject(source: KtxObject): JsonObject {
    return KtxJsonObject(source)
}

@ExperimentalApi
public fun JsonObject(builder: JsonObject.Builder.() -> Unit): JsonObject {
    return JsonObject.builder(isShared = false).apply(builder).build()
}

@ExperimentalApi
public fun JsonArray(): JsonArray {
    return KtxJsonArray(KtxArray(emptyList()))
}

@ExperimentalApi
public fun JsonArray(source: String): JsonArray {
    val element = runCatching { KtxJson.parseToJsonElement(source) }.getOrElse { e -> throw malformedJson(e) }
    val array = runCatching { element.jsonArray }.getOrElse { throw unexpectedRootArray(element) }
    return KtxJsonArray(array)
}

@InternalApi
public fun JsonArray(source: JSONArray): JsonArray {
    return OrgJsonArray(source)
}

@InternalApi
public fun JsonArray(source: KtxArray): JsonArray {
    return KtxJsonArray(source)
}

@ExperimentalApi
public fun JsonArray(builder: JsonArray.Builder.() -> Unit): JsonArray {
    return JsonArray.builder(isShared = false).apply(builder).build()
}

@InternalApi
public fun JSONObject.asJsonObject(): JsonObject {
    return OrgJsonObject(this)
}

@InternalApi
public fun KtxObject.asJsonObject(): JsonObject {
    return KtxJsonObject(this)
}

@InternalApi
public fun JSONArray.asJsonArray(): JsonArray {
    return OrgJsonArray(this)
}

@InternalApi
public fun KtxArray.asJsonArray(): JsonArray {
    return KtxJsonArray(this)
}
