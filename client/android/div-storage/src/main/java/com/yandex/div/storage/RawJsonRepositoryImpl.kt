package com.yandex.div.storage

import androidx.annotation.UiThread
import com.yandex.div.internal.KAssert
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.rawjson.RawJson

private typealias JsonId = String

internal class RawJsonRepositoryImpl(
    private val divStorage: DivStorage,
) : RawJsonRepository {
    private val inMemoryData = mutableMapOf<JsonId, RawJson>()

    private var areJsonsSynchronizedWithInMemory = false
    private var jsonIdsWithErrors = setOf<JsonId>()

    @UiThread
    override fun put(payload: RawJsonRepository.Payload): RawJsonRepositoryResult {
        KAssert.assertMainThread()
        val rawJsons = payload.jsons
        rawJsons.forEach { inMemoryData[it.id] = it }

        val divStorageErrors = divStorage.saveRawJsons(rawJsons, payload.actionOnError).errors
        val exceptions = mutableListOf<RawJsonRepositoryException>()
        exceptions.addAll(divStorageErrors.toRawJsonRepositoryExceptions())

        return RawJsonRepositoryResult(rawJsons, exceptions)
    }

    @UiThread
    override fun get(ids: List<JsonId>): RawJsonRepositoryResult {
        KAssert.assertMainThread()
        if (ids.isEmpty()) {
            return RawJsonRepositoryResult.EMPTY
        }

        val idsToLoad = ids.toMutableSet()
        val inMemoryResults = ArrayList<RawJson>(ids.size)
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

    @UiThread
    override fun getAll(): RawJsonRepositoryResult {
        KAssert.assertMainThread()
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

    @UiThread
    override fun remove(predicate: (RawJson) -> Boolean): RawJsonRepositoryRemoveResult {
        KAssert.assertMainThread()
        val (deletedIds, storageExceptions) = divStorage.removeRawJsons(predicate)
        val exceptions = storageExceptions.toRawJsonRepositoryExceptions()
        removeFromInMemory(deletedIds)
        return RawJsonRepositoryRemoveResult(deletedIds, exceptions)
    }

    private fun loadFromStorage(ids: Set<JsonId>): RawJsonRepositoryResult {
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
