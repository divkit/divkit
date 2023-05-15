package com.yandex.div.storage

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.core.database.getBlobOrNull
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.KAssert
import com.yandex.div.storage.DivDataRepository.ActionOnError
import com.yandex.div.storage.DivStorage.LoadDataResult
import com.yandex.div.storage.DivStorage.RemoveResult
import com.yandex.div.storage.database.COLUMN_CARD_DATA
import com.yandex.div.storage.database.COLUMN_CARD_GROUP_ID
import com.yandex.div.storage.database.COLUMN_CARD_METADATA
import com.yandex.div.storage.database.COLUMN_GROUP_ID
import com.yandex.div.storage.database.COLUMN_LAYOUT_ID
import com.yandex.div.storage.database.COLUMN_TEMPLATE_DATA
import com.yandex.div.storage.database.COLUMN_TEMPLATE_HASH
import com.yandex.div.storage.database.COLUMN_TEMPLATE_ID
import com.yandex.div.storage.database.CREATE_TABLE_CARDS
import com.yandex.div.storage.database.CREATE_TABLE_TEMPLATES
import com.yandex.div.storage.database.CREATE_TABLE_TEMPLATE_REFERENCES
import com.yandex.div.storage.database.DB_VERSION
import com.yandex.div.storage.database.DELETE_CARDS
import com.yandex.div.storage.database.DELETE_TEMPLATES
import com.yandex.div.storage.database.DELETE_TEMPLATE_USAGES
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.database.ReadState
import com.yandex.div.storage.database.SELECT_TEMPLATES_BY_HASHES
import com.yandex.div.storage.database.SingleTransactionCardSavePerformer
import com.yandex.div.storage.database.StorageStatementExecutor
import com.yandex.div.storage.database.StorageStatements
import com.yandex.div.storage.database.TABLE_CARDS
import com.yandex.div.storage.database.TABLE_TEMPLATE_REFERENCES
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.Template
import com.yandex.div.storage.util.closeSilently
import com.yandex.div.storage.util.endTransactionSilently
import org.json.JSONException
import org.json.JSONObject
import java.io.Closeable
import java.nio.charset.StandardCharsets

private const val DB_NAME = "div-storage.db"

@Mockable
internal class DivStorageImpl(
        context: Context,
        openHelperProvider: DatabaseOpenHelperProvider,
) : DivStorage {

    private val openHelper = openHelperProvider.provide(
            context, DB_NAME, DB_VERSION, this::onCreate, this::onUpgrade
    )
    @VisibleForTesting  // TODO: ticket for make a private pls
    val statementExecutor: StorageStatementExecutor = StorageStatementExecutor {
        openHelper.writableDatabase
    }
    private val cardSaveUseCase = SingleTransactionCardSavePerformer(statementExecutor)


    @VisibleForTesting
    fun onCreate(db: DatabaseOpenHelper.Database) {
        createTables(db)
    }

    @VisibleForTesting
    fun onUpgrade(db: DatabaseOpenHelper.Database, oldVersion: Int, newVersion: Int) {
        KAssert.assertEquals(newVersion, DB_VERSION)
        if (oldVersion == DB_VERSION) return

        // recreate schema and delete all data
        dropTables(db)
        createTables(db)
    }

    @VisibleForTesting
    @Throws(SQLException::class)
    fun createTables(db: DatabaseOpenHelper.Database) {
        try {
            db.execSQL(CREATE_TABLE_CARDS)
            db.execSQL(CREATE_TABLE_TEMPLATE_REFERENCES)
            db.execSQL(CREATE_TABLE_TEMPLATES)
        } catch (e: SQLException) {
            throw SQLException("Create tables", e)
        }
    }

    @VisibleForTesting
    @Throws(SQLException::class)
    fun dropTables(db: DatabaseOpenHelper.Database) {
        StorageStatementExecutor { db }
                .execute(StorageStatements.dropAllTables())
    }

    override fun saveData(groupId: String,
                          divs: List<RawDataAndMetadata>,
                          templatesByHash: List<Template>,
                          actionOnError: ActionOnError,
    ) = cardSaveUseCase.save(
            groupId,
            divs,
            templatesByHash,
            actionOnError,
    )

    override fun loadData(ids: List<String>): LoadDataResult<DivStorage.RestoredRawData> {
        val usedGroups = mutableSetOf<String>()
        val cards = ArrayList<DivStorage.RestoredRawData>(ids.size)
        val exceptions = mutableListOf<StorageException>()

        val selection = when {
            ids.isEmpty() -> null
            else -> "$COLUMN_LAYOUT_ID IN ${ids.asSqlList()}"
        }
        val cardsReadState = readStateFor {
            query(TABLE_CARDS, null, selection, null, null, null, null, null)
        }
        cardsReadState.use {
            val cursor = it.cursor

            if (cursor.count == 0 || !cursor.moveToFirst()) {
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

        return LoadDataResult(cards, exceptions)
    }

    override fun remove(predicate: (RawDataAndMetadata) -> Boolean): RemoveResult {
        val ids: Set<String> = collectsRecordsFor(predicate)
        val exceptions = statementExecutor.execute(
                ActionOnError.SKIP_ELEMENT,
                StorageStatements.deleteCardsAndTemplates(ids),
                StorageStatements.deleteTemplatesWithoutLinksToCards()
        ).errors
        return RemoveResult(ids, exceptions)
    }

    @WorkerThread
    override fun removeAllCards(): DivStorageErrorException? {
        return deleteTablesTransaction(actionDesc = "delete all cards", DELETE_CARDS)
    }

    @WorkerThread
    override fun readTemplates(templateHashes: Set<String>): LoadDataResult<RawTemplateData> {
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
            val actionDesc = "Read templates with hashes: $templateHashes"
            exceptions.add(e.toStorageException(actionDesc))
        }

        return LoadDataResult(templates, exceptions)
    }

    @WorkerThread
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
                StorageStatements.isCardExists(cardId = id, groupId) {
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
                StorageStatements.isTemplateExists(templateHash) {
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

        try {
            val readState = readStateFor {
                query(TABLE_TEMPLATE_REFERENCES, null, null, null, null, null, null, null)
            }
            val results = readState.use {
                it.cursor.getTemplateReferences()
            }
            return LoadDataResult(results)
        } catch (e: SQLException) {
            return LoadDataResult(emptyList(), listOf(e.toStorageException(actionDesc)))
        }

    }

    private fun Cursor.getRestoredRawData(
            exceptions: MutableList<StorageException>
    ): DivStorage.RestoredRawData? {
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
            cardBlob.toJSONObject()
        } catch (e: JSONException) {
            exceptions.add(
                    DivStorageErrorException(
                            errorMessage = "DivData is invalid for card with id $id",
                            cause = e,
                            cardId = id)
            )
            null
        }

        val metadata = try {
            metadataBlob?.toJSONObject()
        } catch (e: JSONException) {
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
        if (count == 0 || !moveToFirst()) {
            return emptyList()
        }

        val templates = ArrayList<RawTemplateData>(count)

        do {
            val template = RawTemplateData(
                    hash = getString(indexOf(COLUMN_TEMPLATE_HASH)),
                    data = getBlob(indexOf(COLUMN_TEMPLATE_DATA))
            )
            templates.add(template)
        } while (moveToNext())

        return templates
    }

    private fun collectsRecordsFor(predicate: (RawDataAndMetadata) -> Boolean): Set<String> {
        val results = mutableSetOf<String>()
        statementExecutor.execute(StorageStatements.readData {
            val cursor = it.cursor
            if (cursor.count == 0 || !cursor.moveToFirst()) {
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

    @WorkerThread
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
        if (count == 0 || !moveToFirst()) {
            return emptyList()
        }

        val templates = ArrayList<DivStorage.TemplateReference>(count)

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

    private inner class CursorDrivenRawDataAndMetadata(val cursor: Cursor) : RawDataAndMetadata, Closeable {
        private var cursorInvalid = false
        override val id = cursor.getString(cursor.indexOf(COLUMN_LAYOUT_ID))!!

        override val divData: JSONObject by lazy(LazyThreadSafetyMode.NONE) {
            if (cursorInvalid) {
                throw IllegalStateException("Data no longer valid!")
            }
            cursor.getBlob(cursor.indexOf(COLUMN_CARD_DATA)).toJSONObject()
        }

        override val metadata: JSONObject? by lazy(LazyThreadSafetyMode.NONE) {
            if (cursorInvalid) {
                throw IllegalStateException("Data no longer valid!")
            }
            cursor.getBlobOrNull(cursor.indexOf(COLUMN_CARD_METADATA))?.toJSONObject()
        }

        override fun close() {
            cursorInvalid = true
        }
    }

    @WorkerThread
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

    private fun ByteArray.toJSONObject() = JSONObject(this.toString(charset = StandardCharsets.UTF_8))

    companion object {
        private fun <T> Collection<T>.asSqlList(): String {
            return joinToString(prefix = "('", separator = "', '", postfix = "')")
        }
    }
}
