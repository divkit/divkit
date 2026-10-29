package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonArray as KtxArray
import kotlinx.serialization.json.JsonElement as KtxElement

/**
 * [JsonArray] backed by [kotlinx.serialization.json.JsonArray].
 *
 * `get*` methods throw [RuntimeException] if a value is missing or has an unexpected type,
 * `get*` with a fallback throw only on unexpected type,
 * `opt*` methods never throw.
 */
@Suppress("TooManyFunctions")
@ExperimentalApi
internal class KtxJsonArray(
    private val source: KtxArray
) : JsonArray {

    override val size: Int
        get() = source.size

    override fun isEmpty(): Boolean = source.isEmpty()

    override fun contains(element: Any?): Boolean = source.any { it.asJsonValue() == element }

    override fun containsAll(elements: Collection<Any?>): Boolean = elements.all { contains(it) }

    override fun indexOf(element: Any?): Int = source.indexOfFirst { it.asJsonValue() == element }

    override fun iterator(): Iterator<Any?> = source.asSequence().map { it.asJsonValue() }.iterator()

    override fun length(): Int = source.size

    override fun get(index: Int): Any = opt(index) ?: throw missingValue(index)

    override fun opt(index: Int): Any? = source.getOrNull(index).asJsonValue()

    override fun getInt(index: Int): Int = requireValue(index) { it.asInt() }

    override fun getInt(index: Int, fallback: Int): Int = requireValue(index, fallback) { it.asInt() }

    override fun optInt(index: Int): Int? = opt(index).asInt()

    override fun optInt(index: Int, fallback: Int): Int = optInt(index) ?: fallback

    override fun getLong(index: Int): Long = requireValue(index) { it.asLong() }

    override fun getLong(index: Int, fallback: Long): Long = requireValue(index, fallback) { it.asLong() }

    override fun optLong(index: Int): Long? = opt(index).asLong()

    override fun optLong(index: Int, fallback: Long): Long = optLong(index) ?: fallback

    override fun getFloat(index: Int): Float = requireValue(index) { it.asFloat() }

    override fun getFloat(index: Int, fallback: Float): Float = requireValue(index, fallback) { it.asFloat() }

    override fun optFloat(index: Int): Float? = opt(index).asFloat()

    override fun optFloat(index: Int, fallback: Float): Float = optFloat(index) ?: fallback

    override fun getDouble(index: Int): Double = requireValue(index) { it.asDouble() }

    override fun getDouble(index: Int, fallback: Double): Double = requireValue(index, fallback) { it.asDouble() }

    override fun optDouble(index: Int): Double? = opt(index).asDouble()

    override fun optDouble(index: Int, fallback: Double): Double = optDouble(index) ?: fallback

    override fun getBoolean(index: Int): Boolean = requireValue(index) { it.asBoolean() }

    override fun getBoolean(index: Int, fallback: Boolean): Boolean = requireValue(index, fallback) { it.asBoolean() }

    override fun optBoolean(index: Int): Boolean? = opt(index).asBoolean()

    override fun optBoolean(index: Int, fallback: Boolean): Boolean = optBoolean(index) ?: fallback

    override fun getString(index: Int): String = requireValue(index) { it.asString() }

    override fun getString(index: Int, fallback: String): String = requireValue(index, fallback) { it.asString() }

    override fun optString(index: Int): String? = opt(index).asString()

    override fun optString(index: Int, fallback: String): String = optString(index) ?: fallback

    override fun getObject(index: Int): JsonObject = requireValue(index) { it as? JsonObject }

    override fun optObject(index: Int): JsonObject? = opt(index) as? JsonObject

    override fun getArray(index: Int): JsonArray = requireValue(index) { it as? JsonArray }

    override fun optArray(index: Int): JsonArray? = opt(index) as? JsonArray

    override fun modify(modify: JsonArray.Builder.() -> Unit): JsonArray {
        return KtxJsonArrayBuilder(
            source,
            isShared = false,
        ).apply(modify).build()
    }

    override fun asKtxJsonArray(): KtxArray = source

    override fun equals(other: Any?): Boolean = other is KtxJsonArray && source === other.source

    override fun hashCode(): Int = source.hashCode()

    override fun toString(): String = source.toString()
}

@Suppress("TooManyFunctions")
internal class KtxJsonArrayBuilder(
    source: KtxArray,
    val isShared: Boolean,
) : JsonArray.Builder {

    private val data = source.toMutableList()

    override fun add(value: Int) = addImpl(JsonPrimitive(value))

    override fun add(value: Long) = addImpl(JsonPrimitive(value))

    override fun add(value: Float) = addImpl(JsonPrimitive(requireFinite(value)))

    override fun add(value: Double) = addImpl(JsonPrimitive(requireFinite(value)))

    override fun add(value: Boolean) = addImpl(JsonPrimitive(value))

    override fun add(value: String) = addImpl(JsonPrimitive(value))

    override fun add(value: JsonObject) = addImpl(value.asKtxJsonObject())

    override fun add(value: JsonArray) = addImpl(value.asKtxJsonArray())

    override fun add(value: Any?) {
        if (value == null) {
            addImpl(JsonNull)
        } else {
            dispatchAdd(value)
        }
    }

    private fun dispatchAdd(value: Any) = when (value) {
        is String -> add(value)
        is Boolean -> add(value)
        is Int -> add(value)
        is Long -> add(value)
        is Float -> add(value)
        is Double -> add(value)
        is JsonObject -> add(value)
        is JsonArray -> add(value)
        else -> throw unsupportedValue(value)
    }

    private fun addImpl(value: KtxElement) {
        data.add(value)
    }

    override fun put(index: Int, value: Int) = putImpl(index, JsonPrimitive(value))

    override fun put(index: Int, value: Long) = putImpl(index, JsonPrimitive(value))

    override fun put(index: Int, value: Float) = putImpl(index, JsonPrimitive(requireFinite(value)))

    override fun put(index: Int, value: Double) = putImpl(index, JsonPrimitive(requireFinite(value)))

    override fun put(index: Int, value: Boolean) = putImpl(index, JsonPrimitive(value))

    override fun put(index: Int, value: String) = putImpl(index, JsonPrimitive(value))

    override fun put(index: Int, value: JsonObject) = putImpl(index, value.asKtxJsonObject())

    override fun put(index: Int, value: JsonArray) = putImpl(index, value.asKtxJsonArray())

    override fun put(index: Int, value: Any?) {
        if (value == null) {
            putImpl(index, JsonNull)
        } else {
            dispatchPut(index, value)
        }
    }

    private fun dispatchPut(index: Int, value: Any) = when (value) {
        is String -> put(index, value)
        is Boolean -> put(index, value)
        is Int -> put(index, value)
        is Long -> put(index, value)
        is Float -> put(index, value)
        is Double -> put(index, value)
        is JsonObject -> put(index, value)
        is JsonArray -> put(index, value)
        else -> throw unsupportedValue(value)
    }

    private fun putImpl(index: Int, value: KtxElement) {
        require(index >= 0) {
            "Index must be non-negative: $index"
        }

        while (data.size < index) {
            data.add(JsonNull)
        }

        if (index == data.size) {
            data.add(value)
        } else {
            data[index] = value
        }
    }

    override fun remove(index: Int): Any? {
        if (index !in data.indices) return null
        return data.removeAt(index).asJsonValue()
    }

    override fun build(): JsonArray {
        val result = if (isShared) data.toList() else data
        return KtxJsonArray(KtxArray(result))
    }
}
