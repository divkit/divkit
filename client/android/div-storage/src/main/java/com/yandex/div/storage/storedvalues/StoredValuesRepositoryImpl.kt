package com.yandex.div.storage.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.json.asJsonObject
import com.yandex.div.core.json.asOrgJsonObject
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.common.StoredValuesRepository
import com.yandex.div.storage.rawjson.RawJson

@InternalApi
class StoredValuesRepositoryImpl(
    private val rawJsonRepository: RawJsonRepository
) : StoredValuesRepository {

    override fun get(id: String): JsonObject? {
        return rawJsonRepository.get(listOf(id)).resultData.firstOrNull()?.data?.asJsonObject()
    }

    override fun put(id: String, value: JsonObject) {
        rawJsonRepository.put(
            RawJsonRepository.Payload(
                jsons = listOf(RawJson(id, value.asOrgJsonObject()))
            )
        )
    }

    override fun remove(id: String) {
        rawJsonRepository.remove { it.id == id }
    }
}
