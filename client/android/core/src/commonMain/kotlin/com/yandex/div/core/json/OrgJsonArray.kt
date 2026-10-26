package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.JvmInline

/**
 * [JsonArray] backed by [org.json.JSONArray].
 *
 * `get*` methods throw [RuntimeException] if a value is missing or has an unexpected type,
 * `get*` with a fallback throw only on unexpected type,
 * `opt*` methods never throw.
 */
@Suppress("TooManyFunctions")
@ExperimentalApi
internal class OrgJsonArray(
    private val source: JSONArray
) : JsonArray {

    override val size: Int
        get() = source.length()

    override fun isEmpty(): Boolean = source.length() == 0

    override fun contains(element: Any?): Boolean = indexOf(element) >= 0

    override fun containsAll(elements: Collection<Any?>): Boolean = elements.all { contains(it) }

    override fun indexOf(element: Any?): Int = (0 until source.length()).firstOrNull { opt(it) == element } ?: -1

    override fun iterator(): Iterator<Any?> = (0 until source.length()).asSequence().map { opt(it) }.iterator()

    override fun length(): Int = source.length()

    override fun get(index: Int): Any = opt(index) ?: throw missingValue(index)

    override fun opt(index: Int): Any? = source.opt(index).asJsonValue()

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

    override fun modify(modify: JsonArray.Builder.() -> Unit): JsonArray  {
        return OrgJsonArrayBuilder(
            source,
            isShared = false,
        ).apply(modify).build()
    }

    override fun asOrgJsonArray(): JSONArray = source

    override fun asKtxJsonArray(): kotlinx.serialization.json.JsonArray = source.toKtxJsonArray()

    override fun equals(other: Any?): Boolean = other is OrgJsonArray && source === other.source

    override fun hashCode(): Int = source.hashCode()

    override fun toString(): String = source.toString()
}

@Suppress("TooManyFunctions")
private class OrgJsonArrayBuilder(
    source: JSONArray,
    val isShared: Boolean,
) : JsonArray.Builder {

    private val data = source.copy()

    override fun add(value: Int) = addImpl(value)

    override fun add(value: Long) = addImpl(value)

    override fun add(value: Float) = addImpl(requireFinite(value))

    override fun add(value: Double) = addImpl(requireFinite(value))

    override fun add(value: Boolean) = addImpl(value)

    override fun add(value: String) = addImpl(value)

    override fun add(value: JsonObject) = addImpl(value.asOrgJsonObject())

    override fun add(value: JsonArray) = addImpl(value.asOrgJsonArray())

    override fun add(value: Any?) {
        if (value == null) {
            addImpl(JSONObject.NULL)
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

    private fun addImpl(value: Any?) {
        try {
            data.put(value)
        } catch (e: JSONException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun put(index: Int, value: Int) = putImpl(index, value)

    override fun put(index: Int, value: Long) = putImpl(index, value)

    override fun put(index: Int, value: Float) = putImpl(index, requireFinite(value))

    override fun put(index: Int, value: Double) = putImpl(index, requireFinite(value))

    override fun put(index: Int, value: Boolean) = putImpl(index, value)

    override fun put(index: Int, value: String) = putImpl(index, value)

    override fun put(index: Int, value: JsonObject) = putImpl(index, value.asOrgJsonObject())

    override fun put(index: Int, value: JsonArray) = putImpl(index, value.asOrgJsonArray())

    override fun put(index: Int, value: Any?) {
        if (value == null) {
            putImpl(index, JSONObject.NULL)
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

    private fun putImpl(index: Int, value: Any?) {
        require(index >= 0) {
            "Index must be non-negative: $index"
        }

        try {
            data.put(index, value)
        } catch (e: JSONException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun remove(index: Int): Any? {
        return data.remove(index).asJsonValue()
    }

    override fun build(): JsonArray {
        val result = if (isShared) data.copy() else data
        return OrgJsonArray(result)
    }
}
