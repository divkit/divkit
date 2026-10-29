@file:JvmName("JsonJvm")

package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import org.json.JSONArray
import org.json.JSONObject

@InternalApi
public fun JsonObject(source: JSONObject): JsonObject {
    return OrgJsonObject(source)
}

@InternalApi
public fun JsonArray(source: JSONArray): JsonArray {
    return OrgJsonArray(source)
}

@InternalApi
public fun JSONObject.asJsonObject(): JsonObject {
    return OrgJsonObject(this)
}

@InternalApi
public fun JSONArray.asJsonArray(): JsonArray {
    return OrgJsonArray(this)
}

@ExperimentalApi
public fun JsonObject.asOrgJsonObject(): JSONObject = when (this) {
    is OrgJsonObject -> source
    else -> asKtxJsonObject().toOrgJsonObject()
}

@ExperimentalApi
public fun JsonArray.asOrgJsonArray(): JSONArray = when (this) {
    is OrgJsonArray -> source
    else -> asKtxJsonArray().toOrgJsonArray()
}
