package com.yandex.div.storage

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.Template
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StorageReadSnapshotTest {
    private val path = temporaryDatabasePath()
    private val ids = (0..900).map { "id-$it" }
    private val oldValues = ids.associateWith { listOf(1.toByte()) }
    private val readerDriver = ReadDriver()
    private val readerProvider = provider(readerDriver)
    private val writerProvider = provider(BundledSQLiteDriver())

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `single template batch does not start a transaction`() {
        BaseDivDatabaseStorage(writerProvider).use { writer ->
            writer.writeTemplates("card", mapOf("id-0" to byteArrayOf(1)))
            BaseDivDatabaseStorage(readerProvider).use { reader ->
                val result = reader.readTemplatesByIds("id-0")

                assertEquals(setOf("id-0"), result.keys)
                assertEquals(0, readerDriver.transactionCount)
            }
        }
    }

    @Test
    fun `template ids return one snapshot while another storage replaces the templates`() {
        BaseDivDatabaseStorage(writerProvider).use { writer ->
            writer.writeTemplates("card", ids.associateWith { byteArrayOf(1) })
            BaseDivDatabaseStorage(readerProvider).use { reader ->
                readerDriver.afterFirstRead = { attemptWrite { replaceTemplates(writer) } }

                val result = reader.readTemplatesByIds(*ids.toTypedArray())

                replaceTemplates(writer)
                assertEquals(oldValues, result.mapValues { it.value.toList() })
            }
        }
    }

    @Test
    fun `card batches return one snapshot while another storage replaces the cards`() {
        val writer = DivStorage(StorageCodec.BYTES, writerProvider)
        writeCards(writer, 1)
        val reader = DivStorage(StorageCodec.BYTES, readerProvider)
        readerDriver.afterFirstRead = { attemptWrite { writeCards(writer, 2) } }

        val result = reader.loadData(ids, emptyList())

        writeCards(writer, 2)
        assertTrue(result.errors.isEmpty())
        assertEquals(oldValues, result.restoredData.associate { it.id to it.divData.toList() })
    }

    @Test
    fun `template hash batches return one snapshot while another storage replaces templates`() {
        val writer = DivStorage(StorageCodec.BYTES, writerProvider)
        writeHashedTemplates(writer, 1)
        val reader = DivStorage(StorageCodec.BYTES, readerProvider)
        readerDriver.afterFirstRead = {
            attemptWrite {
                writer.removeAllTemplates()?.let { throw it }
                writeHashedTemplates(writer, 2)
            }
        }

        val result = reader.readTemplates(ids.toSet())

        writer.removeAllTemplates()?.let { throw it }
        writeHashedTemplates(writer, 2)
        assertTrue(result.errors.isEmpty())
        assertEquals(oldValues, result.restoredData.associate { it.hash to it.data.toList() })
    }

    @Test
    fun `raw batches return one snapshot while another storage replaces the values`() {
        val writer = DivStorage(StorageCodec.BYTES, writerProvider)
        writeRaw(writer, 1)
        val reader = DivStorage(StorageCodec.BYTES, readerProvider)
        readerDriver.afterFirstRead = { attemptWrite { writeRaw(writer, 2) } }

        val result = reader.readRawJsons(ids.toSet())

        writeRaw(writer, 2)
        assertTrue(result.errors.isEmpty())
        assertEquals(oldValues, result.restoredData.associate { it.id to it.data.toList() })
    }

    @Test
    fun `failed later raw batch releases its snapshot for another writer`() {
        val writer = DivStorage(StorageCodec.BYTES, writerProvider)
        writeRaw(writer, 1)
        val reader = DivStorage(StorageCodec.BYTES, readerProvider)
        readerDriver.failReadNumber = 2

        val result = reader.readRawJsons(ids.toSet())

        assertEquals(1, result.errors.size)
        writeRaw(writer, 2)
        assertEquals(listOf(2.toByte()), reader.readRawJsons(setOf("id-0"))
            .restoredData.single().data.toList())
    }

    @Test
    fun `successful nested card batches preserve the outer transaction`() {
        val storage = DivStorage(StorageCodec.BYTES, readerProvider)
        storage.saveData("group", listOf(RawDataAndMetadata("id-0", byteArrayOf(1))),
            emptyList(), ActionOnError.ABORT_TRANSACTION).requireSuccess()

        val result = storage.remove {
            assertEquals(listOf("id-0"),
                storage.loadData(ids, emptyList()).restoredData.map { it.id })
            true
        }

        assertTrue(result.errors.isEmpty())
        assertEquals(setOf("id-0"), result.ids)
        assertTrue(storage.loadData(listOf("id-0"), emptyList()).restoredData.isEmpty())
    }

    @Test
    fun `handled nested batch error does not make the outer transaction rollback only`() {
        val storage = DivStorage(StorageCodec.BYTES, readerProvider)
        storage.saveData("group", listOf(RawDataAndMetadata("id-0", byteArrayOf(1))),
            emptyList(), ActionOnError.ABORT_TRANSACTION).requireSuccess()
        readerDriver.failReadNumber = 3

        val result = storage.remove {
            assertEquals(1, storage.loadData(ids, emptyList()).errors.size)
            true
        }

        assertTrue(result.errors.isEmpty())
        assertEquals(setOf("id-0"), result.ids)
        assertTrue(storage.loadData(listOf("id-0"), emptyList()).restoredData.isEmpty())
    }

    private fun replaceTemplates(writer: BaseDivDatabaseStorage) {
        writer.clear()
        writer.writeTemplates("card", ids.associateWith { byteArrayOf(2) })
    }

    private fun writeCards(storage: DivStorage<ByteArray>, value: Byte) {
        storage.saveData("group", ids.map { RawDataAndMetadata(it, byteArrayOf(value)) },
            emptyList(), ActionOnError.ABORT_TRANSACTION).requireSuccess()
    }

    private fun writeHashedTemplates(storage: DivStorage<ByteArray>, value: Byte) {
        storage.saveData("group", listOf(RawDataAndMetadata("card", byteArrayOf(1))),
            ids.map { Template(it, it, byteArrayOf(value)) },
            ActionOnError.ABORT_TRANSACTION).requireSuccess()
    }

    private fun writeRaw(storage: DivStorage<ByteArray>, value: Byte) {
        storage.saveRawJsons(ids.map { RawJson(it, byteArrayOf(value)) },
            ActionOnError.ABORT_TRANSACTION).requireSuccess()
    }

    private fun ExecutionResult.requireSuccess() {
        errors.firstOrNull()?.let { throw it }
    }

    private fun attemptWrite(action: () -> Unit) {
        val failure = runCatching(action).exceptionOrNull() ?: return
        assertTrue(generateSequence(failure) { it.cause }
            .any { it.message.orEmpty().contains("locked") }, failure.toString())
    }

    private fun provider(
        driver: SQLiteDriver,
    ) = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        val helper = SQLiteDatabaseOpenHelper(driver, path, version, create, upgrade)
        object : DatabaseOpenHelper by helper {
            override val writableDatabase: DatabaseOpenHelper.Database
                get() = helper.writableDatabase.also { it.execSQL("PRAGMA busy_timeout = 0") }
        }
    }

    private class ReadDriver : SQLiteDriver {
        var afterFirstRead: (() -> Unit)? = null
        var failReadNumber = 0
        var transactionCount = 0
        private var reads = 0

        override fun open(fileName: String): SQLiteConnection {
            val connection = BundledSQLiteDriver().open(fileName)
            return object : SQLiteConnection by connection {
                override fun prepare(sql: String): SQLiteStatement {
                    if (sql.startsWith("BEGIN ")) transactionCount++
                    val isRead = sql.trimStart().startsWith("SELECT") && sql.contains("FROM")
                    if (!isRead) return connection.prepare(sql)
                    reads++
                    if (reads == failReadNumber) throw SQLiteException("read failed")
                    val statement = connection.prepare(sql)
                    return object : SQLiteStatement by statement {
                        override fun close() {
                            statement.close()
                            val callback = afterFirstRead
                            afterFirstRead = null
                            callback?.invoke()
                        }
                    }
                }
            }
        }
    }
}
