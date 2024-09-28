package com.yandex.div.storage.database

import android.database.SQLException
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.div.storage.rawjson.RawJson
import com.yandex.div.storage.templates.Template
import com.yandex.div.storage.util.bindNullableBlob

/**
 * Repository of statements for [com.yandex.div.storage.StorageStatementExecutor.execute].
 */
internal object StorageStatements {

    fun writeTemplates(templates: List<Template>) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val insertStatement = compiler.compileStatement(INSERT_TEMPLATE)
            templates.forEach {
                insertStatement.bindString(1, it.hash)
                insertStatement.bindBlob(2, it.template.toString().toByteArray())
                insertStatement.executeInsert()
            }
        }

        override fun toString(): String {
            return "Write templates ${templates.joinToString { "${it.id}/${it.hash}" }}"
        }
    }

    fun replaceRawJsons(
        rawJsons: List<RawJson>,
        onFailedTransactions: ((List<String>) -> Unit) = { failedTransactions ->
            throw SQLException("Insertion failed for raw jsons with ids: " +
                    failedTransactions.joinToString())
        },
    ) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val failedTransactions = mutableListOf<String>()
            val replaceStatement = compiler.compileStatement(REPLACE_RAW_JSON)
            rawJsons.forEach { json ->
                with(replaceStatement) {
                    bindString(1, json.id)
                    bindBlob(2, json.data.toString().toByteArray())

                    executeInsert().takeIf { id -> id < 0 }?.let { failedTransactions.add(json.id) }
                }
            }
            if (failedTransactions.isNotEmpty()) {
                onFailedTransactions(failedTransactions)
            }
        }

        override fun toString(): String {
            return "Replace raw jsons ($cardIdsString)"
        }

        private val cardIdsString by lazy(LazyThreadSafetyMode.NONE) {
            rawJsons.joinToString { it.id }
        }
    }

    fun replaceCards(
        groupId: String,
        cards: List<RawDataAndMetadata>,
        onFailedTransactions: ((List<String>) -> Unit) = { failedTransactions ->
            throw SQLException("Insertion failed for cards with ids: " +
                    failedTransactions.joinToString())
        },
    ) = object : StorageStatement {
        private val cardIdsString by lazy(LazyThreadSafetyMode.NONE) {
            cards.joinToString { it.id }
        }

        override fun execute(compiler: SqlCompiler) {
            val failedTransactions = mutableListOf<String>()
            val replaceCardStatement = compiler.compileStatement(REPLACE_CARD)
            cards.forEach { dataAndMetadata ->
                val cardId = dataAndMetadata.id
                val divData = dataAndMetadata.divData.toString().toByteArray()
                val metaBlob = dataAndMetadata.metadata?.toString()?.toByteArray()
                var c = 1
                replaceCardStatement.bindString(c++, cardId)
                replaceCardStatement.bindNullableBlob(c++, divData)
                replaceCardStatement.bindNullableBlob(c++, metaBlob)
                replaceCardStatement.bindString(c++, groupId)
                val id = replaceCardStatement.executeInsert()
                if (id < 0) {
                    failedTransactions.add(cardId)
                }
            }

            if (failedTransactions.isNotEmpty()) {
                onFailedTransactions(failedTransactions)
            }
        }

        override fun toString(): String {
            return "Replace cards ($cardIdsString)}"
        }
    }

    fun deleteTemplatesWithoutLinksToCards() = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            compiler.compileStatement(DELETE_UNUSED_TEMPLATE_REFERENCES).executeUpdateDelete()
            compiler.compileStatement(DELETE_UNUSED_TEMPLATES).executeUpdateDelete()
        }

        override fun toString(): String {
            return "Deleting unused templates"
        }
    }

    fun deleteCardsAndTemplates(elementIds: Set<String>) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val deleteCardsStatement = compiler.compileStatement(
                    "$DELETE_CARDS_IDS ${elementIds.asSqlList()}")
            val deleteTemplateUsagesStatement = compiler.compileStatement(
                    "$DELETE_TEMPLATE_USAGES_BY_CARD_IDS ${elementIds.asSqlList()}")

            deleteCardsStatement.executeUpdateDelete()
            deleteTemplateUsagesStatement.executeUpdateDelete()
        }

        override fun toString(): String {
            return "Deleting cards with ids: $elementIds"
        }
    }

    fun deleteRawJsons(elementIds: Set<String>) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            compiler.compileStatement(
                "$DELETE_RAW_JSON_BY_IDS ${elementIds.asSqlList()}"
            ).executeUpdateDelete()
        }

        override fun toString(): String {
            return "Deleting raw jsons with ids: $elementIds"
        }
    }

    private fun <T> Collection<T>.asSqlList(): String {
        return joinToString(prefix = "('", separator = "', '", postfix = "')")
    }

    fun isTemplateExists(templateHash: String,
                         result: (exists: Boolean) -> Unit) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val state = compiler.compileQuery(
                    "SELECT 1 FROM $TABLE_TEMPLATES "+
                    "WHERE $COLUMN_TEMPLATE_HASH == '$templateHash' "
            )
            state.use {
                result.invoke(it.cursor.count > 0)
            }
        }

        override fun toString(): String {
            return "Check template '$templateHash' exists in group"
        }
    }

    fun isCardExists(cardId: String, groupId: String,
                     result: (exists: Boolean) -> Unit) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val state = compiler.compileQuery(
                    "SELECT 1 FROM $TABLE_CARDS "+
                    "WHERE $COLUMN_LAYOUT_ID == '$cardId' "+
                    "AND $COLUMN_GROUP_ID == '$groupId'"
            )
            state.use {
                result.invoke(it.cursor.count > 0)
            }
        }

        override fun toString(): String {
            return "Check card '$cardId' with group '$groupId' exists"
        }
    }

    fun readData(reader: (r: ReadState) -> Unit) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val readState = compiler.compileQuery("SELECT * FROM $TABLE_CARDS")
            readState.use(reader)
        }

        override fun toString(): String {
            return "Selecting all div data"
        }
    }

    fun readRawJsons(reader: (r: ReadState) -> Unit) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val readState = compiler.compileQuery("SELECT * FROM $TABLE_RAW_JSON")
            readState.use(reader)
        }

        override fun toString(): String {
            return "Selecting all raw jsons"
        }
    }

    fun writeTemplatesUsages(groupId: String,
                             templates: List<Template>,
    ) = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val insertUsageStatement = compiler.compileStatement(INSERT_TEMPLATE_USAGE)

            templates.forEach { template ->
                insertUsageStatement.bindString(1, groupId)
                insertUsageStatement.bindString(2, template.id)
                insertUsageStatement.bindString(3, template.hash)
                insertUsageStatement.executeInsert()
            }
        }

        override fun toString(): String {
            return "Write template usages for $groupId"
        }
    }

    fun dropAllTables() = object : StorageStatement {
        override fun execute(compiler: SqlCompiler) {
            val tableNames = mutableListOf<String>()
            compiler.compileQuery("SELECT name FROM sqlite_master WHERE type='table'").use {
                val cursor = it.cursor

                if (!cursor.moveToFirst()) {
                    return
                }
                do {
                    tableNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                } while (cursor.moveToNext())
            }

            tableNames.forEach {
                compiler.compileStatement("DROP TABLE IF EXISTS $it").execute()
            }
        }

        override fun toString(): String {
            return "Drop all database tables"
        }
    }
}
