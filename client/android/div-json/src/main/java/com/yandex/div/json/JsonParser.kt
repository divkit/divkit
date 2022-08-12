@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.yandex.div.json

import com.yandex.div.core.util.Assert
import com.yandex.div.json.expressions.ConstantExpressionsList
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.expressions.MutableExpressionsList
import com.yandex.div.util.whenNotEmpty
import org.json.JSONArray
import org.json.JSONObject

fun <T : Any> JSONObject.read(
    key: String,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): T {
    val value = optSafe(key) ?: throw missingValue(this, key)
    val result = value as? T ?: throw typeMismatch(this, key, value)

    return if (validator.isValid(result)) {
        result
    } else {
        throw invalidValue(this, key, result)
    }
}

inline fun <reified R, reified T : Any> JSONObject.read(
    key: String,
    converter: Converter<R, T?>,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
    ): T {
    val value = optSafe(key) ?: throw missingValue(this, key)
    val intermediate = value as? R ?: throw typeMismatch(this, key, value)

    val result = converter.tryConvert(intermediate)
        ?: throw invalidValue(this, key, intermediate)

    return if (validator.isValid(result)) {
        result
    } else {
        throw invalidValue(this, key, result)
    }
}

fun JSONObject.read(
    key: String,
    logger: ParsingErrorLogger,
): String {
    return optString(key) ?: throw missingValue(this, key)
}

fun <T : JSONSerializable> JSONObject.read(
    key: String,
    creator: Creator<JSONObject, T>,
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): T {
    val json = optJSONObject(key) ?: throw missingValue(this, key)
    val result = try {
        creator(env, json)
    } catch (e: ParsingException) {
        throw dependencyFailed(this, key, e)
    }

    return result
}

fun <T : Any> JSONObject.readOptional(
    key: String,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): T? {
    val value = optSafe(key) ?: return null
    val result = value as? T
    if (result == null) {
        logger.logError(typeMismatch(this, key, value))
        return null
    }

    return if (validator.isValid(result)) {
        result
    } else {
        logger.logError(invalidValue(this, key, result))
        null
    }
}

fun <R, T : Any> JSONObject.readOptional(
    key: String,
    converter: Converter<R, T?>,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): T? {
    val value = optSafe(key) ?: return null
    val intermediate = value as? R
    if (intermediate == null) {
        logger.logError(typeMismatch(this, key, value))
        return null
    }

    val result = converter.tryConvert(intermediate)
    if (result == null) {
        logger.logError(invalidValue(this, key, intermediate))
        return null
    }

    return if (validator.isValid(result)) {
        result
    } else {
        logger.logError(invalidValue(this, key, result))
        null
    }
}

fun <T : JSONSerializable> JSONObject.readOptional(
    key: String,
    creator: Creator<JSONObject, T>,
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): T? {
    val json = optJSONObject(key) ?: return null
    return creator.tryCreate(env, json, logger)
}

fun <T : Any> JSONObject.readList(
    key: String,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): List<T> {
    return getList(key, validator, logger) { jsonArray, i ->
        val item = jsonArray.optSafe(i) as? T ?: return@getList null
        item.takeIf { itemValidator.isValid(it) }.onNull {
            logger.logError(invalidValue(jsonArray, key, i, item))
        }
    }
}

fun <R, T : Any> JSONObject.readList(
    key: String,
    converter: Converter<R, T?>,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): List<T> {
    return getList(key, validator, logger) { jsonArray, i ->
        val rawItem = jsonArray.optSafe(i) as? R ?: return@getList null
        val item = converter.tryConvert(rawItem).onNull {
            logger.logError(invalidValue(this, key, rawItem))
        } ?: return@getList null
        item.takeIf { itemValidator.isValid(it) }.onNull {
            logger.logError(invalidValue(jsonArray, key, i, item))
        }
    }
}

@JvmName("readSerializableList")
fun <T : JSONSerializable> JSONObject.readList(
    key: String,
    creator: Creator<JSONObject, T>,
    validator: ListValidator<T>,
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): List<T> {
    return getList(key, validator, logger) { jsonArray, i ->
        val json = jsonArray.optJSONObject(i) ?: return@getList null
        val item = creator.tryCreate(env, json, logger) ?: return@getList null
        item
    }
}

fun <T : Any> JSONObject.readStrictList(
    key: String,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger
): List<T> {
    return getList(key, validator, logger) { jsonArray, i ->
        val rawItem = jsonArray.optSafe(i) ?: throw missingValue(jsonArray, key, i)
        val item = rawItem as? T ?: throw typeMismatch(jsonArray, key, i, rawItem)
        item.takeIf { itemValidator.isValid(it) } ?: throw invalidValue(jsonArray, key, i, item)
    }
}

fun <R, T : Any> JSONObject.readStrictList(
    key: String,
    converter: Converter<R, T?>,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger
): List<T> {
    return getList(key, validator, logger) { jsonArray, i ->
        val rawItem = jsonArray.optSafe(i) ?: throw missingValue(jsonArray, key, i)
        val intermediateItem = rawItem as? R ?: throw typeMismatch(jsonArray, key, i, rawItem)
        val item = converter.tryConvert(intermediateItem) ?: throw invalidValue(
            jsonArray,
            key,
            i,
            intermediateItem
        )
        item.takeIf { itemValidator.isValid(it) } ?: throw invalidValue(jsonArray, key, i, item)
    }
}

@JvmName("readStrictSerializableList")
fun <T : JSONSerializable> JSONObject.readStrictList(
    key: String,
    creator: Creator<JSONObject, T>,
    validator: ListValidator<T>,
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): List<T> {
    return getList(key, validator, logger) { jsonArray, i ->
        val json = jsonArray.optJSONObject(i) ?: throw missingValue(jsonArray, key, i)
        val item = try {
            creator(env, json)
        } catch (e: ParsingException) {
            throw dependencyFailed(jsonArray, key, i, e)
        }
        item
    }
}

fun <T : Any> JSONObject.readOptionalList(
    key: String,
    validator: ListValidator<T>,
    itemValidator: ValueValidator<T>,
    logger: ParsingErrorLogger,
): List<T>? {
    return optList(key, validator, logger) { jsonArray, i ->
        val item = jsonArray.optSafe(i) as? T ?: return@optList null
        item.takeIf { itemValidator.isValid(it) }.onNull {
            logger.logError(invalidValue(jsonArray, key, i, item))
        }
    }
}

fun <R, T : Any> JSONObject.readOptionalList(
    key: String,
    converter: Converter<R, T?>,
    validator: ListValidator<T>,
    itemValidator: ValueValidator<T>,
    logger: ParsingErrorLogger
): List<T>? {
    return optList(key, validator, logger) { jsonArray, i ->
        val rawItem = jsonArray.optSafe(i) as? R ?: return@optList null
        val item = converter.tryConvert(rawItem).onNull {
            logger.logError(invalidValue(this, key, rawItem))
        } ?: return@optList null
        item.takeIf { itemValidator.isValid(it) }.onNull {
            logger.logError(invalidValue(jsonArray, key, i, item))
        }
    }
}

@JvmName("readOptionalSerializableList")
fun <T : JSONSerializable> JSONObject.readOptionalList(
    key: String,
    creator: Creator<JSONObject, T>,
    validator: ListValidator<T>,
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): List<T>? {
    return optList(key, validator, logger) { jsonArray, i ->
        val json = jsonArray.optJSONObject(i) ?: return@optList null
        val item = creator.tryCreate(env, json, logger) ?: return@optList null
        item.takeIf { itemValidator.isValid(it) }.onNull {
            logger.logError(invalidValue(jsonArray, key, i, item))
        }
    }
}

fun <T : Any> JSONObject.write(key: String, value: T?, converter: Converter<T, Any> = { it }) {
    if (value != null) {
        put(key, converter(value))
    }
}

inline fun <reified T : JSONSerializable> JSONObject.write(key: String, value: T?) {
    if (value != null) {
        put(key, value.writeToJSON())
    }
}

fun <T : Any> JSONObject.write(key: String, value: List<T>?) {
    value.whenNotEmpty { list ->
        val item = list.first()
        if (item is JSONSerializable) {
            put(key, (list as List<JSONSerializable>).toJsonArray())
        } else {
            put(key, JSONArray(list))
        }
    }
}

fun <T : Any> JSONObject.write(key: String, value: List<T>?, converter: Converter<T, Any>) {
    value.whenNotEmpty { list ->
        val item = list.first()
        if (item is JSONSerializable) {
            put(key, (list as List<JSONSerializable>).toJsonArray())
        } else {
            put(key, JSONArray(list.map { converter(it) }))
        }
    }
}

fun <T : Any> JSONObject.writeExpression(
    key: String,
    value: Expression<T>?,
) = this.writeExpression(
        key, value, converter = { it }
)

fun <T : Any, R> JSONObject.writeExpression(
    key: String,
    value: Expression<T>?,
    converter: Converter<T, R>,
) {
    if (value == null) {
        return
    }

    val rawValue = value.rawValue
    val needsConversion = !Expression.mayBeExpression(rawValue)

    if (needsConversion) {
        put(key, converter(rawValue as T))
    } else {
        put(key, rawValue)
    }
}

fun <T : Any> JSONObject.writeExpressionsList(
    key: String,
    value: ExpressionsList<T>?) = writeExpressionsList(key, value, converter = { it })

fun <T : Any, R> JSONObject.writeExpressionsList(
    key: String,
    value: ExpressionsList<T>?,
    converter: Converter<T, R>,
) {
    when (value) {
        null -> {
            return
        }
        is MutableExpressionsList -> {
            val rawExpressions = value.expressionsList
            if (rawExpressions.isEmpty()) {
                return
            }

            put(key, JSONArray(rawExpressions.map { it.rawValue }))
        }
        is ConstantExpressionsList -> {
            put(key, JSONArray(value.evaluate(ExpressionResolver.EMPTY).map { converter(it) }))
        }
        else -> {
            Assert.fail("Unknown list type: $value")
        }
    }
}
