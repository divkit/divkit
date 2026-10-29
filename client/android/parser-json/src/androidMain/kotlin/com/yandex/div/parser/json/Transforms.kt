package com.yandex.div.parser.json

import android.net.Uri
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.json.asOrgJsonArray
import com.yandex.div.core.json.asOrgJsonObject
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject

internal typealias Transform<T, R> = (T) -> R

private val AS_IS: Transform<Any?, Any?> = { it }

internal fun <T> doNotConvert(): Transform<T, T> = AS_IS.cast()

@JvmField
internal val COLOR_INT_TO_STRING: Transform<Int, String?> = { value: Int -> Color(value).toString() }

@JvmField
internal val STRING_TO_COLOR_INT: Transform<Any, Int?> = { value ->
    when (value) {
        is String -> Color.parse(value).value
        is Color -> value.value
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@JvmField
internal val URI_TO_STRING: Transform<Uri, String?> = { uri -> uri.toString() }

@JvmField
internal val STRING_TO_URI: Transform<String, Uri?> = { value -> Uri.parse(value) }

@JvmField
internal val ANY_TO_URI: Transform<Any, Uri?> = { value ->
    when(value) {
        is String -> Uri.parse(value)
        is Url -> Uri.parse(value.value)
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@JvmField
internal val ANY_TO_BOOLEAN: Transform<Any, Boolean?> = { value ->
    when (value) {
        is Boolean -> value
        is Number -> value.toBoolean()
        else -> null
    } ?: throw ClassCastException("Received value of wrong type")
}

@JvmField
internal val NUMBER_TO_DOUBLE: Transform<Number, Double?> = { n: Number -> n.toDouble() }

@JvmField
internal val NUMBER_TO_INT: Transform<Number, Long?> = { n: Number -> n.toLong() }

@JvmField
internal val JSON_TO_ORG_OBJECT: Transform<Any, JSONObject?> = { value ->
    when (value) {
        is JsonObject -> value.asOrgJsonObject()
        is JSONObject -> value
        else -> throw ClassCastException("Expected a JSON object")
    }
}

@JvmField
internal val JSON_TO_ORG_ARRAY: Transform<Any, JSONArray?> = { value ->
    when (value) {
        is JsonArray -> value.asOrgJsonArray()
        is JSONArray -> value
        else -> throw ClassCastException("Expected a JSON array")
    }
}

private fun Number.toBoolean(): Boolean? {
    return when(this) {
        is Double -> this.toBoolean()
        else -> toInt().toBoolean()
    }
}

private fun Int.toBoolean(): Boolean? {
    return when(this) {
        0 -> false
        1 -> true
        else -> null
    }
}

private fun Double.toBoolean(): Boolean? {
    return when(this) {
        0.0 -> false
        1.0 -> true
        else -> null
    }
}
