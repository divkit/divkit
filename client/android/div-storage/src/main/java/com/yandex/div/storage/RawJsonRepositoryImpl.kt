package com.yandex.div.storage

import com.yandex.div.core.json.asJsonObject
import com.yandex.div.core.json.asOrgJsonObject
import com.yandex.div.storage.ActionOnError as CommonActionOnError
import com.yandex.div.storage.internal.DivStorage as CommonDivStorage
import com.yandex.div.storage.internal.RawJsonRepository as CommonRawJsonRepository
import com.yandex.div.storage.internal.RawJsonRepositoryResult as CommonRepositoryResult
import com.yandex.div.storage.internal.rawjson.RawJson as CommonRawJson
import com.yandex.div.storage.rawjson.RawJson
import org.json.JSONObject

internal class RawJsonRepositoryImpl(divStorage: CommonDivStorage<JSONObject>) : RawJsonRepository {
    private val delegate = CommonRawJsonRepository(object : CommonDivStorage<JSONObject> by divStorage {
        override fun readRawJsons(
            rawJsonIds: Set<String>,
        ): CommonDivStorage.LoadDataResult<CommonRawJson<JSONObject>> {
            val result = divStorage.readRawJsons(rawJsonIds)
            return CommonDivStorage.LoadDataResult(
                result.restoredData.map { AndroidRawJson(RawJson(it.id, it.data)) }, result.errors
            )
        }
    })

    internal fun createStoredValuesRepository(): StoredValuesRepository =
        PersistentStoredValuesRepository(
            delegate,
            toJsonObject = JSONObject::asJsonObject,
            createRecord = { id, value -> AndroidRawJson(RawJson(id, value.asOrgJsonObject())) },
        )

    override fun put(payload: RawJsonRepository.Payload): RawJsonRepositoryResult {
        val result = delegate.put(
            CommonRawJsonRepository.Payload(
                payload.jsons.map { AndroidRawJson(it) },
                when (payload.actionOnError) {
                    DivDataRepository.ActionOnError.ABORT_TRANSACTION ->
                        CommonActionOnError.ABORT_TRANSACTION
                    DivDataRepository.ActionOnError.SKIP_ELEMENT -> CommonActionOnError.SKIP_ELEMENT
                },
            )
        )
        return RawJsonRepositoryResult(payload.jsons, result.errors)
    }

    override fun get(ids: List<String>): RawJsonRepositoryResult =
        if (ids.isEmpty()) RawJsonRepositoryResult.EMPTY else delegate.get(ids).asAndroid()

    override fun getAll(): RawJsonRepositoryResult = delegate.getAll().asAndroid()

    override fun remove(predicate: (RawJson) -> Boolean): RawJsonRepositoryRemoveResult {
        val result = delegate.remove { predicate(it.asAndroid()) }
        return RawJsonRepositoryRemoveResult(result.ids, result.errors)
    }
}

private class AndroidRawJson(val source: RawJson) : CommonRawJson<JSONObject> {
    override val id get() = source.id
    override val data get() = source.data
}

private fun CommonRawJson<JSONObject>.asAndroid(): RawJson =
    if (this is AndroidRawJson) source else object : RawJson {
        override val id get() = this@asAndroid.id
        override val data get() = this@asAndroid.data
    }

private fun CommonRepositoryResult<JSONObject>.asAndroid(): RawJsonRepositoryResult =
    RawJsonRepositoryResult(resultData.map { it.asAndroid() }, errors)

