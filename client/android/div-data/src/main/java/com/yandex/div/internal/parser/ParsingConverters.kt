@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.internal.parser

import android.net.Uri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
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

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getCOLOR_INT_TO_STRING() = COLOR_INT_TO_STRING

@JvmField
val STRING_TO_COLOR_INT: Converter<Any?, Int?> = { value ->
    when (value) {
        is String -> Color.parse(value).value
        is Color -> value.value
        null -> null
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getSTRING_TO_COLOR_INT() = STRING_TO_COLOR_INT

@JvmField
val URI_TO_STRING: Converter<Uri, String> = { uri -> uri.toString() }

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getURI_TO_STRING() = URI_TO_STRING

@JvmField
val ANY_TO_URI: Converter<Any, Uri> = { value -> 
    when(value) {
        is String -> Uri.parse(value)
        is Url -> Uri.parse(value.value)
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getANY_TO_URI() = ANY_TO_URI

@JvmField
val ANY_TO_BOOLEAN: Converter<Any, Boolean?> = { value ->
    when (value) {
        is Boolean -> value
        is Number -> value.toBoolean()
        else -> throw ClassCastException("Received value of wrong type")
    }
}

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getANY_TO_BOOLEAN() = ANY_TO_BOOLEAN

@JvmField
val NUMBER_TO_DOUBLE: Converter<Number, Double> = { n: Number -> n.toDouble() }

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getNUMBER_TO_DOUBLE() = NUMBER_TO_DOUBLE

@JvmField
val NUMBER_TO_INT: Converter<Number, Long> = { n: Number -> n.toLong() }

@Suppress("FunctionName", "DeprecatedCallableAddReplaceWith")
@Deprecated("Do not use internal API")
fun getNUMBER_TO_INT() = NUMBER_TO_INT
