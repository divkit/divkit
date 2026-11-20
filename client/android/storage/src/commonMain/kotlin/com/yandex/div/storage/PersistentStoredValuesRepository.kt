package com.yandex.div.storage

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonObject
import com.yandex.div.storage.internal.RawJsonRepository
import com.yandex.div.storage.internal.rawjson.RawJson

@InternalApi
public class PersistentStoredValuesRepository<T : Any>(
    private val rawJsonRepository: RawJsonRepository<T>,
    private val toJsonObject: (T) -> JsonObject,
    private val createRecord: (String, JsonObject) -> RawJson<T>,
) : StoredValuesRepository {
    override fun get(id: String): JsonObject? =
        rawJsonRepository.get(listOf(id)).resultData.firstOrNull()?.data?.let(toJsonObject)

    override fun put(id: String, value: JsonObject) {
        rawJsonRepository.put(RawJsonRepository.Payload(listOf(createRecord(id, value))))
    }

    override fun remove(id: String) {
        rawJsonRepository.remove { it.id == id }
    }
}

@InternalApi
public fun createPersistentStoredValuesRepository(
    rawJsonRepository: RawJsonRepository<JsonObject>,
): StoredValuesRepository = PersistentStoredValuesRepository(
    rawJsonRepository,
    toJsonObject = { it },
    createRecord = { id, value -> RawJson(id, value) },
)
