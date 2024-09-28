@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.internal.parser

import android.net.Uri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.internal.util.toBoolean

typealias Converter<T, R> = (T) -> R

@PublishedApi
internal inline fun <T, R> Converter<T, R?>.tryConvert(value: T): R? {
    return try {
        this(value)
    } catch (e: Exception) {
        null
    }
}

@JvmField
val COLOR_INT_TO_STRING: Converter<Int, String> = { value -> Color(value).toString() }

@JvmField
val STRING_TO_COLOR_INT: Converter<Any?, Int?> = { value ->
    when (value) {
        is String -> Color.parse(value).value
        is Color -> value.value
        null -> null
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@JvmField
val URI_TO_STRING: Converter<Uri, String> = { uri -> uri.toString() }

@JvmField
val STRING_TO_URI: Converter<String, Uri> = { value -> Uri.parse(value) }

@JvmField
val ANY_TO_BOOLEAN: Converter<Any, Boolean?> = { value ->
    when (value) {
        is Boolean -> value
        is Number -> value.toBoolean()
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@JvmField
val NUMBER_TO_DOUBLE: Converter<Number, Double> = { n: Number -> n.toDouble() }

@JvmField
val NUMBER_TO_INT: Converter<Number, Long> = { n: Number -> n.toLong() }
