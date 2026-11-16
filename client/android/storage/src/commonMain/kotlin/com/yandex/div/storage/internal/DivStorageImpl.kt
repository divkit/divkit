package com.yandex.div.storage.internal

import androidx.annotation.AnyThread
import androidx.annotation.VisibleForTesting
import androidx.sqlite.SQLiteException as SQLException
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div.storage.StorageCodec
import com.yandex.div.storage.database.COLUMN_CARD_DATA
import com.yandex.div.storage.database.COLUMN_CARD_GROUP_ID
import com.yandex.div.storage.database.COLUMN_CARD_METADATA
import com.yandex.div.storage.database.COLUMN_GROUP_ID
import com.yandex.div.storage.database.COLUMN_LAYOUT_ID
import com.yandex.div.storage.database.COLUMN_RAW_JSON_DATA
import com.yandex.div.storage.database.COLUMN_RAW_JSON_ID
import com.yandex.div.storage.database.COLUMN_TEMPLATE_DATA
import com.yandex.div.storage.database.COLUMN_TEMPLATE_HASH
import com.yandex.div.storage.database.COLUMN_TEMPLATE_ID
import com.yandex.div.storage.database.CREATE_TABLE_CARDS
import com.yandex.div.storage.database.CREATE_TABLE_RAW_JSON
import com.yandex.div.storage.database.CREATE_TABLE_TEMPLATES
import com.yandex.div.storage.database.CREATE_TABLE_TEMPLATE_REFERENCES
import com.yandex.div.storage.database.DB_VERSION
import com.yandex.div.storage.database.DELETE_CARDS
import com.yandex.div.storage.database.DELETE_TEMPLATES
import com.yandex.div.storage.database.DELETE_TEMPLATE_USAGES
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.Migration
import com.yandex.div.storage.database.ReadState
import com.yandex.div.storage.database.SELECT_RAW_JSONS_BY_IDS
import com.yandex.div.storage.database.SELECT_TEMPLATES_BY_HASHES
import com.yandex.div.storage.database.SingleTransactionDataSavePerformer
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.database.StorageStatementExecutor
import com.yandex.div.storage.database.StorageStatements
import com.yandex.div.storage.database.TABLE_CARDS
import com.yandex.div.storage.database.TABLE_TEMPLATE_REFERENCES
import com.yandex.div.storage.internal.DivStorage.LoadDataResult
import com.yandex.div.storage.internal.DivStorage.RemoveResult
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.Template
import com.yandex.div.storage.util.closeSilently
import com.yandex.div.storage.util.endTransactionSilently
import com.yandex.div.storage.util.getBlobOrNull
import com.yandex.div.storage.util.sqliteException

private const val DB_NAME = "div-storage.db"

internal class DivStorageImpl<T : Any>(
    private val codec: StorageCodec<T>,
    openHelperProvider: DatabaseOpenHelperProvider,
    databaseNamePrefix: String = "",
    private val reportMigrationError: (Throwable, String) -> Unit = { _, _ -> },
    private val sqlExceptionFactory: (String, Throwable) -> SQLException = ::sqliteException,
    private val shouldCatchLoadException: (SQLException) -> Boolean = { true },
) : DivStorage<T> {

    private val dbName = if (databaseNamePrefix.isEmpty()) DB_NAME else "$databaseNamePrefix-$DB_NAME"

    private val openHelper = openHelperProvider.provide(
            dbName, DB_VERSION, this::onCreate, this::onUpgrade
    )
    @VisibleForTesting  // TODO: ticket for make a private pls
    val statementExecutor = StorageStatementExecutor(sqlExceptionFactory) { openHelper.writableDatabase }
    private val storageStatements = StorageStatements(codec)
    private val dataSaveUseCase = SingleTransactionDataSavePerformer(codec, statementExecutor)

    override val migrations = mapOf(
        /**
         * Migration to add a new "raw_json" table
         */
        (2 to 3) to Migration { db ->
            try {
                db.execSQL(CREATE_TABLE_RAW_JSON)
            } catch (e: SQLException) {
                throw sqlExceptionFactory("Create \"raw_json\" table", e)
            }
        },
    )

    /**
     * Migration to recreate schema and delete all data
     */
    private val defaultDropAllMigration = Migration { db ->
        dropTables(db)
        createTables(db)
    }

    @VisibleForTesting
    fun onCreate(db: DatabaseOpenHelper.Database) {
        createTables(db)
    }

    @VisibleForTesting
    fun onUpgrade(db: DatabaseOpenHelper.Database, oldVersion: Int, newVersion: Int) {
        check(newVersion == DB_VERSION)
        if (oldVersion == DB_VERSION) return

        val migration = migrations[oldVersion to newVersion] ?: defaultDropAllMigration
        try {
            migration.migrate(db)
        } catch (e: SQLException) {
            reportMigrationError(e, "Migration from $oldVersion to $newVersion throws exception")
            defaultDropAllMigration.migrate(db)
        }
    }

    @VisibleForTesting
    @Throws(SQLException::class)
    fun createTables(db: DatabaseOpenHelper.Database) {
        try {
            db.execSQL(CREATE_TABLE_CARDS)
            db.execSQL(CREATE_TABLE_TEMPLATE_REFERENCES)
            db.execSQL(CREATE_TABLE_TEMPLATES)
            db.execSQL(CREATE_TABLE_RAW_JSON)
        } catch (e: SQLException) {
            throw sqlExceptionFactory("Create tables", e)
        }
    }

    @VisibleForTesting
    @Throws(SQLException::class)
    fun dropTables(db: DatabaseOpenHelper.Database) {
        StorageStatementExecutor(sqlExceptionFactory) { db }
                .execute(storageStatements.dropAllTables())
    }

    @AnyThread
    override fun saveData(groupId: String,
                          divs: List<RawDataAndMetadata<T>>,
                          templatesByHash: List<Template<T>>,
                          actionOnError: ActionOnError,
    ) = dataSaveUseCase.saveDivData(
            groupId,
            divs,
            templatesByHash,
            actionOnError,
    )

    @AnyThread
    override fun saveRawJsons(rawJsons: List<RawJson<T>>, actionOnError: ActionOnError) =
        dataSaveUseCase.saveRawJsons(rawJsons, actionOnError)

    @AnyThread
    override fun loadData(
        ids: List<String>,
        idsToExclude: List<String>,
    ): LoadDataResult<DivStorage.RestoredRawData<T>> {
        val usedGroups = mutableSetOf<String>()
        val cards = ArrayList<DivStorage.RestoredRawData<T>>(ids.size)
        val exceptions = mutableListOf<StorageException>()

        val selection = when {
            ids.isEmpty() && idsToExclude.isEmpty() -> null
            ids.isEmpty() -> "$COLUMN_LAYOUT_ID NOT IN ${idsToExclude.asSqlList()}"
            idsToExclude.isEmpty() -> "$COLUMN_LAYOUT_ID IN ${ids.asSqlList()}"
            else -> "$COLUMN_LAYOUT_ID NOT IN ${idsToExclude.asSqlList()} AND $COLUMN_LAYOUT_ID IN ${ids.asSqlList()}"
        }

        try {
            val cardsReadState = readStateFor {
                query(TABLE_CARDS, null, selection, null, null, null, null, null)
            }
            cardsReadState.use {
                val cursor = it.cursor
                if (!cursor.moveToFirst()) {
                    return LoadDataResult(emptyList(), exceptions)
                }

                do {
                    val rawData = cursor.getRestoredRawData(exceptions)
                    if (rawData != null) {
                        cards.add(rawData)
                        usedGroups.add(rawData.groupId)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: SQLException) {
            if (!shouldCatchLoadException(e)) throw e
            exceptions.add(e.toStorageException("Exception on load data from storage"))
        } catch (e: IllegalStateException) {
            exceptions.add(e.toStorageException("Exception on load data from storage"))
        }

        return LoadDataResult(cards, exceptions)
    }

    @AnyThread
    @Throws(SQLException::class)
    override fun remove(predicate: (RawDataAndMetadata<T>) -> Boolean): RemoveResult {
        val ids: Set<String> = collectsRecordsFor(predicate)
        val exceptions = statementExecutor.execute(
                ActionOnError.SKIP_ELEMENT,
                storageStatements.deleteCardsAndTemplates(ids),
                storageStatements.deleteTemplatesWithoutLinksToCards()
        ).errors
        return RemoveResult(ids, exceptions)
    }

    @AnyThread
    override fun removeAllCards(): DivStorageErrorException? {
        return deleteTablesTransaction(actionDesc = "delete all cards", DELETE_CARDS)
    }

    @AnyThread
    override fun readRawJsons(rawJsonIds: Set<String>): LoadDataResult<RawJson<T>> {
        val actionDesc = "Read raw jsons with ids: $rawJsonIds"
        val exceptions = mutableListOf<StorageException>()
        var rawJsons = emptyList<RawJson<T>>()

        try {
            rawJsons = collectsRawJsons(rawJsonIds)
        } catch (e: SQLException) {
            exceptions.add(e.toStorageException(actionDesc))
        } catch (e: IllegalStateException) {
            exceptions.add(e.toStorageException(actionDesc))
        }

        return LoadDataResult(rawJsons, exceptions)
    }

    @AnyThread
    override fun removeRawJsons(predicate: (RawJson<T>) -> Boolean): RemoveResult {
        val ids: Set<String> = collectsRawJsonsIdsFor(predicate)
        val exceptions = statementExecutor.execute(
            ActionOnError.SKIP_ELEMENT,
            storageStatements.deleteRawJsons(ids)
        ).errors
        return RemoveResult(ids, exceptions)
    }

    @AnyThread
    override fun readTemplates(templateHashes: Set<String>): LoadDataResult<RawTemplateData> {
        val actionDesc = "Read templates with hashes: $templateHashes"
        val exceptions = mutableListOf<StorageException>()
        var templates = emptyList<RawTemplateData>()

        try {
            val readState = readStateFor {
                rawQuery("$SELECT_TEMPLATES_BY_HASHES  ${templateHashes.asSqlList()}", emptyArray())
            }

            templates = readState.use {
                it.cursor.getTemplates()
            }
        } catch (e: SQLException) {
            exceptions.add(e.toStorageException(actionDesc))
        } catch (e: IllegalStateException) {
            exceptions.add(e.toStorageException(actionDesc))
        }

        return LoadDataResult(templates, exceptions)
    }

    @AnyThread
    override fun removeAllTemplates(): DivStorageErrorException? {
        return deleteTablesTransaction(
                actionDesc = "Delete all templates",
                DELETE_TEMPLATE_USAGES, DELETE_TEMPLATES,
        )
    }

    @Throws(DivStorageErrorException::class)
    override fun isCardExists(id: String, groupId: String): Boolean {
        var result = false
        val executionResult = statementExecutor.execute(
                storageStatements.isCardExists(cardId = id, groupId) {
                    result = it
                }
        )

        if (!executionResult.isSuccessful && executionResult.errors.isNotEmpty()) {
            throw executionResult.errors.first().toStorageException("Check card exists", id)
        }

        return result
    }

    @Throws(DivStorageErrorException::class)
    override fun isTemplateExists(templateHash: String): Boolean {
        var result = false
        val executionResult = statementExecutor.execute(
                storageStatements.isTemplateExists(templateHash) {
                    result = it
                }
        )

        if (!executionResult.isSuccessful && executionResult.errors.isNotEmpty()) {
            throw executionResult.errors.first().toStorageException("Check template $templateHash exists")
        }

        return result
    }

    override fun readTemplateReferences(): LoadDataResult<DivStorage.TemplateReference> {
        val actionDesc = "Template references"

        return try {
            val readState = readStateFor {
                query(TABLE_TEMPLATE_REFERENCES, null, null, null, null, null, null, null)
            }
            val results = readState.use {
                it.cursor.getTemplateReferences()
            }
            LoadDataResult(results)
        } catch (e: SQLException) {
            LoadDataResult(emptyList(), listOf(e.toStorageException(actionDesc)))
        } catch (e: IllegalStateException) {
            LoadDataResult(emptyList(), listOf(e.toStorageException(actionDesc)))
        }
    }

    // The payload codec defines its parsing exception type; retain per-card error reporting.
    @Suppress("TooGenericExceptionCaught")
    private fun Cursor.getRestoredRawData(
            exceptions: MutableList<StorageException>
    ): DivStorage.RestoredRawData<T>? {
        val id = getString(indexOf(COLUMN_LAYOUT_ID))
        val groupId = getString(indexOf(COLUMN_CARD_GROUP_ID))
        val cardBlob = getBlobOrNull(indexOf(COLUMN_CARD_DATA))
        val metadataBlob = getBlobOrNull(indexOf(COLUMN_CARD_METADATA))

        if (cardBlob == null) {
            val exception = DivStorageErrorException(
                    "DivData is null for card with id $id.", cardId = id)
            exceptions.add(exception)
            return null
        }

        val divData = try {
            cardBlob.decodePayload()
        } catch (e: Exception) {
            exceptions.add(
                    DivStorageErrorException(
                            errorMessage = "DivData is invalid for card with id $id",
                            cause = e,
                            cardId = id)
            )
            null
        }

        val metadata = try {
            metadataBlob?.decodePayload()
        } catch (e: Exception) {
            exceptions.add(
                    DivStorageErrorException(
                            errorMessage = "Metadata is invalid for card with id $id",
                            cause = e,
                            cardId = id)
            )
            null
        }

        if (divData == null) {
            return null
        }

        return DivStorage.RestoredRawData(
                id,
                divData = divData,
                metadata = metadata,
                groupId,
        )
    }

    @Throws(SQLException::class)
    private fun Cursor.getTemplates(): List<RawTemplateData> {
        if (!moveToFirst()) {
            return emptyList()
        }

        val templates = ArrayList<RawTemplateData>()

        do {
            val template = RawTemplateData(
                    hash = getString(indexOf(COLUMN_TEMPLATE_HASH)),
                    data = getBlob(indexOf(COLUMN_TEMPLATE_DATA))
            )
            templates.add(template)
        } while (moveToNext())

        return templates
    }

    @AnyThread
    @Throws(SQLException::class)
    private fun collectsRecordsFor(predicate: (RawDataAndMetadata<T>) -> Boolean): Set<String> {
        val results = mutableSetOf<String>()
        statementExecutor.execute(storageStatements.readData {
            val cursor = it.cursor
            if (!cursor.moveToFirst()) {
                return@readData
            }

            do {
                val rawDataAndMetadata = CursorDrivenRawDataAndMetadata(cursor)
                if (predicate(rawDataAndMetadata)) {
                    results.add(rawDataAndMetadata.id)
                }
                rawDataAndMetadata.close()
            } while (cursor.moveToNext())
        })

        return results
    }

    @AnyThread
    @Throws(SQLException::class)
    private fun collectsRawJsons(rawJsonIds: Set<String>): List<RawJson<T>> {
        val results = ArrayList<RawJson<T>>(rawJsonIds.size)
        readStateFor {
            rawQuery(
                query = "$SELECT_RAW_JSONS_BY_IDS ${rawJsonIds.asSqlList()}",
                selectionArgs = emptyArray()
            )
        }.use {
            val cursor = it.cursor
            if (!cursor.moveToFirst()) {
                return@use
            }

            do {
                val cursorRawJson = CursorDrivenRawJson(cursor)
                val rawJson = with(cursorRawJson) {
                    RawJson.Ready(id, data)
                }
                results.add(rawJson)
                cursorRawJson.close()
            } while (cursor.moveToNext())
        }

        return results
    }

    @AnyThread
    @Throws(SQLException::class)
    private fun collectsRawJsonsIdsFor(predicate: (RawJson<T>) -> Boolean): Set<String> {
        val results = mutableSetOf<String>()
        statementExecutor.execute(storageStatements.readRawJsons {
            val cursor = it.cursor
            if (!cursor.moveToFirst()) {
                return@readRawJsons
            }

            do {
                val rawJson = CursorDrivenRawJson(cursor)
                if (predicate(rawJson)) {
                    results.add(rawJson.id)
                }
                rawJson.close()
            } while (cursor.moveToNext())
        })

        return results
    }

    @AnyThread
    private fun deleteTablesTransaction(
            actionDesc: String, vararg queries: String
    ): DivStorageErrorException? {
        var exception: DivStorageErrorException? = null

        with(openHelper.writableDatabase) {
            val statements = queries.map { compileStatement(it) }

            try {
                beginTransaction()
                statements.forEach { it.executeUpdateDelete() }
                setTransactionSuccessful()
            } catch (e: SQLException) {
                exception = e.toStorageException(actionDesc)
            } finally {
                endTransactionSilently()
                statements.forEach { it.closeSilently() }
                closeSilently()
            }
        }

        return exception
    }

    @Throws(SQLException::class)
    private fun Cursor.getTemplateReferences(): List<DivStorage.TemplateReference> {
        if (!moveToFirst()) {
            return emptyList()
        }

        val templates = ArrayList<DivStorage.TemplateReference>()

        do {
            val template = DivStorage.TemplateReference(
                    groupId = getString(indexOf(COLUMN_GROUP_ID)),
                    templateHash = getString(indexOf(COLUMN_TEMPLATE_HASH)),
                    templateId = getString(indexOf(COLUMN_TEMPLATE_ID)),
            )
            templates.add(template)
        } while (moveToNext())

        return templates
    }

    private fun Exception.toStorageException(
            actionDesc: String, cardId: String? = null
    ): DivStorageErrorException {
        val msg = "Unexpected exception on database access: $actionDesc"
        return DivStorageErrorException(msg, this, cardId)
    }

    private inner class CursorDrivenRawDataAndMetadata(val cursor: Cursor) : RawDataAndMetadata<T>, AutoCloseable {
        private var cursorInvalid = false
        override val id = cursor.getString(cursor.indexOf(COLUMN_LAYOUT_ID))

        override val divData: T by lazy(LazyThreadSafetyMode.NONE) {
            if (cursorInvalid) {
                throw IllegalStateException("Data no longer valid!")
            }
            cursor.getBlob(cursor.indexOf(COLUMN_CARD_DATA)).decodePayload()
        }

        override val metadata: T? by lazy(LazyThreadSafetyMode.NONE) {
            if (cursorInvalid) {
                throw IllegalStateException("Data no longer valid!")
            }
            cursor.getBlobOrNull(cursor.indexOf(COLUMN_CARD_METADATA))?.decodePayload()
        }

        override fun close() {
            cursorInvalid = true
        }
    }

    private inner class CursorDrivenRawJson(val cursor: Cursor) : RawJson<T>, AutoCloseable {
        private var cursorInvalid = false

        override val id: String = cursor.getString(cursor.indexOf(COLUMN_RAW_JSON_ID))

        override val data: T by lazy(LazyThreadSafetyMode.NONE) {
            if (cursorInvalid) {
                throw IllegalStateException("Data no longer valid!")
            }
            cursor.getBlob(cursor.indexOf(COLUMN_RAW_JSON_DATA)).decodePayload()
        }

        override fun close() {
            cursorInvalid = true
        }
    }

    @AnyThread
    private fun readStateFor(func: DatabaseOpenHelper.Database.() -> Cursor): ReadState {
        val db = openHelper.readableDatabase
        return ReadState({ db.closeSilently() }, { db.run(func) })
    }

    private fun Cursor.indexOf(columnName: String): Int {
        val index = this.getColumnIndex(columnName)
        if (index < 0) {
            throw IllegalStateException("Column '$columnName' not found in cursor")
        }

        return index
    }

    private fun ByteArray.decodePayload(): T = codec.decode(this)

    companion object {
        private fun <T> Collection<T>.asSqlList(): String {
            return joinToString(prefix = "('", separator = "', '", postfix = "')")
        }
    }
}

/**
 * Creates storage using the platform database provider and the caller's payload codec.
 * [shouldCatchLoadException] selects SQL errors returned by [DivStorage.loadData]; other SQL errors are rethrown.
 */
@InternalApi
public fun <T : Any> DivStorage(
    codec: StorageCodec<T>,
    openHelperProvider: DatabaseOpenHelperProvider,
    databaseNamePrefix: String = "",
    reportMigrationError: (Throwable, String) -> Unit = { _, _ -> },
    sqlExceptionFactory: (String, Throwable) -> SQLException = ::sqliteException,
    shouldCatchLoadException: (SQLException) -> Boolean = { true },
): DivStorage<T> = DivStorageImpl(
    codec, openHelperProvider, databaseNamePrefix, reportMigrationError, sqlExceptionFactory, shouldCatchLoadException
)
