package com.yandex.div.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.storage.database.AndroidDatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.TransactionOutcomeDatabase
import com.yandex.div.storage.database.asStorageDatabase
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div.storage.internal.DivStorage as CommonDivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata as CommonRawData
import com.yandex.div.storage.internal.rawjson.RawJson as CommonRawJson
import com.yandex.div.storage.templates.Template
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28, 34, 35])
class AndroidReadOnlyStorageTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val histogramRecorder = HistogramRecorder(HistogramReporterDelegate.NoOp, null)
    private val ids = (0..894).map { "id-$it" } + listOf(
        "id-900", "", "quoted') OR 1=1 --", "zero\u0000tail", "Привет😀", "\n\t",
    )
    private val expected = ids.mapIndexed { index, id -> id to listOf((index % 113).toByte()) }
        .toMap()
    private var afterRead: (() -> Unit)? = null
    private val provider = DatabaseOpenHelperProvider { name, version, create, upgrade ->
        val helper = AndroidDatabaseOpenHelper(context, name, version, create, upgrade,
            histogramRecorder)
        object : DatabaseOpenHelper by helper {
            override val writableDatabase: DatabaseOpenHelper.Database
                get() = helper.writableDatabase.also { db ->
                    db.rawQuery("PRAGMA busy_timeout = 0", null).use { it.moveToFirst() }
                }
        }
    }

    @Test
    fun `raw batches read an existing read only database`() {
        val writer = CommonDivStorage(StorageCodec.BYTES, provider)
        val values = expected.map { (id, bytes) -> CommonRawJson(id, bytes.toByteArray()) } +
            CommonRawJson("outside", ByteArray(4 * 1024 * 1024))
        assertTrue(writer.saveRawJsons(values, ActionOnError.ABORT_TRANSACTION).isSuccessful)

        withReadOnlyDatabase("div-storage.db", 3) { readOnlyProvider ->
            val reader = CommonDivStorage(StorageCodec.BYTES, readOnlyProvider)
            assertTrue(reader.readRawJsons(setOf("id-0")).errors.isEmpty())

            val result = reader.readRawJsons(ids.toSet())

            assertTrue(result.errors.isEmpty(), result.errors.firstOrNull()?.cause.toString())
            assertEquals(expected, result.restoredData.associate { it.id to it.data.toList() })
        }
    }

    @Test
    fun `card batches read an existing read only database`() {
        val writer = CommonDivStorage(StorageCodec.BYTES, provider)
        val values = expected.map { (id, bytes) -> CommonRawData(id, bytes.toByteArray()) } +
            CommonRawData("outside", ByteArray(4 * 1024 * 1024))
        assertTrue(writer.saveData("group", values, emptyList(),
            ActionOnError.ABORT_TRANSACTION).isSuccessful)

        withReadOnlyDatabase("div-storage.db", 3) { readOnlyProvider ->
            val result = CommonDivStorage(StorageCodec.BYTES, readOnlyProvider)
                .loadData(ids, emptyList())

            assertTrue(result.errors.isEmpty(), result.errors.firstOrNull()?.cause.toString())
            assertEquals(expected, result.restoredData.associate { it.id to it.divData.toList() })
        }
    }

    @Test
    fun `large exclusions filter unrequested payload in SQL`() {
        val writer = CommonDivStorage(StorageCodec.BYTES, provider)
        val excluded = (0..900).map { "excluded-$it" }
        assertTrue(writer.saveData("group", listOf(
            CommonRawData("excluded-900", ByteArray(4 * 1024 * 1024)),
            CommonRawData("retained", byteArrayOf(1)),
        ), emptyList(), ActionOnError.ABORT_TRANSACTION).isSuccessful)

        withReadOnlyDatabase("div-storage.db", 3) { readOnlyProvider ->
            val result = CommonDivStorage(StorageCodec.BYTES, readOnlyProvider)
                .loadData(emptyList(), excluded)

            assertTrue(result.errors.isEmpty(), result.errors.firstOrNull()?.cause.toString())
            assertEquals(mapOf("retained" to listOf(1.toByte())),
                result.restoredData.associate { it.id to it.divData.toList() })
        }
    }

    @Test
    fun `template hash batches read an existing read only database`() {
        val writer = CommonDivStorage(StorageCodec.BYTES, provider)
        val values = expected.map { (id, bytes) -> Template(id, id, bytes.toByteArray()) } +
            Template("outside", "outside", ByteArray(4 * 1024 * 1024))
        assertTrue(writer.saveData("group", listOf(CommonRawData("card", byteArrayOf(1))), values,
            ActionOnError.ABORT_TRANSACTION).isSuccessful)

        withReadOnlyDatabase("div-storage.db", 3) { readOnlyProvider ->
            val result = CommonDivStorage(StorageCodec.BYTES, readOnlyProvider)
                .readTemplates(ids.toSet())

            assertTrue(result.errors.isEmpty(), result.errors.firstOrNull()?.cause.toString())
            assertEquals(expected, result.restoredData.associate { it.hash to it.data.toList() })
        }
    }

    @Test
    fun `template id batches read an existing read only database`() {
        BaseDivDatabaseStorage(provider).use { writer ->
            writer.writeTemplates("card",
                expected.mapValues { it.value.toByteArray() } +
                    ("outside" to ByteArray(4 * 1024 * 1024)))
        }

        withReadOnlyDatabase("div.db", 1) { readOnlyProvider ->
            BaseDivDatabaseStorage(readOnlyProvider).use { reader ->
                val result = reader.readTemplatesByIds(*ids.toTypedArray())

                assertEquals(expected, result.mapValues { it.value.toList() })
            }
        }
    }

    @Test
    fun `read only raw lookup keeps one snapshot during another writer update`() {
        val writer = CommonDivStorage(StorageCodec.BYTES, provider)
        assertTrue(writer.saveRawJsons(ids.map { CommonRawJson(it, byteArrayOf(1)) },
            ActionOnError.ABORT_TRANSACTION).isSuccessful)
        val updated = ids.map { CommonRawJson(it, byteArrayOf(2)) }

        withReadOnlyDatabase("div-storage.db", 3) { readOnlyProvider ->
            afterRead = {
                val failure = writer.saveRawJsons(updated, ActionOnError.ABORT_TRANSACTION)
                    .errors.firstOrNull()
                if (failure != null) {
                    assertTrue(generateSequence<Throwable>(failure) { it.cause }
                        .any { it.message.orEmpty().contains("locked") })
                }
            }
            val reader = CommonDivStorage(StorageCodec.BYTES, readOnlyProvider)

            val result = reader.readRawJsons(ids.toSet())

            assertTrue(result.errors.isEmpty(), result.errors.firstOrNull()?.cause.toString())
            assertTrue(writer.saveRawJsons(updated, ActionOnError.ABORT_TRANSACTION).isSuccessful)
            assertEquals(ids.associateWith { listOf(1.toByte()) },
                result.restoredData.associate { it.id to it.data.toList() })
        }
    }

    private fun withReadOnlyDatabase(
        name: String,
        version: Int,
        block: (DatabaseOpenHelperProvider) -> Unit,
    ) {
        val file = context.getDatabasePath(name)
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY).use { native ->
            assertTrue(native.isReadOnly)
            assertEquals(version, native.version)
            val actual = native.asStorageDatabase()
            val borrowed = object : TransactionOutcomeDatabase by actual {
                override fun close() = Unit
                override fun rawQuery(
                    query: String,
                    selectionArgs: Array<out String?>?,
                ): DatabaseOpenHelper.Cursor {
                    val cursor = actual.rawQuery(query, selectionArgs)
                    if (!query.contains("FROM", ignoreCase = true)) return cursor
                    return object : DatabaseOpenHelper.Cursor by cursor {
                        override fun close() {
                            cursor.close()
                            val callback = afterRead
                            afterRead = null
                            callback?.invoke()
                        }
                    }
                }
            }
            block(DatabaseOpenHelperProvider { _, _, _, _ ->
                object : DatabaseOpenHelper {
                    override val readableDatabase = borrowed
                    override val writableDatabase = borrowed
                }
            })
        }
    }
}
