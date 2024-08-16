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

val COLOR_INT_TO_STRING: Converter<Int, String> = { value -> Color(value).toString() }

val STRING_TO_COLOR_INT: Converter<Any?, Int?> = { value ->
    when (value) {
        is String -> Color.parse(value).value
        is Color -> value.value
        null -> null
        else -> throw ClassCastException("Received value of wrong type")
    }
}

val URI_TO_STRING: Converter<Uri, String> = { uri -> uri.toString() }

val STRING_TO_URI: Converter<String, Uri> = { value -> Uri.parse(value) }

val ANY_TO_BOOLEAN: Converter<Any, Boolean?> = { value ->
    when (value) {
        is Number -> value.toBoolean()
        is Boolean -> value
        else -> throw ClassCastException("Received value of wrong type")
    }
}
val NUMBER_TO_DOUBLE: Converter<Number, Double> = { n: Number -> n.toDouble() }

val NUMBER_TO_INT: Converter<Number, Long> = { n: Number -> n.toLong() }
