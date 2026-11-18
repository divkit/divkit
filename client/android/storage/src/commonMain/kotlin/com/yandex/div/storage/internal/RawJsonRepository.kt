package com.yandex.div.storage.internal

import androidx.annotation.UiThread
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.internal.rawjson.RawJson

/**
 * Encapsulates logic for parsing, storing and accessing RawJson from in-memory
 * and persistent storages.
 */
@InternalApi
public interface RawJsonRepository<T : Any> {
    /**
     * Will try to parse raw json payload and after successful
     * parse this data will be available at in-memory and saved to persistent storage.
     */
    @UiThread
    public fun put(payload: Payload<T>): RawJsonRepositoryResult<T>

    /**
     * @param ids list of specific identifiers for loading.
     */
    @UiThread
    public fun get(ids: List<String>): RawJsonRepositoryResult<T>

    /**
     * Requests all jsons from storage.
     */
    @UiThread
    public fun getAll(): RawJsonRepositoryResult<T>

    /**
     * Allows to remove data from in-memory and persistent storage.
     */
    @UiThread
    public fun remove(predicate: (RawJson<T>) -> Boolean): RawJsonRepositoryRemoveResult

    /**
     * @param jsons raw jsons
     * @param actionOnError defines behavior in case it is impossible to parse [jsons] correctly
     */
    public data class Payload<T : Any>(
        public val jsons: List<RawJson<T>>,
        public val actionOnError: ActionOnError = ActionOnError.ABORT_TRANSACTION
    )
}

@InternalApi
public fun <T : Any> RawJsonRepository(divStorage: DivStorage<T>): RawJsonRepository<T> =
    RawJsonRepositoryImpl(divStorage)
