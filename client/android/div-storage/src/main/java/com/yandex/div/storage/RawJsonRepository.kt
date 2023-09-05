package com.yandex.div.storage

import androidx.annotation.UiThread
import com.yandex.div.storage.DivDataRepository.ActionOnError
import com.yandex.div.storage.rawjson.RawJson

/**
 * Encapsulates logic for parsing, storing and accessing RawJson from in-memory
 * and persistent storages.
 */
interface RawJsonRepository {
    /**
     * Will try to parse raw json payload and after successful
     * parse this data will be available at in-memory and saved to persistent storage.
     */
    @UiThread
    fun put(payload: Payload): RawJsonRepositoryResult

    /**
     * @param ids list of specific identifiers for loading.
     */
    @UiThread
    fun get(ids: List<String>): RawJsonRepositoryResult

    /**
     * Requests all jsons from storage.
     */
    @UiThread
    fun getAll(): RawJsonRepositoryResult

    /**
     * Allows to remove data from in-memory and persistent storage.
     */
    @UiThread
    fun remove(predicate: (RawJson) -> Boolean): RawJsonRepositoryRemoveResult

    /**
     * @param jsons raw jsons
     * @param actionOnError defines behavior in case it is impossible to parse [jsons] correctly
     */
    data class Payload(
        val jsons: List<RawJson>,
        val actionOnError: ActionOnError = ActionOnError.ABORT_TRANSACTION
    )
}
