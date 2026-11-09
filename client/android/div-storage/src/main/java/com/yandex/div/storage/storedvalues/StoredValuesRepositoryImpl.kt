package com.yandex.div.storage.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.storedvalues.StoredValuesRepository
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.rawjson.RawJson
import org.json.JSONObject

@InternalApi
class StoredValuesRepositoryImpl(
    private val rawJsonRepository: RawJsonRepository
) : StoredValuesRepository {

    override fun get(id: String): JSONObject? {
        return rawJsonRepository.get(listOf(id)).resultData.firstOrNull()?.data
    }

    override fun put(id: String, value: JSONObject) {
        rawJsonRepository.put(
            RawJsonRepository.Payload(
                jsons = listOf(RawJson(id, value))
            )
        )
    }

    override fun remove(id: String) {
        rawJsonRepository.remove { it.id == id }
    }
}
