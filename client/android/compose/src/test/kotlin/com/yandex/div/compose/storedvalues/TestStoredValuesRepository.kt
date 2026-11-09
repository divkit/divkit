package com.yandex.div.compose.storedvalues

import com.yandex.div.internal.storedvalues.StoredValuesRepository
import org.json.JSONObject

class TestStoredValuesRepository : StoredValuesRepository {
    private val _values = mutableMapOf<String, JSONObject>()
    val values: Map<String, JSONObject> get() = _values

    override fun get(id: String): JSONObject? {
        return _values[id]
    }

    override fun put(id: String, value: JSONObject) {
        _values[id] = value
    }

    override fun remove(id: String) {
        _values.remove(id)
    }
}
