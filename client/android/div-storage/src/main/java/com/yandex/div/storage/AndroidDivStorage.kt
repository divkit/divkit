package com.yandex.div.storage

import android.content.Context
import androidx.sqlite.SQLiteException
import com.yandex.div.internal.KAssert
import com.yandex.div.storage.ActionOnError as CommonActionOnError
import com.yandex.div.storage.StorageCodec
import com.yandex.div.storage.database.AndroidDatabaseOpenHelperProvider
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.isAndroidDatabaseLocked
import com.yandex.div.storage.internal.DivStorage as CommonDivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata as CommonRawData
import com.yandex.div.storage.internal.rawjson.RawJson as CommonRawJson
import com.yandex.div.storage.rawjson.RawJson
import com.yandex.div.storage.templates.AndroidTemplate
import com.yandex.div.storage.templates.RawTemplateData
import org.json.JSONObject

@Suppress("TooManyFunctions")
internal class AndroidDivStorage(
    context: Context,
    openHelperProvider: AndroidDatabaseOpenHelperProvider,
    databaseNamePrefix: String = "",
) : DivStorage {
    internal val delegate = CommonDivStorage(
        codec = object : StorageCodec<JSONObject> {
            override fun encode(value: JSONObject): ByteArray = value.toString().encodeToByteArray()
            override fun decode(bytes: ByteArray): JSONObject = JSONObject(bytes.decodeToString())
        },
        openHelperProvider = { name, version, create, upgrade ->
            openHelperProvider.provide(context, name, version, create, upgrade)
        },
        databaseNamePrefix = databaseNamePrefix,
        reportMigrationError = { error, message -> KAssert.fail(error) { message } },
        sqlExceptionFactory = { message, cause -> SQLiteException(message, cause) },
        // Preserve the SQL exception boundary of the original Android loadData implementation.
        shouldCatchLoadException = ::isAndroidDatabaseLocked,
    )

    override val migrations get() = delegate.migrations

    override fun saveData(
        groupId: String,
        divs: List<RawDataAndMetadata>,
        templatesByHash: List<AndroidTemplate>,
        actionOnError: DivDataRepository.ActionOnError,
    ): ExecutionResult = delegate.saveData(
        groupId, divs.map { it.asCommon() }, templatesByHash, actionOnError.asCommon()
    )

    override fun saveRawJsons(
        rawJsons: List<RawJson>,
        actionOnError: DivDataRepository.ActionOnError,
    ): ExecutionResult = delegate.saveRawJsons(
        rawJsons.map { it.asCommon() }, actionOnError.asCommon()
    )

    override fun loadData(
        ids: List<String>,
        idsToExclude: List<String>,
    ): DivStorage.LoadDataResult<DivStorage.RestoredRawData> =
        delegate.loadData(ids, idsToExclude).asAndroid {
            DivStorage.RestoredRawData(it.id, it.divData, it.metadata, it.groupId)
        }

    override fun readRawJsons(rawJsonIds: Set<String>): DivStorage.LoadDataResult<RawJson> =
        delegate.readRawJsons(rawJsonIds).asAndroid { RawJson(it.id, it.data) }

    override fun remove(predicate: (RawDataAndMetadata) -> Boolean): DivStorage.RemoveResult =
        delegate.remove { predicate(it.asAndroid()) }.asAndroid()

    override fun removeRawJsons(predicate: (RawJson) -> Boolean): DivStorage.RemoveResult =
        delegate.removeRawJsons { predicate(it.asAndroid()) }.asAndroid()

    override fun readTemplates(templateHashes: Set<String>): DivStorage.LoadDataResult<RawTemplateData> =
        delegate.readTemplates(templateHashes).asAndroid { it }

    override fun readTemplateReferences(): DivStorage.LoadDataResult<DivStorage.TemplateReference> =
        delegate.readTemplateReferences().asAndroid {
            DivStorage.TemplateReference(it.groupId, it.templateId, it.templateHash)
        }

    override fun isCardExists(id: String, groupId: String): Boolean =
        delegate.isCardExists(id, groupId)

    override fun isTemplateExists(templateHash: String): Boolean =
        delegate.isTemplateExists(templateHash)

    override fun removeAllCards(): DivStorageErrorException? =
        delegate.removeAllCards()

    override fun removeAllTemplates(): DivStorageErrorException? =
        delegate.removeAllTemplates()


}

private fun RawDataAndMetadata.asCommon(): CommonRawData<JSONObject> =
    object : CommonRawData<JSONObject> {
        override val id get() = this@asCommon.id
        override val divData get() = this@asCommon.divData
        override val metadata get() = this@asCommon.metadata
    }

private fun CommonRawData<JSONObject>.asAndroid(): RawDataAndMetadata = object : RawDataAndMetadata {
    override val id get() = this@asAndroid.id
    override val divData get() = this@asAndroid.divData
    override val metadata get() = this@asAndroid.metadata
}

private fun RawJson.asCommon(): CommonRawJson<JSONObject> = object : CommonRawJson<JSONObject> {
    override val id get() = this@asCommon.id
    override val data get() = this@asCommon.data
}

private fun CommonRawJson<JSONObject>.asAndroid(): RawJson = object : RawJson {
    override val id get() = this@asAndroid.id
    override val data get() = this@asAndroid.data
}

private fun DivDataRepository.ActionOnError.asCommon(): CommonActionOnError = when (this) {
    DivDataRepository.ActionOnError.ABORT_TRANSACTION -> CommonActionOnError.ABORT_TRANSACTION
    DivDataRepository.ActionOnError.SKIP_ELEMENT -> CommonActionOnError.SKIP_ELEMENT
}

private fun <T, R> CommonDivStorage.LoadDataResult<T>.asAndroid(
    convert: (T) -> R,
): DivStorage.LoadDataResult<R> =
    DivStorage.LoadDataResult(restoredData.map(convert), errors)

private fun CommonDivStorage.RemoveResult.asAndroid(): DivStorage.RemoveResult =
    DivStorage.RemoveResult(ids, errors)
