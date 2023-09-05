package com.yandex.div.storage

import androidx.annotation.AnyThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.Migration
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.rawjson.RawJson
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.Template
import org.json.JSONObject

internal interface DivStorage {
    @AnyThread
    fun saveData(groupId: String,
                 divs: List<RawDataAndMetadata>,
                 templatesByHash: List<Template>,
                 actionOnError: DivDataRepository.ActionOnError,
    ): ExecutionResult

    @AnyThread
    fun saveRawJsons(
        rawJsons: List<RawJson>,
        actionOnError: DivDataRepository.ActionOnError,
    ): ExecutionResult

    @AnyThread
    fun readRawJsons(rawJsonIds: Set<String>): LoadDataResult<RawJson>

    @AnyThread
    fun removeRawJsons(predicate: (RawJson) -> Boolean): RemoveResult

    @AnyThread
    fun loadData(ids: List<String>): LoadDataResult<RestoredRawData>

    @AnyThread
    fun remove(predicate: (RawDataAndMetadata) -> Boolean): RemoveResult

    @AnyThread
    fun removeAllCards(): DivStorageErrorException?

    @AnyThread
    fun readTemplates(templateHashes: Set<String>): LoadDataResult<RawTemplateData>

    @AnyThread
    fun removeAllTemplates(): DivStorageErrorException?

    @Throws(DivStorageErrorException::class)
    fun isCardExists(id: String, groupId: String): Boolean

    @Throws(DivStorageErrorException::class)
    fun isTemplateExists(templateHash: String): Boolean

    fun readTemplateReferences(): LoadDataResult<TemplateReference>

    val migrations: Map<Pair<Int, Int>, Migration>

    data class RestoredRawData(
            val id: String,
            val divData: JSONObject,
            val metadata: JSONObject? = null,
            internal val groupId: String,
    )

    @Mockable
    data class LoadDataResult<T>(
            val restoredData: List<T>,
            val errors: List<StorageException> = emptyList()
    )

    data class RemoveResult(
            val ids: Set<String>,
            val errors: List<StorageException> = emptyList()
    )

    data class TemplateReference(
            val groupId: String,
            val templateId: String,
            val templateHash: String,
    )
}
