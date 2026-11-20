package com.yandex.div.storage.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.json.asJsonObject
import com.yandex.div.core.json.asOrgJsonObject
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.RawJsonRepositoryImpl
import com.yandex.div.storage.StoredValuesRepository
import com.yandex.div.storage.rawjson.RawJson

@InternalApi
class StoredValuesRepositoryImpl(
    rawJsonRepository: RawJsonRepository,
) : StoredValuesRepository by when (rawJsonRepository) {
    is RawJsonRepositoryImpl -> rawJsonRepository.createStoredValuesRepository()
    else -> RawJsonStoredValuesAdapter(rawJsonRepository)
}

private class RawJsonStoredValuesAdapter(
    private val rawJsonRepository: RawJsonRepository,
) : StoredValuesRepository {
    override fun get(id: String): JsonObject? =
        rawJsonRepository.get(listOf(id)).resultData.firstOrNull()?.data?.asJsonObject()

    override fun put(id: String, value: JsonObject) {
        val rawJson = RawJson(id, value.asOrgJsonObject())
        rawJsonRepository.put(RawJsonRepository.Payload(listOf(rawJson)))
    }

    override fun remove(id: String) {
        rawJsonRepository.remove { it.id == id }
    }
}
