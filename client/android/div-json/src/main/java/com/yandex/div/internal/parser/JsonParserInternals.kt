package com.yandex.div.internal.parser

import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.invalidValue
import com.yandex.div.json.missingValue
import org.json.JSONArray
import org.json.JSONObject

typealias Creator<T, R> = (env: ParsingEnvironment, value: T) -> R

internal typealias ItemReader<T> = (JSONArray, Int) -> T

@PublishedApi
internal fun <T, R : JSONSerializable> Creator<T, R>.tryCreate(
    env: ParsingEnvironment,
    arg: T,
    logger: ParsingErrorLogger,
): R? {
    return try {
        this(env, arg)
    } catch (e: ParsingException) {
        logger.logError(e)
        return null
    }
}

@PublishedApi
internal fun JSONObject.optSafe(key: String): Any? {
    val result = opt(key)
    return if (result == JSONObject.NULL) null else result
}

@PublishedApi
internal fun JSONArray.optSafe(index: Int): Any? {
    val result = opt(index)
    return if (result == JSONObject.NULL) null else result
}

@PublishedApi
internal fun <T : Any> JSONObject.getList(
    key: String,
    validator: ListValidator<T>,
    logger: ParsingErrorLogger,
    itemReader: ItemReader<T?>
): List<T> {
    val jsonArray = optJSONArray(key) ?: throw missingValue(this, key)
    val length = jsonArray.length()
    val list: MutableList<T> = ArrayList(length)

    for (index in 0 until length) {
        val item = itemReader(jsonArray, index)
        if (item != null) list.add(item)
    }

    return if (validator.isValid(list)) {
        list
    } else {
        throw invalidValue(this, key, list)
    }
}

@PublishedApi
internal fun <T : Any> JSONObject.optList(
    key: String,
    validator: ListValidator<T>,
    logger: ParsingErrorLogger,
    itemReader: ItemReader<T?>
): List<T>? {
    val jsonArray = optJSONArray(key) ?: return null
    val length = jsonArray.length()
    val list: MutableList<T> = ArrayList(length)

    for (index in 0 until length) {
        val item = itemReader(jsonArray, index)
        if (item != null) list.add(item)
    }

    return if (validator.isValid(list)) {
        list
    } else {
        logger.logError(invalidValue(this, key, list))
        null
    }
}

@PublishedApi
internal fun <T : JSONSerializable> List<T>.toJsonArray(): JSONArray {
    val jsonArray = JSONArray()
    forEach {
        jsonArray.put(it.writeToJSON())
    }
    return jsonArray
}

@PublishedApi
internal inline fun <T> T.onNull(block: () -> Unit): T? {
    if (this == null) {
        block()
    }
    return this
}
