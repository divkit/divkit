package com.yandex.div.storage

import android.database.sqlite.SQLiteException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.storage.database.AndroidDatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.TransactionOutcomeDatabase
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div.storage.internal.DivStorage as CommonDivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata as CommonRawData
import com.yandex.div.storage.internal.rawjson.RawJson as CommonRawJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidStorageReadSnapshotTest {
    private val ids = (0..900).map { "id-$it" }
    private var afterFirstRead: (() -> Unit)? = null
    private var reads = 0
    private var failReadNumber = 0
    private val writerProvider = provider(observeReads = false)
    private val readerProvider = provider(observeReads = true)

    @Test
    fun `template batches keep one snapshot when another storage attempts replacement`() {
        BaseDivDatabaseStorage(writerProvider).use { writer ->
            writer.writeTemplates("card", ids.associateWith { byteArrayOf(1) })
            BaseDivDatabaseStorage(readerProvider).use { reader ->
                afterFirstRead = { attemptWrite { replaceTemplates(writer) } }

                val result = reader.readTemplatesByIds(*ids.toTypedArray())

                replaceTemplates(writer)
                assertEquals(ids.associateWith { listOf(1.toByte()) },
                    result.mapValues { it.value.toList() })
            }
        }
    }

    @Test
    fun `nested multi-batch read leaves the outer transaction successful`() {
        val storage = CommonDivStorage(StorageCodec.BYTES, readerProvider)
        storage.saveData("group", listOf(CommonRawData("id-0", byteArrayOf(1))),
            emptyList(), ActionOnError.ABORT_TRANSACTION)

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
    fun `handled nested batch error leaves the outer transaction successful`() {
        val storage = CommonDivStorage(StorageCodec.BYTES, readerProvider)
        storage.saveData("group", listOf(CommonRawData("id-0", byteArrayOf(1))),
            emptyList(), ActionOnError.ABORT_TRANSACTION)
        failReadNumber = 3

        val result = storage.remove {
            assertEquals(1, storage.loadData(ids, emptyList()).errors.size)
            true
        }

        assertTrue(result.errors.isEmpty())
        assertEquals(setOf("id-0"), result.ids)
        assertTrue(storage.loadData(listOf("id-0"), emptyList()).restoredData.isEmpty())
    }

    @Test
    fun `later batch failure releases the snapshot for a writer`() {
        val writer = CommonDivStorage(StorageCodec.BYTES, writerProvider)
        writer.saveRawJsons(listOf(CommonRawJson("id-0", byteArrayOf(1))),
            ActionOnError.ABORT_TRANSACTION)
        val reader = CommonDivStorage(StorageCodec.BYTES, readerProvider)
        failReadNumber = 2

        val result = reader.readRawJsons(ids.toSet())

        assertEquals(1, result.errors.size)
        assertTrue(writer.saveRawJsons(listOf(CommonRawJson("id-0", byteArrayOf(2))),
            ActionOnError.ABORT_TRANSACTION).isSuccessful)
        assertEquals(listOf(2.toByte()), reader.readRawJsons(setOf("id-0"))
            .restoredData.single().data.toList())
    }

    private fun replaceTemplates(writer: BaseDivDatabaseStorage) {
        writer.clear()
        writer.writeTemplates("card", ids.associateWith { byteArrayOf(2) })
    }

    private fun attemptWrite(action: () -> Unit) {
        val failure = runCatching(action).exceptionOrNull() ?: return
        assertTrue(generateSequence(failure) { it.cause }
            .any { it.message.orEmpty().contains("locked") }, failure.toString())
    }

    private fun provider(
        observeReads: Boolean,
    ) = DatabaseOpenHelperProvider { name, version, create, upgrade ->
        val helper = AndroidDatabaseOpenHelper(ApplicationProvider.getApplicationContext(),
            name, version, create, upgrade, HistogramRecorder(HistogramReporterDelegate.NoOp, null))
        object : DatabaseOpenHelper by helper {
            override val writableDatabase: Database
                get() = wrapDatabase(helper.writableDatabase, observeReads)
            override val readableDatabase: Database
                get() = wrapDatabase(helper.readableDatabase, observeReads)
        }
    }

    private fun wrapDatabase(db: Database, observeReads: Boolean): Database {
        db.rawQuery("PRAGMA busy_timeout = 0", null).use { it.moveToFirst() }
        if (!observeReads) return db
        val transactions = db as TransactionOutcomeDatabase
        return object : TransactionOutcomeDatabase by transactions {
            override fun rawQuery(query: String, selectionArgs: Array<out String?>?): Cursor {
                beforeRead()
                return wrapCursor(db.rawQuery(query, selectionArgs))
            }

            override fun query(
                table: String,
                columns: Array<String?>?,
                selection: String?,
                selectionArgs: Array<String?>?,
                groupBy: String?,
                having: String?,
                orderBy: String?,
                limit: String?,
            ): Cursor {
                beforeRead()
                return wrapCursor(db.query(table, columns, selection, selectionArgs,
                    groupBy, having, orderBy, limit))
            }
        }
    }

    private fun beforeRead() {
        reads++
        if (reads == failReadNumber) throw SQLiteException("read failed")
    }

    private fun wrapCursor(cursor: Cursor): Cursor = object : Cursor by cursor {
        override fun close() {
            cursor.close()
            val callback = afterFirstRead
            afterFirstRead = null
            callback?.invoke()
        }
    }
}
