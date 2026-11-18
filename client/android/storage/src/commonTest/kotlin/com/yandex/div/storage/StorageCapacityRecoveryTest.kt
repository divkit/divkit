package com.yandex.div.storage

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.rawjson.RawJson
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StorageCapacityRecoveryTest {
    private val path = temporaryDatabasePath()
    private var capacityLimited = true
    private val driver = object : SQLiteDriver {
        override fun open(fileName: String): SQLiteConnection {
            val connection = BundledSQLiteDriver().open(fileName)
            connection.execSQL("PRAGMA max_page_count = ${if (capacityLimited) 16 else 10000}")
            return connection
        }
    }
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(driver, path, version, create, upgrade)
    }

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `template storage recovers on the same instance after capacity returns`() {
        BaseDivDatabaseStorage(provider).use { storage ->
            storage.writeTemplates("card", mapOf("committed" to byteArrayOf(1)))
            assertFailsWith<SQLiteException> {
                storage.writeTemplates("card", mapOf("large" to ByteArray(128 * 1024)))
            }
            capacityLimited = false

            storage.writeTemplates("next", mapOf("recovered" to byteArrayOf(2)))

            assertEquals(setOf("committed", "recovered"), storage.readAllTemplates().keys)
            assertContentEquals(byteArrayOf(1), storage.readTemplates("card").getValue("committed"))
        }
    }

    @Test
    fun `raw storage recovers on the same instance after capacity returns`() {
        val storage = DivStorage(StorageCodec.BYTES, provider)
        storage.saveRawJsons(listOf(RawJson("committed", byteArrayOf(1))),
            ActionOnError.ABORT_TRANSACTION)
        val failed = storage.saveRawJsons(listOf(RawJson("large", ByteArray(128 * 1024))),
            ActionOnError.ABORT_TRANSACTION)
        val causes = generateSequence<Throwable>(failed.errors.single()) { it.cause }
            .flatMap { sequenceOf(it) + it.suppressedExceptions.asSequence() }
        assertTrue(causes.any { it.message.orEmpty().contains("database or disk is full") })
        capacityLimited = false

        val result = storage.saveRawJsons(listOf(RawJson("recovered", byteArrayOf(2))),
            ActionOnError.ABORT_TRANSACTION)

        assertTrue(result.isSuccessful)
        assertEquals(setOf("committed", "recovered"), storage.readRawJsons(
            setOf("committed", "large", "recovered")).restoredData.map { it.id }.toSet())
    }

    @Test
    fun `skip element preserves the full disk error when commit also fails`() {
        val storage = DivStorage(StorageCodec.BYTES, provider)

        val result = storage.saveRawJsons(
            listOf(RawJson("large", ByteArray(128 * 1024))), ActionOnError.SKIP_ELEMENT,
        )

        assertTrue(result.errors.single().cause?.message.orEmpty()
            .contains("database or disk is full"))
    }

    @Test
    fun `template write preserves the full disk error when rollback also fails`() {
        BaseDivDatabaseStorage(provider).use { storage ->
            storage.writeTemplates("card", mapOf("committed" to byteArrayOf(1)))

            val error = assertFailsWith<SQLiteException> {
                storage.writeTemplates("card", mapOf("large" to ByteArray(128 * 1024)))
            }

            assertTrue(error.message.orEmpty().contains("database or disk is full"))
        }
    }
}
