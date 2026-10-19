@file:Suppress("unused")

package com.yandex.div.parser.json

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.parser.json.util.addValue
import com.yandex.div.parser.json.util.putValue
import com.yandex.div.serialization.Deserializer
import com.yandex.div.serialization.ParsingContext
import com.yandex.div.serialization.Serializer

@InternalApi
internal object JsonPropertyParser {

    @JvmStatic
    fun readString(context: ParsingContext, jsonObject: JsonObject, key: String): String {
        val value = jsonObject.opt(key) ?: throw missingValue(jsonObject, key)
        return value as? String ?: throw typeMismatch(jsonObject, key, value)
    }

    @JvmStatic
    fun readOptionalString(context: ParsingContext, jsonObject: JsonObject, key: String): String? {
        val value = jsonObject.opt(key) ?: return null
        if (value !is String) {
            context.logger.logError(typeMismatch(jsonObject, key, value))
            return null
        }
        return value
    }

    @JvmStatic
    fun <V : Any> read(context: ParsingContext, jsonObject: JsonObject, key: String): V {
        return read(context, jsonObject, key, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> read(context: ParsingContext, jsonObject: JsonObject, key: String, transform: Transform<R, V?>): V {
        return read(context, jsonObject, key, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> read(context: ParsingContext, jsonObject: JsonObject, key: String, validator: ValueValidator<V>): V {
        return read(context, jsonObject, key, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> read(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): V {
        val intermediate = jsonObject.opt(key) ?: throw missingValue(jsonObject, key)
        val result = try {
            transform(intermediate.cast())
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, intermediate)
        } catch (e: Exception) {
            throw invalidValue(jsonObject, key, intermediate, e)
        } ?: throw invalidValue(jsonObject, key, intermediate)

        try {
            if (!validator.isValid(result)) {
                throw invalidValue(jsonObject, key, result)
            }
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, result)
        }
        return result
    }

    @JvmStatic
    fun <V : Any> read(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): V {
        val json = jsonObject.optObject(key) ?: throw missingValue(jsonObject, key)
        return try {
            deserializer.value.deserialize(context, json)
        } catch (e: Exception) {
            throw dependencyFailed(jsonObject, key, e)
        }
    }

    @JvmStatic
    fun <V : Any> readOptional(context: ParsingContext, jsonObject: JsonObject, key: String): V? {
        return readOptional(context, jsonObject, key, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptional(context: ParsingContext, jsonObject: JsonObject, key: String, transform: Transform<R, V?>): V? {
        return readOptional(context, jsonObject, key, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptional(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        validator: ValueValidator<V>,
    ): V? {
        return readOptional(context, jsonObject, key, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> readOptional(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): V? {
        val intermediate = jsonObject.opt(key) ?: return null
        val result = try {
            transform(intermediate.cast())
        } catch (e: ClassCastException) {
            context.logger.logError(typeMismatch(jsonObject, key, intermediate))
            return null
        } catch (e: Exception) {
            context.logger.logError(invalidValue(jsonObject, key, intermediate, e))
            return null
        }
        if (result == null) {
            context.logger.logError(invalidValue(jsonObject, key, intermediate))
            return null
        }

        try {
            if (!validator.isValid(result)) {
                context.logger.logError(invalidValue(jsonObject, key, intermediate))
                return null
            }
        } catch (e: ClassCastException) {
            context.logger.logError(typeMismatch(jsonObject, key, intermediate))
            return null
        }
        return result
    }

    @JvmStatic
    fun <V : Any> readOptional(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): V? {
        val json = jsonObject.optObject(key) ?: return null
        return try {
            deserializer.value.deserialize(context, json)
        } catch (e: Exception) {
            context.logger.logError(dependencyFailed(jsonObject, key, e))
            null
        }
    }

    @JvmStatic
    fun <R, V : Any> readList(context: ParsingContext, jsonObject: JsonObject, key: String, transform: Transform<R, V?>): List<V> {
        return readList(context, jsonObject, key, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun readList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        listValidator: ListValidator<String>,
    ): List<String> {
        return readList(context, jsonObject, key, doNotConvert<String>(), listValidator, alwaysValidString())
    }

    @JvmStatic
    fun <R, V : Any> readList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): List<V> {
        return readList(context, jsonObject, key, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): List<V> {
        val array = jsonObject.optArray(key) ?: throw missingValue(jsonObject, key)
        val length = array.length()
        if (length == 0) {
            // Unlike a non-empty list, a rejected empty list is only logged.
            validateList(context, jsonObject, key, emptyList(), listValidator)
            return emptyList()
        }

        val list = readItems(context, array, key, length, transform, itemValidator)
        try {
            if (!listValidator.isValid(list)) {
                throw invalidValue(jsonObject, key, list)
            }
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, list)
        }
        return list
    }

    @JvmStatic
    fun <V : Any> readList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): List<V> {
        val array = jsonObject.optArray(key) ?: throw missingValue(jsonObject, key)
        val length = array.length()
        if (length == 0) {
            return emptyList()
        }
        return readObjects(context, array, key, length, deserializer)
    }

    @JvmStatic
    fun <V : Any> readList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
        listValidator: ListValidator<V>,
    ): List<V> {
        val array = jsonObject.optArray(key) ?: throw missingValue(jsonObject, key)
        val length = array.length()
        if (length == 0) {
            // Unlike a non-empty list, a rejected empty list is only logged.
            validateList(context, jsonObject, key, emptyList(), listValidator)
            return emptyList()
        }

        val list = readObjects(context, array, key, length, deserializer)
        try {
            if (!listValidator.isValid(list)) {
                throw invalidValue(jsonObject, key, list)
            }
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, list)
        }
        return list
    }

    @JvmStatic
    fun <R, V : Any> readOptionalList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
    ): List<V>? {
        return readOptionalList(context, jsonObject, key, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): List<V>? {
        return readOptionalList(context, jsonObject, key, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): List<V>? {
        val array = jsonObject.optArray(key) ?: return null
        val length = array.length()
        if (length == 0) {
            return if (validateList(context, jsonObject, key, emptyList(), listValidator)) emptyList() else null
        }

        val list = readItems(context, array, key, length, transform, itemValidator)
        return if (validateList(context, jsonObject, key, list, listValidator)) list else null
    }

    @JvmStatic
    fun <V : Any> readOptionalList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): List<V>? {
        val array = jsonObject.optArray(key) ?: return null
        val length = array.length()
        if (length == 0) {
            return emptyList()
        }
        return readObjects(context, array, key, length, deserializer)
    }

    @JvmStatic
    fun <V : Any> readOptionalList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
        listValidator: ListValidator<V>,
    ): List<V>? {
        val array = jsonObject.optArray(key) ?: return null
        val length = array.length()
        if (length == 0) {
            return if (validateList(context, jsonObject, key, emptyList(), listValidator)) emptyList() else null
        }

        val list = readObjects(context, array, key, length, deserializer)
        return if (validateList(context, jsonObject, key, list, listValidator)) list else null
    }

    /**
     * Converts and validates items of [array]; failed items are logged and skipped,
     * JSON nulls and items converted to null are skipped silently.
     */
    private fun <R, V : Any> readItems(
        context: ParsingContext,
        array: JsonArray,
        key: String,
        length: Int,
        transform: Transform<R, V?>,
        itemValidator: ValueValidator<V>,
    ): MutableList<V> {
        val list = ArrayList<V>(length)
        for (i in 0 until length) {
            val intermediate = array.opt(i) ?: continue
            val item = try {
                transform(intermediate.cast())
            } catch (e: ClassCastException) {
                context.logger.logError(typeMismatch(array, key, i, intermediate))
                continue
            } catch (e: Exception) {
                context.logger.logError(invalidValue(array, key, i, intermediate, e))
                continue
            } ?: continue

            try {
                if (!itemValidator.isValid(item)) {
                    context.logger.logError(invalidValue(array, key, i, item))
                    continue
                }
            } catch (e: ClassCastException) {
                context.logger.logError(typeMismatch(array, key, i, item))
                continue
            }
            list.add(item)
        }
        return list
    }

    /** Deserializes object items of [array]; failed items are logged and skipped, non-object items are skipped silently. */
    private fun <V : Any> readObjects(
        context: ParsingContext,
        array: JsonArray,
        key: String,
        length: Int,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): MutableList<V> {
        val parser = deserializer.value
        val list = ArrayList<V>(length)
        for (i in 0 until length) {
            val json = array.optObject(i) ?: continue
            val item = try {
                parser.deserialize(context, json)
            } catch (e: Exception) {
                context.logger.logError(dependencyFailed(array, key, i, e))
                continue
            }
            list.add(item)
        }
        return list
    }


    @JvmStatic
    fun <V : Any> write(context: ParsingContext, builder: JsonObject.Builder, key: String, value: V?) {
        write(context, builder, key, value, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> write(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        value: V?,
        transform: Transform<V, R>,
    ) {
        if (value == null) return
        try {
            builder.putValue(key, transform(value))
        } catch (e: RuntimeException) {
            context.logger.logError(e)
        }
    }

    @JvmStatic
    fun <V : Any> write(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        value: V?,
        serializer: Lazy<Serializer<JsonObject, V>>,
    ) {
        if (value == null) return
        try {
            builder.put(key, serializer.value.serialize(context, value))
        } catch (e: RuntimeException) {
            context.logger.logError(e)
        }
    }

    @JvmStatic
    fun <V : Any> writeList(context: ParsingContext, builder: JsonObject.Builder, key: String, list: List<V>?) {
        writeList(context, builder, key, list, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeList(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        list: List<V>?,
        transform: Transform<V, R>,
    ) {
        if (list == null) return
        val array = JsonArray.builder()
        for (i in 0 until list.size) {
            array.addValue(transform(list[i]))
        }
        try {
            builder.put(key, array.build())
        } catch (e: RuntimeException) {
            context.logger.logError(e)
        }
    }

    @JvmStatic
    fun <V : Any> writeList(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        list: List<V>?,
        serializer: Lazy<Serializer<JsonObject, V>>,
    ) {
        if (list == null) return
        val itemSerializer = serializer.value
        val array = JsonArray.builder()
        for (i in 0 until list.size) {
            array.add(itemSerializer.serialize(context, list[i]))
        }
        try {
            builder.put(key, array.build())
        } catch (e: RuntimeException) {
            context.logger.logError(e)
        }
    }
}
