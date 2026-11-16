package com.yandex.div.storage

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.Template
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DivStoragePersistenceTest {
    private val path = temporaryDatabasePath()
    private val driver = BundledSQLiteDriver()
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(driver, path, version, create, upgrade)
    }
    private val storage = createStorage()

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `raw value survives recreating storage`() {
        storage.saveRawJsons(
            listOf(RawJson("stored_value_name", byteArrayOf(0, 1, -1))),
            ActionOnError.ABORT_TRANSACTION,
        )

        val result = createStorage().readRawJsons(setOf("stored_value_name"))

        assertTrue(result.errors.isEmpty())
        assertContentEquals(byteArrayOf(0, 1, -1), result.restoredData.single().data)
    }

    @Test
    fun `v2 migration preserves existing card data`() {
        driver.open(path).use {
            it.execSQL("CREATE TABLE cards(layout_id TEXT PRIMARY KEY, card_data BLOB, " +
                "metadata BLOB, group_id TEXT NOT NULL)")
            it.execSQL("CREATE TABLE templates(template_hash TEXT PRIMARY KEY, template_data BLOB)")
            it.execSQL("CREATE TABLE template_references(group_id TEXT, template_id TEXT, " +
                "template_hash TEXT, PRIMARY KEY(group_id, template_id))")
            it.execSQL("INSERT INTO cards VALUES ('old', X'7B7D', NULL, 'group')")
            it.execSQL("PRAGMA user_version = 2")
        }

        val result = storage.saveRawJsons(
            listOf(RawJson("value", byteArrayOf(1))), ActionOnError.ABORT_TRANSACTION
        )

        assertTrue(result.isSuccessful)
        assertContentEquals(
            byteArrayOf(123, 125), storage.loadData(listOf("old"), emptyList()).restoredData.single().divData
        )
        assertContentEquals(byteArrayOf(1), storage.readRawJsons(setOf("value")).restoredData.single().data)
    }


    @Test
    fun `failed card statement rolls back cards and template references`() {
        rejectSecondCard()

        val result = storage.saveData(
            "group",
            listOf(RawDataAndMetadata("first", byteArrayOf(1)), RawDataAndMetadata("second", byteArrayOf(2))),
            listOf(Template("template", "hash", byteArrayOf(3))),
            ActionOnError.ABORT_TRANSACTION,
        )

        assertFalse(result.isSuccessful)
        assertTrue(storage.loadData(emptyList(), emptyList()).restoredData.isEmpty())
        assertTrue(storage.readTemplateReferences().restoredData.isEmpty())
    }

    @Test
    fun `skip element retains the successful part of a statement and continues`() {
        rejectSecondCard()

        val result = storage.saveData(
            "group",
            listOf(RawDataAndMetadata("first", byteArrayOf(1)), RawDataAndMetadata("second", byteArrayOf(2))),
            listOf(Template("template", "hash", byteArrayOf(3))),
            ActionOnError.SKIP_ELEMENT,
        )

        assertFalse(result.isSuccessful)
        assertEquals(listOf("first"), storage.loadData(emptyList(), emptyList()).restoredData.map { it.id })
        assertTrue(storage.isTemplateExists("hash"))
    }

    @Test
    fun `id based removal does not decode the payload`() {
        val codec = object : StorageCodec<String> {
            override fun encode(value: String): ByteArray = value.encodeToByteArray()
            override fun decode(bytes: ByteArray): String = error("Invalid payload")
        }
        val textStorage = DivStorage(codec, provider)
        textStorage.saveData(
            "group", listOf(RawDataAndMetadata("card", "invalid")), emptyList(),
            ActionOnError.ABORT_TRANSACTION,
        )

        val result = textStorage.remove { it.id == "card" }

        assertEquals(setOf("card"), result.ids)
        assertTrue(result.errors.isEmpty())
        assertTrue(textStorage.loadData(emptyList(), emptyList()).restoredData.isEmpty())
    }

    @Test
    fun `failed transaction start is returned as a storage error`() {
        storage.loadData(emptyList(), emptyList())
        driver.open(path).use { other ->
            other.execSQL("BEGIN IMMEDIATE TRANSACTION")
            try {
                val result = storage.saveRawJsons(
                    listOf(RawJson("value", byteArrayOf(1))), ActionOnError.ABORT_TRANSACTION
                )

                assertFalse(result.isSuccessful)
            } finally {
                other.execSQL("ROLLBACK")
            }
        }
    }

    @Test
    fun `predicate can read from the same storage`() {
        storage.saveData("group", listOf(RawDataAndMetadata("card", byteArrayOf(1))),
            emptyList(), ActionOnError.ABORT_TRANSACTION)

        val result = storage.remove {
            val nested = storage.loadData(listOf(it.id), emptyList())
            assertTrue(nested.errors.isEmpty())
            nested.restoredData.isNotEmpty()
        }

        assertTrue(result.errors.isEmpty())
        assertEquals(setOf("card"), result.ids)
    }

    @Test
    fun `predicate can check existence through a nested transaction`() {
        storage.saveData("group", listOf(RawDataAndMetadata("card", byteArrayOf(1))),
            emptyList(), ActionOnError.ABORT_TRANSACTION)

        val result = storage.remove { storage.isCardExists(it.id, "group") }

        assertTrue(result.errors.isEmpty())
        assertEquals(setOf("card"), result.ids)
    }

    @Test
    fun `successful nested transactions commit together`() {
        assertEquals("2", nestedTransactionRows(childSuccessful = true, parentSuccessful = true))
    }

    @Test
    fun `successful child does not commit an unsuccessful parent`() {
        assertEquals("0", nestedTransactionRows(childSuccessful = true, parentSuccessful = false))
    }

    @Test
    fun `unsuccessful child prevents the parent commit`() {
        assertEquals("0", nestedTransactionRows(childSuccessful = false, parentSuccessful = true))
    }

    private fun nestedTransactionRows(childSuccessful: Boolean, parentSuccessful: Boolean): String {
        val helper = SQLiteDatabaseOpenHelper(driver, path, 1,
            { it.execSQL("CREATE TABLE nested_data(value INTEGER)") }, { _, _, _ -> })
        helper.writableDatabase.use { outer ->
            outer.beginTransaction()
            outer.execSQL("INSERT INTO nested_data VALUES (1)")
            helper.writableDatabase.use { inner ->
                inner.beginTransaction()
                inner.execSQL("INSERT INTO nested_data VALUES (2)")
                if (childSuccessful) inner.setTransactionSuccessful()
                inner.endTransaction()
            }
            if (parentSuccessful) outer.setTransactionSuccessful()
            outer.endTransaction()
            return outer.rawQuery("SELECT COUNT(*) FROM nested_data", null).use {
                assertTrue(it.moveToFirst())
                it.getString(0)
            }
        }
    }

    private fun createStorage(): DivStorage<ByteArray> = DivStorage(StorageCodec.BYTES, provider)

    private fun rejectSecondCard() {
        storage.loadData(emptyList(), emptyList())
        driver.open(path).use {
            it.execSQL("CREATE TRIGGER reject_card BEFORE INSERT ON cards " +
                "WHEN NEW.layout_id = 'second' BEGIN SELECT RAISE(ABORT, 'rejected'); END")
        }
    }
}
