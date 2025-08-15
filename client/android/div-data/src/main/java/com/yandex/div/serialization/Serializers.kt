package com.yandex.div.serialization

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.Creator
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingException
import org.json.JSONArray
import org.json.JSONObject

@ExperimentalApi
fun <V> Serializer<JSONObject, V>.serialize(context: ParsingContext, value: V?): JSONObject? {
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
fun <V> Serializer<JSONObject, V>.serialize(context: ParsingContext, list: List<V>?): JSONArray? {
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
internal inline fun <reified V> Serializer<JSONObject, V>.asConverter(context: ParsingContext): Converter<V, JSONObject> {
    return { value: V -> serialize(context, value) }
}

@Suppress("DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")
internal inline fun <reified V> Serializer<JSONObject, V>.asCreator(): Creator<V, JSONObject> {
    return { env: ParsingEnvironment, value: V -> serialize(env, value) }
}
