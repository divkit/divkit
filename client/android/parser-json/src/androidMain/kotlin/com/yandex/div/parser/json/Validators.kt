package com.yandex.div.parser.json

import com.yandex.div.core.json.JsonObject
import com.yandex.div.serialization.ParsingContext
import java.util.regex.Pattern

private val ALWAYS_VALID = ValueValidator<Any?> { true }

private val ALWAYS_VALID_STRING = ValueValidator<String> { _ -> true }

private val ALWAYS_VALID_LIST = ListValidator<Any?> { true }

private val IS_NOT_EMPTY = ValueValidator<String> { it.isNotEmpty() }

internal fun String.doesMatch(regex: String): Boolean = Pattern.matches(regex, this)

internal fun <T> alwaysValid(): ValueValidator<T> = ALWAYS_VALID.cast()

internal fun alwaysValidString(): ValueValidator<String> = ALWAYS_VALID_STRING

internal fun <T> alwaysValidList(): ListValidator<T> = ALWAYS_VALID_LIST.cast<ListValidator<T>>()

internal fun isNotEmpty(): ValueValidator<String> = IS_NOT_EMPTY

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
internal inline fun <T> Any?.cast(): T = this as T

@Suppress("UNCHECKED_CAST")
internal inline fun <reified R, reified T> ListValidator<R>.cast(): ListValidator<T> {
    return this as ListValidator<T>
}

internal fun <V> validateList(
    context: ParsingContext,
    jsonObject: JsonObject,
    key: String,
    list: List<V>,
    listValidator: ListValidator<V>,
): Boolean {
    try {
        if (listValidator.isValid(list)) {
            return true
        }
        context.logger.logError(invalidValue(jsonObject, key, list))
    } catch (_: ClassCastException) {
        context.logger.logError(typeMismatch(jsonObject, key, list))
    }
    return false
}
