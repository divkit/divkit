@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.json

import android.net.Uri
import com.yandex.div.evaluable.types.Color
import java.lang.ClassCastException

typealias Converter<T, R> = (T) -> R

@PublishedApi
internal inline fun <T, R> Converter<T, R?>.tryConvert(value: T): R? {
    return try {
        this(value)
    } catch (e: Exception) {
        null
    }
}

val BOOLEAN_TO_INT: Converter<Boolean, Int> = { value -> if (value) 1 else 0 }

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

val NUMBER_TO_BOOLEAN: Converter<Number, Boolean?> = { n -> n.toBoolean() }
val NUMBER_TO_DOUBLE: Converter<Number, Double> = { n: Number -> n.toDouble() }
val NUMBER_TO_INT: Converter<Number, Int> = { n: Number -> n.toInt() }

fun Number.toBoolean(): Boolean? {
    return when (toInt()) {
        0 -> false
        1 -> true
        else -> null
    }
}

fun Int.toBoolean(): Boolean {
    return when (this) {
        0 -> false
        1 -> true
        else -> throw IllegalArgumentException("Unable to convert $this to boolean")
    }
}