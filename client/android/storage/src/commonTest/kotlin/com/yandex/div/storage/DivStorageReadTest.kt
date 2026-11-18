package com.yandex.div.storage

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.Template
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DivStorageReadTest {
    private val path = temporaryDatabasePath()
    private val driver = CountingDriver()
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(driver, path, version, create, upgrade)
    }
    private val storage = DivStorage(StorageCodec.BYTES, provider)
    private val manyIds = (0..1800).map { "id-$it" }

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `single query reads do not start transactions`() {
        saveCards(listOf("id-0"))

        assertTrue(storage.loadData(listOf("id-0"), emptyList()).errors.isEmpty())
        assertTrue(storage.readTemplates(setOf("id-0")).errors.isEmpty())
        assertTrue(storage.readRawJsons(setOf("id-0")).errors.isEmpty())

        assertEquals(0, driver.transactionCount)
    }

    @Test
    fun `ordinary exclusions are filtered before reaching the cursor`() {
        saveCards((0..19).map { "id-$it" })

        val result = storage.loadData(emptyList(), (0..18).map { "id-$it" })

        assertEquals(listOf("id-19"), result.restoredData.map { it.id })
        assertEquals(1, driver.rowsRead)
        assertEquals(1, driver.openCount)
    }

    @Test
    fun `explicit ids excluded completely do not become an unfiltered read`() {
        saveCards(listOf("excluded", "other"))

        val result = storage.loadData(listOf("excluded"), listOf("excluded"))

        assertTrue(result.restoredData.isEmpty())
        assertEquals(0, driver.rowsRead)
        assertEquals(1, driver.openCount)
    }

    @Test
    fun `fully excluded explicit ids still report an acquisition error`() {
        driver.failOpen = true

        val result = storage.loadData(listOf("excluded"), listOf("excluded"))

        assertEquals(1, result.errors.size)
        assertEquals(1, driver.openCount)
    }

    @Test
    fun `large exclusions remain bounded and exclude their literal ids`() {
        val excluded = (0..1000).map { "quoted'\u0000$it" }
        saveCards(excluded + "retained")

        val result = storage.loadData(emptyList(), excluded)

        assertTrue(result.errors.isEmpty())
        assertEquals(listOf("retained"), result.restoredData.map { it.id })
        assertTrue(driver.maxBindingIndex in 1..900)
        assertEquals(1, driver.openCount)
    }

    @Test
    fun `card batches share one database acquisition`() {
        val expectedIds = listOf("id-0", "id-900", "id-1800")
        saveCards(expectedIds)

        val result = storage.loadData(manyIds, emptyList())

        assertEquals(expectedIds.sorted(), result.restoredData.map { it.id }.sorted())
        assertReadBatches()
    }

    @Test
    fun `template batches share one database acquisition`() {
        val expectedHashes = listOf("id-0", "id-900", "id-1800")
        assertTrue(storage.saveData("group", listOf(RawDataAndMetadata("card", byteArrayOf(1))),
            expectedHashes.map { Template(it, it, byteArrayOf(2)) },
            ActionOnError.ABORT_TRANSACTION).isSuccessful)
        driver.resetCounts()

        val result = storage.readTemplates(manyIds.toSet())

        assertEquals(expectedHashes.sorted(), result.restoredData.map { it.hash }.sorted())
        assertReadBatches()
    }

    @Test
    fun `raw batches return requested payloads across both batch boundaries`() {
        val expected = mapOf(
            "id-0" to listOf(0.toByte()),
            "id-899" to listOf(1.toByte(), 2.toByte()),
            "id-900" to listOf(3.toByte()),
            "id-1799" to listOf(4.toByte()),
            "id-1800" to listOf(5.toByte(), 6.toByte()),
        )
        val values = expected + ("not-requested" to listOf(7.toByte()))
        assertTrue(storage.saveRawJsons(
            values.map { (id, data) -> RawJson(id, data.toByteArray()) },
            ActionOnError.ABORT_TRANSACTION,
        ).isSuccessful)
        driver.resetCounts()

        val result = storage.readRawJsons(manyIds.toSet())

        assertTrue(result.errors.isEmpty())
        assertEquals(expected, result.restoredData.associate { it.id to it.data.toList() })
        assertReadBatches()
    }

    @Test
    fun `empty template request preserves the empty hash lookup`() {
        storage.saveData("group", listOf(RawDataAndMetadata("card", byteArrayOf(1))),
            listOf(Template("template", "", byteArrayOf(2))), ActionOnError.ABORT_TRANSACTION)
        driver.resetCounts()

        val result = storage.readTemplates(emptySet())

        assertEquals(listOf(""), result.restoredData.map { it.hash })
        assertEquals(1, driver.openCount)
        assertEquals(1, driver.closedCount)
    }

    @Test
    fun `empty template request still reports an acquisition error`() {
        driver.failOpen = true

        val result = storage.readTemplates(emptySet())

        assertEquals(1, result.errors.size)
        assertEquals(1, driver.openCount)
    }

    @Test
    fun `later batch failure closes the cursor and the shared database`() {
        saveCards(listOf("id-0"))
        driver.failReadNumber = 2

        val result = storage.loadData(manyIds, emptyList())

        assertEquals(1, result.errors.size)
        assertEquals(1, driver.openCount)
        assertEquals(1, driver.closedCount)
        assertEquals(1, driver.closedReadStatements)
    }

    @Test
    fun `template read does not publish earlier batches when a later query fails`() {
        saveCards(listOf("id-0"))
        driver.failReadNumber = 2

        val result = storage.readTemplates(manyIds.toSet())

        assertEquals(1, result.errors.size)
        assertTrue(result.restoredData.isEmpty())
    }

    @Test
    fun `template read does not publish data when closing the database fails`() {
        saveCards(listOf("id-0"))
        driver.failClose = true

        val result = storage.readTemplates(setOf("id-0"))

        assertEquals(1, result.errors.size)
        assertTrue(result.restoredData.isEmpty())
    }

    private fun saveCards(ids: List<String>) {
        assertTrue(storage.saveData("group", ids.map { RawDataAndMetadata(it, byteArrayOf(1)) },
            listOf(Template("template", "id-0", byteArrayOf(2))),
            ActionOnError.ABORT_TRANSACTION).isSuccessful)
        driver.resetCounts()
    }

    private fun assertReadBatches() {
        assertEquals(driver.readCount, driver.closedReadStatements)
        assertEquals(1, driver.openCount)
        assertEquals(1, driver.closedCount)
        assertTrue(driver.maxBindingIndex in 1..900)
    }

    private class CountingDriver : SQLiteDriver {
        var openCount = 0
        var transactionCount = 0
        var closedCount = 0
        var readCount = 0
        var closedReadStatements = 0
        var rowsRead = 0
        var maxBindingIndex = 0
        var failReadNumber = 0
        var failOpen = false
        var failClose = false

        fun resetCounts() {
            openCount = 0
            transactionCount = 0
            closedCount = 0
            readCount = 0
            closedReadStatements = 0
            rowsRead = 0
            maxBindingIndex = 0
        }

        override fun open(fileName: String): SQLiteConnection {
            openCount++
            if (failOpen) throw SQLiteException("open failed")
            val connection = BundledSQLiteDriver().open(fileName)
            return object : SQLiteConnection by connection {
                override fun prepare(sql: String): SQLiteStatement {
                    if (sql.startsWith("BEGIN ")) transactionCount++
                    val isRead = sql.trimStart().startsWith("SELECT") && sql.contains("FROM")
                    if (isRead) {
                        readCount++
                        if (readCount == failReadNumber) throw SQLiteException("read failed")
                    }
                    return countStatement(connection.prepare(sql), isRead = isRead)
                }

                override fun close() {
                    closedCount++
                    connection.close()
                    if (failClose) throw SQLiteException("close failed")
                }
            }
        }

        private fun countStatement(statement: SQLiteStatement, isRead: Boolean): SQLiteStatement =
            object : SQLiteStatement by statement {
                override fun bindText(index: Int, value: String) {
                    maxBindingIndex = maxOf(maxBindingIndex, index)
                    statement.bindText(index, value)
                }

                override fun step(): Boolean = statement.step().also { if (isRead && it) rowsRead++ }

                override fun close() {
                    if (isRead) closedReadStatements++
                    statement.close()
                }
            }
    }
}
