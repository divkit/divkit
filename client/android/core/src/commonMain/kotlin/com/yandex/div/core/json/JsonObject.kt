package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.type.DataObject
import org.json.JSONObject
import kotlinx.serialization.json.JsonObject as KtxObject

@Suppress("TooManyFunctions")
@ExperimentalApi
public interface JsonObject : JsonElement, DataObject {

    public fun has(key: String): Boolean
    public fun length(): Int

    override fun get(key: String): Any
    override fun opt(key: String): Any?

    public fun getInt(key: String): Int
    public fun getInt(key: String, fallback: Int): Int
    public fun optInt(key: String): Int?
    public fun optInt(key: String, fallback: Int): Int

    public fun getLong(key: String): Long
    public fun getLong(key: String, fallback: Long): Long
    public fun optLong(key: String): Long?
    public fun optLong(key: String, fallback: Long): Long

    public fun getFloat(key: String): Float
    public fun getFloat(key: String, fallback: Float): Float
    public fun optFloat(key: String): Float?
    public fun optFloat(key: String, fallback: Float): Float

    public fun getDouble(key: String): Double
    public fun getDouble(key: String, fallback: Double): Double
    public fun optDouble(key: String): Double?
    public fun optDouble(key: String, fallback: Double): Double

    public fun getBoolean(key: String): Boolean
    public fun getBoolean(key: String, fallback: Boolean): Boolean
    public fun optBoolean(key: String): Boolean?
    public fun optBoolean(key: String, fallback: Boolean): Boolean

    public fun getString(key: String): String
    public fun getString(key: String, fallback: String): String
    public fun optString(key: String): String?
    public fun optString(key: String, fallback: String): String

    public fun getObject(key: String): JsonObject
    public fun optObject(key: String): JsonObject?

    public fun getArray(key: String): JsonArray
    public fun optArray(key: String): JsonArray?

    public fun modify(modify: Builder.() -> Unit): JsonObject

    public fun asOrgJsonObject(): JSONObject
    public fun asKtxJsonObject(): KtxObject

    public interface Builder {
        public fun put(key: String, value: Int)
        public fun put(key: String, value: Long)
        public fun put(key: String, value: Float)
        public fun put(key: String, value: Double)
        public fun put(key: String, value: Boolean)
        public fun put(key: String, value: String)
        public fun put(key: String, value: JsonObject)
        public fun put(key: String, value: JsonArray)
        public fun put(key: String, value: Any?)

        public fun remove(key: String): Any?

        public fun build(): JsonObject
    }

    public companion object {

        @JvmStatic
        public fun builder(): Builder = KtxJsonObjectBuilder(
            source = KtxObject(emptyMap()),
            isShared = true,
        )

        @JvmStatic
        @InternalApi
        public fun builder(isShared: Boolean = true): Builder = KtxJsonObjectBuilder(
            KtxObject(emptyMap()),
            isShared,
        )
    }
}
