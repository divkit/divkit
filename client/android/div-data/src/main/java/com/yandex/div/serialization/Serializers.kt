package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.internal.parser.Converter
import com.yandex.div.json.ParsingException
import org.json.JSONArray
import org.json.JSONObject

@ExperimentalApi
fun <T> Serializer<T, JSONObject>.serialize(context: ParsingContext, value: T?): JSONObject? {
    if (value == null) {
        return null
    }

    return try {
        serialize(context, value)
    } catch (e: ParsingException) {
        context.logger.logError(e)
        null
    }
}

@ExperimentalApi
fun <T> Serializer<T, JSONObject>.serialize(context: ParsingContext, list: List<T>?): JSONArray? {
    if (list == null) {
        return null
    }

    return try {
        val result = JSONArray()
        for (index in list.indices) {
            result.put(serialize(context, list[index]))
        }
        return result
    } catch (e: ParsingException) {
        context.logger.logError(e)
        null
    }
}

@ExperimentalApi
internal inline fun <reified T> Serializer<T, JSONObject>.asConverter(context: ParsingContext): Converter<T, JSONObject> {
    return { value: T -> serialize(context, value) }
}
