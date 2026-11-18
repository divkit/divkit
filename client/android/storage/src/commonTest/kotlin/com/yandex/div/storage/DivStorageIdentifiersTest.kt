package com.yandex.div.storage

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.Template
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DivStorageIdentifiersTest {
    private val path = temporaryDatabasePath()
    private val storage = DivStorage(StorageCodec.BYTES, { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(BundledSQLiteDriver(), path, version, create, upgrade)
    })

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `quoted card ids are selected literally`() {
        saveCards()

        val result = storage.loadData(listOf(QUOTED), emptyList())

        assertTrue(result.errors.isEmpty())
        assertEquals(listOf(QUOTED), result.restoredData.map { it.id })
    }

    @Test
    fun `quoted card ids are excluded literally`() {
        saveCards()

        val result = storage.loadData(emptyList(), listOf(QUOTED))

        assertTrue(result.errors.isEmpty())
        assertEquals(listOf("other"), result.restoredData.map { it.id })
    }

    @Test
    fun `card existence binds id and group separately`() {
        saveCards()

        assertTrue(storage.isCardExists(QUOTED, QUOTED))
        assertFalse(storage.isCardExists(QUOTED, "other"))
    }

    @Test
    fun `quoted template hashes can be read and checked`() {
        saveCards()

        val result = storage.readTemplates(setOf(QUOTED))

        assertTrue(result.errors.isEmpty())
        assertEquals(listOf(QUOTED), result.restoredData.map { it.hash })
        assertTrue(storage.isTemplateExists(QUOTED))
    }

    @Test
    fun `quoted raw ids are read and deleted literally`() {
        storage.saveRawJsons(
            listOf(RawJson(QUOTED, byteArrayOf(1)), RawJson("other", byteArrayOf(2))),
            ActionOnError.ABORT_TRANSACTION,
        )

        val read = storage.readRawJsons(setOf(QUOTED))
        assertTrue(read.errors.isEmpty())
        assertEquals(listOf(QUOTED), read.restoredData.map { it.id })
        val removed = storage.removeRawJsons { it.id == QUOTED }
        assertTrue(removed.errors.isEmpty())
        assertEquals(listOf("other"), storage.readRawJsons(setOf(QUOTED, "other"))
            .restoredData.map { it.id })
    }

    @Test
    fun `raw ids containing zero characters can be selected and removed`() {
        val ids = listOf("a\u0000b", "\u0000", "quoted'\u0000tail")
        storage.saveRawJsons(ids.map { RawJson(it, byteArrayOf(1)) }, ActionOnError.ABORT_TRANSACTION)

        val read = storage.readRawJsons(ids.toSet())
        assertTrue(read.errors.isEmpty())
        assertEquals(ids.toSet(), read.restoredData.map { it.id }.toSet())
        val removed = storage.removeRawJsons { it.id in ids }
        assertTrue(removed.errors.isEmpty())
        assertEquals(ids.toSet(), removed.ids)
        assertTrue(storage.readRawJsons(ids.toSet()).restoredData.isEmpty())
    }

    @Test
    fun `false removal predicate preserves a raw value with an empty id`() {
        storage.saveRawJsons(listOf(RawJson("", byteArrayOf(1))), ActionOnError.ABORT_TRANSACTION)

        val result = storage.removeRawJsons { false }

        assertTrue(result.ids.isEmpty())
        assertTrue(result.errors.isEmpty())
        assertEquals(listOf(""), storage.readRawJsons(setOf("")).restoredData.map { it.id })
    }

    @Test
    fun `removing a card preserves an unrelated group with the same name`() {
        storage.saveData("group-a", listOf(RawDataAndMetadata("group-b", byteArrayOf(1))),
            listOf(Template("template-a", "hash-a", byteArrayOf(1))), ActionOnError.ABORT_TRANSACTION)
        storage.saveData("group-b", listOf(RawDataAndMetadata("card-b", byteArrayOf(2))),
            listOf(Template("template-b", "hash-b", byteArrayOf(2))), ActionOnError.ABORT_TRANSACTION)

        val removed = storage.remove { it.id == "group-b" }

        assertTrue(removed.errors.isEmpty())
        assertEquals(listOf("card-b"), storage.loadData(emptyList(), emptyList()).restoredData.map { it.id })
        assertTrue(storage.isTemplateExists("hash-b"))
    }

    private fun saveCards() {
        assertTrue(storage.saveData(
            QUOTED,
            listOf(RawDataAndMetadata(QUOTED, byteArrayOf(1)), RawDataAndMetadata("other", byteArrayOf(2))),
            listOf(Template("template", QUOTED, byteArrayOf(1))),
            ActionOnError.ABORT_TRANSACTION,
        ).isSuccessful)
    }

    private companion object {
        const val QUOTED = "quoted') OR 1=1 --"
    }
}
