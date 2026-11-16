package com.yandex.div.storage.internal

import androidx.annotation.AnyThread
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.Template

@InternalApi
public interface DivStorage<T : Any> {
    @AnyThread
    public fun saveData(groupId: String,
                 divs: List<RawDataAndMetadata<T>>,
                 templatesByHash: List<Template<T>>,
                 actionOnError: ActionOnError,
    ): ExecutionResult

    @AnyThread
    public fun saveRawJsons(
        rawJsons: List<RawJson<T>>,
        actionOnError: ActionOnError,
    ): ExecutionResult

    @AnyThread
    public fun readRawJsons(rawJsonIds: Set<String>): LoadDataResult<RawJson<T>>

    @AnyThread
    public fun removeRawJsons(predicate: (RawJson<T>) -> Boolean): RemoveResult

    @AnyThread
    public fun loadData(ids: List<String>, idsToExclude: List<String>): LoadDataResult<RestoredRawData<T>>

    @AnyThread
    @Throws(androidx.sqlite.SQLiteException::class)
    public fun remove(predicate: (RawDataAndMetadata<T>) -> Boolean): RemoveResult

    @AnyThread
    public fun removeAllCards(): DivStorageErrorException?

    @AnyThread
    public fun readTemplates(templateHashes: Set<String>): LoadDataResult<RawTemplateData>

    @AnyThread
    public fun removeAllTemplates(): DivStorageErrorException?

    @Throws(DivStorageErrorException::class)
    public fun isCardExists(id: String, groupId: String): Boolean

    @Throws(DivStorageErrorException::class)
    public fun isTemplateExists(templateHash: String): Boolean

    public fun readTemplateReferences(): LoadDataResult<TemplateReference>

    public val migrations: Map<Pair<Int, Int>, com.yandex.div.storage.database.Migration>

    public data class RestoredRawData<T : Any>(
            public val id: String,
            public val divData: T,
            public val metadata: T? = null,
            public val groupId: String,
    )

    public data class LoadDataResult<T>(
            public val restoredData: List<T>,
            public val errors: List<StorageException> = emptyList()
    )

    public data class RemoveResult(
            public val ids: Set<String>,
            public val errors: List<StorageException> = emptyList()
    )

    public data class TemplateReference(
            public val groupId: String,
            public val templateId: String,
            public val templateHash: String,
    )
}
