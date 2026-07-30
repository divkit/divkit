package com.yandex.div.backdrop.util

import org.json.JSONObject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal fun JSONObject.optionalString(key: String): String? {
    return opt(key) as? String
}

@OptIn(ExperimentalContracts::class)
internal inline fun <R> JSONObject.optObject(key: String, action: (JSONObject) -> R): R? {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    return optJSONObject(key)?.let(action)
}
