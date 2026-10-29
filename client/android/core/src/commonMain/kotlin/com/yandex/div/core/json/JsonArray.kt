package com.yandex.div.core.json

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.type.DataArray
import kotlin.jvm.JvmStatic
import kotlinx.serialization.json.JsonArray as KtxArray

@Suppress("TooManyFunctions")
@ExperimentalApi
public interface JsonArray : JsonElement, DataArray {

    public fun length(): Int

    override fun get(index: Int): Any
    override fun opt(index: Int): Any?

    public fun getInt(index: Int): Int
    public fun getInt(index: Int, fallback: Int): Int
    public fun optInt(index: Int): Int?
    public fun optInt(index: Int, fallback: Int): Int

    public fun getLong(index: Int): Long
    public fun getLong(index: Int, fallback: Long): Long
    public fun optLong(index: Int): Long?
    public fun optLong(index: Int, fallback: Long): Long

    public fun getFloat(index: Int): Float
    public fun getFloat(index: Int, fallback: Float): Float
    public fun optFloat(index: Int): Float?
    public fun optFloat(index: Int, fallback: Float): Float

    public fun getDouble(index: Int): Double
    public fun getDouble(index: Int, fallback: Double): Double
    public fun optDouble(index: Int): Double?
    public fun optDouble(index: Int, fallback: Double): Double

    public fun getBoolean(index: Int): Boolean
    public fun getBoolean(index: Int, fallback: Boolean): Boolean
    public fun optBoolean(index: Int): Boolean?
    public fun optBoolean(index: Int, fallback: Boolean): Boolean

    public fun getString(index: Int): String
    public fun getString(index: Int, fallback: String): String
    public fun optString(index: Int): String?
    public fun optString(index: Int, fallback: String): String

    public fun getObject(index: Int): JsonObject
    public fun optObject(index: Int): JsonObject?

    public fun getArray(index: Int): JsonArray
    public fun optArray(index: Int): JsonArray?

    public fun modify(modify: Builder.() -> Unit): JsonArray

    public fun asKtxJsonArray(): KtxArray

    @Suppress("TooManyFunctions")
    public interface Builder {
        public fun add(value: Int)
        public fun add(value: Long)
        public fun add(value: Float)
        public fun add(value: Double)
        public fun add(value: Boolean)
        public fun add(value: String)
        public fun add(value: JsonObject)
        public fun add(value: JsonArray)
        public fun add(value: Any?)

        public fun put(index: Int, value: Int)
        public fun put(index: Int, value: Long)
        public fun put(index: Int, value: Float)
        public fun put(index: Int, value: Double)
        public fun put(index: Int, value: Boolean)
        public fun put(index: Int, value: String)
        public fun put(index: Int, value: JsonObject)
        public fun put(index: Int, value: JsonArray)
        public fun put(index: Int, value: Any?)

        public fun remove(index: Int): Any?

        public fun build(): JsonArray
    }

    public companion object {

        @JvmStatic
        public fun builder(): Builder = KtxJsonArrayBuilder(
            source = KtxArray(emptyList()),
            isShared = true,
        )

        @JvmStatic
        @InternalApi
        public fun builder(isShared: Boolean = true): Builder = KtxJsonArrayBuilder(
            KtxArray(emptyList()),
            isShared,
        )
    }
}
