package com.yandex.div.storage.common

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonObject

/**
 * Storage of JSON value envelopes. Their type, scope and lifetime are owned by the caller.
 * Callers must serialize access to an instance; implementations need not be thread-safe.
 */
@InternalApi
public interface StoredValuesRepository {
    public fun get(id: String): JsonObject?
    public fun put(id: String, value: JsonObject)
    public fun remove(id: String)
}

/**
 * In-memory storage with separate entries for each instance.
 * Values are held by reference; callers own their mutability.
 */
@InternalApi
public class InMemoryStoredValuesRepository : StoredValuesRepository {
    private val values = mutableMapOf<String, JsonObject>()

    override fun get(id: String): JsonObject? = values[id]

    override fun put(id: String, value: JsonObject) {
        values[id] = value
    }

    override fun remove(id: String) {
        values.remove(id)
    }
}
