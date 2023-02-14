package com.yandex.div.storage

import androidx.annotation.WorkerThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.Template
import org.json.JSONObject

internal interface DivStorage {
    fun saveData(groupId: String,
                 divs: List<RawDataAndMetadata>,
                 templatesByHash: List<Template>,
                 actionOnError: DivDataRepository.ActionOnError
    ): ExecutionResult

    fun loadData(ids: List<String>): LoadDataResult<RestoredRawData>

    fun remove(predicate: (RawDataAndMetadata) -> Boolean): RemoveResult

    @WorkerThread
    fun removeAllCards(): DivStorageErrorException?

    @WorkerThread
    fun readTemplates(templateHashes: Set<String>): LoadDataResult<RawTemplateData>

    @WorkerThread
    fun removeAllTemplates(): DivStorageErrorException?

    @Throws(DivStorageErrorException::class)
    fun isCardExists(id: String, groupId: String): Boolean

    @Throws(DivStorageErrorException::class)
    fun isTemplateExists(templateHash: String): Boolean

    fun readTemplateReferences(): LoadDataResult<TemplateReference>

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
