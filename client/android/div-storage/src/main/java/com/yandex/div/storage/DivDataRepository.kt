package com.yandex.div.storage

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.yandex.div2.DivData
import org.json.JSONObject

/**
 * Encapsulates logic for parsing, storing and accessing DivData from in-memory
 * and persistent storages.
 */
interface DivDataRepository {
    /**
     * Will try to parse raw json payload and after successful
     * parse this data will be available at in-memory and saved to persistent storage.
     */
    @AnyThread
    fun put(payload: Payload): DivDataRepositoryResult

    /**
     * @param ids list of specific identifiers for loading.
     */
    @AnyThread
    fun get(ids: List<String>): DivDataRepositoryResult

    /**
     * Requests all divs from storage.
     */
    @AnyThread
    fun getAll(): DivDataRepositoryResult

    /**
     * Allows to remove data from in-memory and persistent storage.
     */
    @AnyThread
    fun remove(predicate: (RawDataAndMetadata) -> Boolean): DivDataRepositoryRemoveResult

    class DivDataWithMeta(
            val id: String,
            val divData: DivData,
            val metadata: JSONObject?,
    )

    /**
     * @param divs raw div cards with metadata
     * @param templates card templates
     * @param sourceType source of cards, can be used in explaining card parsing errors
     * @param actionOnError defines behavior in case it is impossible to parse [divs] correctly
     */
    data class Payload(
            val divs: List<RawDataAndMetadata>,
            val templates: Map<String, JSONObject> = emptyMap(),
            val sourceType: String? = null,
            val actionOnError: ActionOnError = ActionOnError.ABORT_TRANSACTION
    )

    enum class ActionOnError {
        ABORT_TRANSACTION, SKIP_ELEMENT
    }
}
