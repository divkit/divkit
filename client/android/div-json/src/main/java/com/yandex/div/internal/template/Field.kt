@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.internal.template

import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.internal.parser.ListValidator
import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingException
import com.yandex.div.json.dependencyFailed
import com.yandex.div.json.invalidValue
import com.yandex.div.json.missingValue
import org.json.JSONObject

typealias Reader<T> = (String, JSONObject, env: ParsingEnvironment) -> T

sealed class Field<T>(val overridable: Boolean) {

    object Null : Field<Any>(overridable = false)
    object Placeholder : Field<Any>(overridable = true)
    class Value<T>(overridable: Boolean, val value: T) : Field<T>(overridable)
    class Reference<T>(overridable: Boolean, val reference: String) : Field<T>(overridable)

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <T> nullField(overridable: Boolean): Field<T> {
            val nullField = if (overridable) Placeholder else Null
            return nullField as Field<T>
        }
    }
}

fun <T> Field<T>.resolve(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<T>
): T {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value<T> -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> throw missingValue(data, key)
    }
}

fun <T> Field<T>.resolveOptional(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<T?>
): T? {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value<T> -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> null
    }
}

fun <T> Field<out List<T>>.resolveList(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    validator: ListValidator<T> = ListValidator { true },
    reader: Reader<List<T>>
): List<T> {
    val result = when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> throw missingValue(data, key)
    }

    return if (validator.isValid(result)) {
        result
    } else {
        throw invalidValue(data, key, result)
    }
}

fun <T> Field<out List<T>>.resolveOptionalList(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    validator: ListValidator<T> = ListValidator { true },
    reader: Reader<List<T>?>
): List<T>? {
    val result = when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> null
    } ?: return null

    return if (validator.isValid(result)) {
        result
    } else {
        env.logger.logError(invalidValue(data, key, result))
        null
    }
}

fun <T : JSONSerializable> Field<out JsonTemplate<T>>.resolveTemplate(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<T>
): T {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value.resolveDependency(env, key, data)
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> throw missingValue(data, key)
    }
}

fun <T : JSONSerializable> Field<out JsonTemplate<T>>.resolveOptionalTemplate(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<T?>
): T? {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value.resolveOptionalDependency(env, data)
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> null
    }
}

fun <T : JSONSerializable> Field<out List<JsonTemplate<T>>>.resolveTemplateList(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    validator: ListValidator<T> = ListValidator { true },
    reader: Reader<List<T>>
): List<T> {
    val result = when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value.mapNotNull { it.resolveOptionalDependency(env, data) }
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> throw missingValue(data, key)
    }

    return if (validator.isValid(result)) {
        result
    } else {
        throw invalidValue(data, key, result)
    }
}

fun <T : JSONSerializable> Field<out List<JsonTemplate<T>>>.resolveOptionalTemplateList(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    validator: ListValidator<T> = ListValidator { true },
    reader: Reader<List<T>?>
): List<T>? {
    val result = when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value.mapNotNull { it.resolveOptionalDependency(env, data) }
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> null
    } ?: return null

    return if (validator.isValid(result)) {
        result
    } else {
        env.logger.logError(invalidValue(data, key, result))
        null
    }
}

@PublishedApi
internal fun <T : JSONSerializable> JsonTemplate<T>.resolveDependency(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject
): T {
    return try {
        resolve(env, data)
    } catch (e: ParsingException) {
        throw dependencyFailed(data, key, e)
    }
}

@PublishedApi
internal fun <T : JSONSerializable> JsonTemplate<T>.resolveOptionalDependency(
    env: ParsingEnvironment,
    data: JSONObject
): T? {
    return try {
        resolve(env, data)
    } catch (e: ParsingException) {
        env.logger.logError(e)
        null
    }
}

fun <T> Field<T>?.clone(overridable: Boolean): Field<T> {
    return when {
        this == null || this == Field.Null || this == Field.Placeholder -> Field.nullField(overridable)
        this is Field.Value -> Field.Value(overridable, value)
        this is Field.Reference -> Field.Reference(overridable, reference)
        else -> throw IllegalStateException("Unknown field type")
    }
}

fun <T: Any> Field<Expression<T>>.resolveExpression(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<Expression<T>>
): Expression<T> {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value<Expression<T>> -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> throw missingValue(data, key)
    }
}

fun <T: Any> Field<ExpressionList<T>>.resolveExpressionList(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<ExpressionList<T>>
): ExpressionList<T> {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> throw missingValue(data, key)
    }
}


fun <T: Any> Field<Expression<T>>.resolveOptionalExpression(
    env: ParsingEnvironment,
    key: String,
    data: JSONObject,
    reader: Reader<Expression<T>?>
): Expression<T>? {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value<Expression<T>> -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> null
    }
}

fun <T: Any> Field<ExpressionList<T>>.resolveOptionalExpressionList(
        env: ParsingEnvironment,
        key: String,
        data: JSONObject,
        reader: Reader<ExpressionList<T>?>
): ExpressionList<T>? {
    return when {
        overridable && data.has(key) -> reader.invoke(key, data, env)
        this is Field.Value -> value
        this is Field.Reference -> reader.invoke(reference, data, env)
        else -> null
    }
}
