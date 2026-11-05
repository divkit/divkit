package com.yandex.div.compose.storedvalues

import com.yandex.div.core.json.JsonObject
import com.yandex.div.storage.common.StoredValuesRepository

class TestStoredValuesRepository : StoredValuesRepository {
    private val _values = mutableMapOf<String, JsonObject>()
    val values: Map<String, JsonObject> get() = _values

    override fun get(id: String): JsonObject? {
        return _values[id]
    }

    override fun put(id: String, value: JsonObject) {
        _values[id] = value
    }

    override fun remove(id: String) {
        _values.remove(id)
    }
}
