package com.yandex.div.storage.internal

import com.yandex.div.storage.RawJsonRepositoryException
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.util.concurrentMap

private typealias JsonId = String

internal class RawJsonRepositoryImpl<T : Any>(
    private val divStorage: DivStorage<T>,
) : RawJsonRepository<T> {
    private val inMemoryData = concurrentMap<JsonId, RawJson<T>>()

    private var areJsonsSynchronizedWithInMemory = false
    private var jsonIdsWithErrors = setOf<JsonId>()

    override fun put(payload: RawJsonRepository.Payload<T>): RawJsonRepositoryResult<T> {
        val rawJsons = payload.jsons
        rawJsons.forEach { inMemoryData[it.id] = it }

        val divStorageErrors = divStorage.saveRawJsons(rawJsons, payload.actionOnError).errors
        val exceptions = mutableListOf<RawJsonRepositoryException>()
        exceptions.addAll(divStorageErrors.toRawJsonRepositoryExceptions())

        return RawJsonRepositoryResult(rawJsons, exceptions)
    }

    override fun get(ids: List<JsonId>): RawJsonRepositoryResult<T> {
        if (ids.isEmpty()) {
            return RawJsonRepositoryResult.empty()
        }

        val idsToLoad = ids.toMutableSet()
        val inMemoryResults = ArrayList<RawJson<T>>(ids.size)
        ids.forEach { id ->
            inMemoryData[id]?.let {
                inMemoryResults.add(it)
                idsToLoad.remove(id)
            }
        }

        if (idsToLoad.isNotEmpty()) {
            val storageResults = loadFromStorage(idsToLoad)
            storageResults.resultData.forEach { inMemoryData[it.id] = it }
            return storageResults.addData(inMemoryResults)
        }
        return RawJsonRepositoryResult(inMemoryResults, emptyList())
    }

    override fun getAll(): RawJsonRepositoryResult<T> {
        if (areJsonsSynchronizedWithInMemory && jsonIdsWithErrors.isEmpty()) {
            return RawJsonRepositoryResult(inMemoryData.values.toList(), emptyList())
        }
        val jsonsToRequest = if (areJsonsSynchronizedWithInMemory) {
            jsonIdsWithErrors
        } else {
            emptySet()
        }

        val storageResults = loadFromStorage(jsonsToRequest)
        val resultsWithInMemory = storageResults.addData(inMemoryData.values)

        storageResults.resultData.forEach { inMemoryData[it.id] = it }
        areJsonsSynchronizedWithInMemory = true

        jsonIdsWithErrors = storageResults.errors.groupBy { it.jsonId }.getKeysNotNull()

        return resultsWithInMemory
    }

    override fun remove(predicate: (RawJson<T>) -> Boolean): RawJsonRepositoryRemoveResult {
        val (deletedIds, storageExceptions) = divStorage.removeRawJsons(predicate)
        val exceptions = storageExceptions.toRawJsonRepositoryExceptions()
        removeFromInMemory(deletedIds)
        return RawJsonRepositoryRemoveResult(deletedIds, exceptions)
    }

    private fun loadFromStorage(ids: Set<JsonId>): RawJsonRepositoryResult<T> {
        val exceptions = mutableListOf<RawJsonRepositoryException>()
        val (rawJsons, storageExceptions) = divStorage.readRawJsons(ids)
        exceptions.addAll(storageExceptions.toRawJsonRepositoryExceptions())
        return RawJsonRepositoryResult(rawJsons, exceptions)
    }

    private fun removeFromInMemory(deletedRecords: Set<JsonId>) {
        deletedRecords.forEach {
            inMemoryData.remove(it)
        }
    }

    private fun <K, V> Map<K?, V>.getKeysNotNull(): Set<K> {
        val filteredKeys = mutableSetOf<K>()
        for ((key, _) in entries) {
            if (key != null) {
                filteredKeys.add(key)
            }
        }
        return filteredKeys
    }

    private fun List<StorageException>.toRawJsonRepositoryExceptions() =
        this.map { RawJsonRepositoryException(it) }
}
