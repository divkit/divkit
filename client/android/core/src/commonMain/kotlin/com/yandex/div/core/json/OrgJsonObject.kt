package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import org.json.JSONException
import org.json.JSONObject

/**
 * [JsonObject] backed by [org.json.JSONObject].
 *
 * `get*` methods throw [RuntimeException] if a value is missing or has an unexpected type,
 * `get*` with a fallback throw only on unexpected type,
 * `opt*` methods never throw.
 */
@Suppress("TooManyFunctions")
@ExperimentalApi
@JvmInline
internal value class OrgJsonObject(
    private val source: JSONObject
) : JsonObject {

    override val size: Int
        get() = source.length()

    override val keys: Set<String>
        get() = source.keys().asSequence().toSet()

    override val values: Collection<Any?>
        get() = source.keys().asSequence().map { opt(it) }.toList()

    override fun isEmpty(): Boolean = source.length() == 0

    override fun containsKey(key: String): Boolean = source.has(key)

    override fun containsValue(value: Any?): Boolean = source.keys().asSequence().any { opt(it) == value }

    override fun iterator(): Iterator<Pair<String, Any?>> = source.keys().asSequence().map { it to opt(it) }.iterator()

    override fun has(key: String): Boolean = source.has(key)

    override fun length(): Int = source.length()

    override fun get(key: String): Any = opt(key) ?: throw missingValue(key)

    override fun opt(key: String): Any? = source.opt(key).asJsonValue()

    override fun getInt(key: String): Int = requireValue(key) { it.asInt() }

    override fun getInt(key: String, fallback: Int): Int = requireValue(key, fallback) { it.asInt() }

    override fun optInt(key: String): Int? = opt(key).asInt()

    override fun optInt(key: String, fallback: Int): Int = optInt(key) ?: fallback

    override fun getLong(key: String): Long = requireValue(key) { it.asLong() }

    override fun getLong(key: String, fallback: Long): Long = requireValue(key, fallback) { it.asLong() }

    override fun optLong(key: String): Long? = opt(key).asLong()

    override fun optLong(key: String, fallback: Long): Long = optLong(key) ?: fallback

    override fun getFloat(key: String): Float = requireValue(key) { it.asFloat() }

    override fun getFloat(key: String, fallback: Float): Float = requireValue(key, fallback) { it.asFloat() }

    override fun optFloat(key: String): Float? = opt(key).asFloat()

    override fun optFloat(key: String, fallback: Float): Float = optFloat(key) ?: fallback

    override fun getDouble(key: String): Double = requireValue(key) { it.asDouble() }

    override fun getDouble(key: String, fallback: Double): Double = requireValue(key, fallback) { it.asDouble() }

    override fun optDouble(key: String): Double? = opt(key).asDouble()

    override fun optDouble(key: String, fallback: Double): Double = optDouble(key) ?: fallback

    override fun getBoolean(key: String): Boolean = requireValue(key) { it.asBoolean() }

    override fun getBoolean(key: String, fallback: Boolean): Boolean = requireValue(key, fallback) { it.asBoolean() }

    override fun optBoolean(key: String): Boolean? = opt(key).asBoolean()

    override fun optBoolean(key: String, fallback: Boolean): Boolean = optBoolean(key) ?: fallback

    override fun getString(key: String): String = requireValue(key) { it.asString() }

    override fun getString(key: String, fallback: String): String = requireValue(key, fallback) { it.asString() }

    override fun optString(key: String): String? = opt(key).asString()

    override fun optString(key: String, fallback: String): String = optString(key) ?: fallback

    override fun getObject(key: String): JsonObject = requireValue(key) { it as? JsonObject }

    override fun optObject(key: String): JsonObject? = opt(key) as? JsonObject

    override fun getArray(key: String): JsonArray = requireValue(key) { it as? JsonArray }

    override fun optArray(key: String): JsonArray? = opt(key) as? JsonArray

    override fun modify(modify: JsonObject.Builder.() -> Unit): JsonObject {
        return OrgJsonObjectBuilder(
            source,
            isShared = false,
        ).apply(modify).build()
    }

    override fun asOrgJsonObject(): JSONObject = source

    override fun asKtxJsonObject(): kotlinx.serialization.json.JsonObject = source.toKtxJsonObject()

    override fun toString(): String = source.toString()
}

@Suppress("TooManyFunctions")
private class OrgJsonObjectBuilder(
    source: JSONObject,
    val isShared: Boolean,
) : JsonObject.Builder {

    private val data = source.copy()

    override fun put(key: String, value: Int) {
        putImpl(key, value)
    }

    override fun put(key: String, value: Long) {
        putImpl(key, value)
    }

    override fun put(key: String, value: Float) {
        putImpl(key, requireFinite(value))
    }

    override fun put(key: String, value: Double) {
        putImpl(key, requireFinite(value))
    }

    override fun put(key: String, value: Boolean) {
        putImpl(key, value)
    }

    override fun put(key: String, value: String) {
        putImpl(key, value)
    }

    override fun put(key: String, value: JsonObject) {
        putImpl(key, value.asOrgJsonObject())
    }

    override fun put(key: String, value: JsonArray) {
        putImpl(key, value.asOrgJsonArray())
    }

    override fun put(key: String, value: Any?) {
        if (value == null) {
            remove(key)
        } else {
            dispatchPut(key, value)
        }
    }

    private fun dispatchPut(key: String, value: Any) = when (value) {
        is String -> put(key, value)
        is Boolean -> put(key, value)
        is Int -> put(key, value)
        is Long -> put(key, value)
        is Float -> put(key, value)
        is Double -> put(key, value)
        is JsonObject -> put(key, value)
        is JsonArray -> put(key, value)
        else -> throw unsupportedValue(value)
    }

    private fun putImpl(key: String, value: Any?) {
        try {
            data.put(key, value)
        } catch (e: JSONException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun remove(key: String): Any? {
        return data.remove(key).asJsonValue()
    }

    override fun build(): JsonObject {
        val result = if (isShared) data.copy() else data
        return OrgJsonObject(result)
    }
}
