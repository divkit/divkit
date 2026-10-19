@file:JvmName("TypeHelpers")

package com.yandex.div.parser.json

import android.graphics.Color
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

internal typealias TypeHelper<T> = com.yandex.div.internal.parser.TypeHelper<T>

@JvmField
internal val TYPE_HELPER_BOOLEAN = object : TypeHelper<Boolean> {
    override val typeDefault = false
    override fun isTypeValid(value: Any) = value is Boolean
}

@JvmField
internal val TYPE_HELPER_INT = object : TypeHelper<Long> {
    override val typeDefault = 0L
    override fun isTypeValid(value: Any) = value is Long
}

@JvmField
internal val TYPE_HELPER_STRING = object : TypeHelper<String> {
    override val typeDefault = ""
    override fun isTypeValid(value: Any) = value is String
}

@JvmField
internal val TYPE_HELPER_DOUBLE = object : TypeHelper<Double> {
    override val typeDefault = 0f.toDouble()
    override fun isTypeValid(value: Any) = value is Double
}

@JvmField
internal val TYPE_HELPER_URI = object : TypeHelper<Uri> {
    override val typeDefault = Uri.EMPTY
    override fun isTypeValid(value: Any) = value is Uri
}

@JvmField
internal val TYPE_HELPER_COLOR = object : TypeHelper<Int> {
    override val typeDefault = Color.BLACK
    override fun isTypeValid(value: Any) = value is Int
}

@JvmField
internal val TYPE_HELPER_JSON_ARRAY = object : TypeHelper<JSONArray> {
    override val typeDefault = JSONArray()
    override fun isTypeValid(value: Any) = value is JSONArray
}

@JvmField
internal val TYPE_HELPER_DICT = object : TypeHelper<JSONObject> {
    override val typeDefault = JSONObject()
    override fun isTypeValid(value: Any) = value is JSONObject
}
