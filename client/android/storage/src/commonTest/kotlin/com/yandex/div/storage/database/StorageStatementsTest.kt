package com.yandex.div.storage.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.StorageCodec
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.internal.DivStorageImpl
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.removeDatabase
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.Template
import com.yandex.div.storage.temporaryDatabasePath
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val GROUP_1 = "group#1"
private const val GROUP_2 = "group#2"
private const val TEMPLATE_1 = "template_id:1"
private const val TEMPLATE_2 = "template_id:2"
private const val CARD_1 = "card_id:1"
private const val CARD_2 = "card_id:2"

/**
 * Integration tests for [StorageStatementExecutor.execute] and [StorageStatements].
 */
class StorageStatementsTest {
    private val path = temporaryDatabasePath()
    private val codec = object : StorageCodec<String> {
        override fun encode(value: String): ByteArray = value.encodeToByteArray()
        override fun decode(bytes: ByteArray): String = bytes.decodeToString()
    }
    private val storageStatements = StorageStatements(codec)
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(BundledSQLiteDriver(), path, version, create, upgrade)
    }
    private val divStorage = DivStorageImpl(codec, provider)

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `store and query templates smoke test`() {
        divStorage.statementExecutor.execute(storageStatements.writeTemplates(
                templates = generate(TEMPLATE_1, TEMPLATE_2)))

        val results = divStorage.readTemplates(setOf(TEMPLATE_1, TEMPLATE_2)).restoredData

        assertEquals(2, results.size, "Unexpected size of results: $results")
        assertTrue(results.containsTemplate(TEMPLATE_1))
        assertTrue(results.containsTemplate(TEMPLATE_2))
    }

    @Test
    fun `templates can be stored in different sources`() {
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1)),

                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_2))
        )

        val results1 = divStorage.readTemplates(setOf(TEMPLATE_1)).restoredData
        val results2 = divStorage.readTemplates(setOf(TEMPLATE_2)).restoredData

        assertCollectionEquals(listOf(TEMPLATE_1), results1.ids())
        assertCollectionEquals(listOf(TEMPLATE_2), results2.ids())
    }

    private fun replaceCardsStatement(id: String, groupId: String): StorageStatement {
        val jsonObject = "{}"
        return storageStatements.replaceCards(
                groupId = groupId,
                cards = listOf(
                        RawDataAndMetadata.Ready(id, jsonObject, jsonObject)
                )
        )
    }

    @Test
    fun `templates without corresponding cards are deleted`() {
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1, TEMPLATE_2)),
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1, TEMPLATE_2)),
                storageStatements.deleteTemplatesWithoutLinksToCards(),
        )

        val cardTemplates = divStorage.readTemplates(setOf(GROUP_1)).restoredData +
                divStorage.readTemplates(setOf(GROUP_2)).restoredData


        assertCollectionEquals(emptyList(), cardTemplates.ids())
    }

    @Test
    fun `templates with unused groups are deleted`() {
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1)),
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_2)),
                storageStatements.writeTemplatesUsages(GROUP_1, generate(TEMPLATE_1)),
                replaceCardsStatement(CARD_1, groupId = GROUP_1),
                storageStatements.deleteTemplatesWithoutLinksToCards(),
        )

        val cardTemplates = divStorage.readTemplates(setOf(TEMPLATE_1)).restoredData +
                divStorage.readTemplates(setOf(TEMPLATE_2)).restoredData

        assertCollectionEquals(listOf(TEMPLATE_1), cardTemplates.ids())
    }

    @Test
    fun `no templates deleted when all groups are used`() {
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1)),
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_2)),
                replaceCardsStatement(CARD_1, groupId = GROUP_1),
                replaceCardsStatement(CARD_2, groupId = GROUP_2),
                storageStatements.writeTemplatesUsages(GROUP_1, generate(TEMPLATE_1)),
                storageStatements.writeTemplatesUsages(GROUP_2, generate(TEMPLATE_2)),
                storageStatements.deleteTemplatesWithoutLinksToCards(),
        )

        val cardTemplates = divStorage.readTemplates(setOf(TEMPLATE_1)).restoredData +
                divStorage.readTemplates(setOf(TEMPLATE_2)).restoredData

        assertCollectionEquals(listOf(TEMPLATE_1, TEMPLATE_2), cardTemplates.ids())
    }

    @Test
    fun `deleteTemplatesWithoutLinksToCards also terminates template usages table`() {
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(generate(TEMPLATE_1, TEMPLATE_2)),
                replaceCardsStatement(CARD_1, groupId = GROUP_1),

                storageStatements.writeTemplatesUsages(
                        GROUP_1, listOf(Template(TEMPLATE_1, TEMPLATE_1, "{}"))
                ),

                storageStatements.writeTemplatesUsages(
                        GROUP_2, listOf(Template(TEMPLATE_2, TEMPLATE_2, "{}"))
                ),
                storageStatements.deleteTemplatesWithoutLinksToCards(),
        )

        assertEquals(1, divStorage.statementExecutor.readTemplatesUsagesSize())
    }

    @Test
    fun `isTemplateExists with existing template`() {
        var exists: Boolean? = null
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1, TEMPLATE_2)),
                storageStatements.isTemplateExists(TEMPLATE_1) {
                    exists = it
                }
        )

        assertEquals(true, exists)
    }

    @Test
    fun `isTemplateExists with non-existent template`() {
        var exists: Boolean? = null
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1)),
                storageStatements.isTemplateExists(TEMPLATE_2) {
                    exists = it
                }
        )

        assertEquals(false, exists)
    }

    @Test
    fun `isTemplateExists with non-existent source`() {
        var exists: Boolean? = null
        divStorage.statementExecutor.execute(
                storageStatements.writeTemplates(
                        templates = generate(TEMPLATE_1)),
                storageStatements.isTemplateExists(TEMPLATE_2) {
                    exists = it
                }
        )

        assertEquals(false, exists)
    }
}

private fun StorageStatementExecutor.readTemplatesUsagesSize(): Int {
    var count = -1
    this.execute(StorageStatement { c ->
        c.compileQuery("SELECT * FROM $TABLE_TEMPLATE_REFERENCES").use {
            val cursor = it.cursor
            count = 0
            while (cursor.moveToNext()) count++
        }
    })

    return count
}

private data class TemplateUsage(
    val templateId: String,
    val cardId: String,
)

private fun StorageStatementExecutor.readTemplatesUsages(cardId: String, responseId: String): List<TemplateUsage> {
    val results = mutableMapOf<String, String>()
    this.execute(StorageStatement { c ->
        val sql = """
            SELECT * FROM $TABLE_TEMPLATE_REFERENCES
            WHERE $COLUMN_CARD_ID = '$cardId' AND $COLUMN_GROUP_ID = '$responseId'
        """.trimIndent()
        c.compileQuery(sql).storeTo(results) {
            val cardId = getString(getColumnIndex(COLUMN_CARD_ID))
            val templateId = getString(getColumnIndex(COLUMN_TEMPLATE_ID))
            templateId to cardId
        }
    })

    return results.map { (t, c) -> TemplateUsage(t, c) }
}


private inline fun <reified T> assertCollectionEquals(
        expected: Collection<T>, actual: Collection<T>
) {
    assertContentEquals(expected.toTypedArray(), actual.toTypedArray(), "Want: $expected Got: $actual")
}

private fun Collection<RawTemplateData>.ids() = map { it.hash }

private fun Collection<RawTemplateData>.containsTemplate(id: String): Boolean {
    return ids().contains(id)
}

private fun generate(vararg templateIds: String): List<Template<String>> {
    return templateIds.map {
        Template(
            template = """ { "template_id": "$it" } """,
            id = it,
            hash = it,
        )
    }
}

private fun <K, V> ReadState.storeTo(results: MutableMap<K, V>, mapper: Cursor.() -> Pair<K, V>) {
    val capturedCursor = cursor

    if (!capturedCursor.moveToFirst()) {
        return
    }

    do {
        val pair = mapper(capturedCursor)
        results[pair.first] = pair.second
    } while (capturedCursor.moveToNext())
}
