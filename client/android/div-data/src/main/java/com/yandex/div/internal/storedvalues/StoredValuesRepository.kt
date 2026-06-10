package com.yandex.div.internal.storedvalues

import com.yandex.div.core.annotations.InternalApi
import org.json.JSONObject

@InternalApi
interface StoredValuesRepository {
    fun get(id: String): JSONObject?
    fun put(id: String, value: JSONObject)
    fun remove(id: String)
}
