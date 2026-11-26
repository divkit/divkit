package com.yandex.div.storage

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.SQLiteException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.storage.database.TransactionOutcomeDatabase
import com.yandex.div.storage.database.createAndroidDatabaseOpenHelper
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div.storage.rawjson.RawJson
import org.json.JSONObject
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AndroidSQLiteDriverMigrationTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `v2 Android database keeps cards and templates when raw JSON is enabled`() {
        val file = context.getDatabasePath("div-storage.db")
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { oldDatabase ->
            oldDatabase.execSQL(
                "CREATE TABLE cards(layout_id TEXT PRIMARY KEY, card_data BLOB, " +
                    "metadata BLOB, group_id TEXT NOT NULL)",
            )
            oldDatabase.execSQL(
                "CREATE TABLE templates(template_hash TEXT PRIMARY KEY, template_data BLOB)",
            )
            oldDatabase.execSQL("INSERT INTO cards VALUES ('card', X'7B7D', NULL, 'group')")
            oldDatabase.execSQL("INSERT INTO templates VALUES ('hash', X'7B7D')")
            oldDatabase.version = 2
        }

        val component = DivStorageComponent.createInternal(context)
        val saved = component.rawJsonRepository.put(
            RawJsonRepository.Payload(listOf(RawJson("stored_value_name", JSONObject()))),
        )

        assertTrue(saved.errors.isEmpty())
        val card = component.storage.loadData(listOf("card"), emptyList()).restoredData.single()
        assertEquals("{}", card.divData.toString())
        val template = component.storage.readTemplates(setOf("hash")).restoredData.single()
        assertContentEquals("{}".encodeToByteArray(), template.data)
        val stored = DivStorageComponent.create(context).rawJsonRepository
            .get(listOf("stored_value_name"))
        assertEquals("stored_value_name", stored.resultData.single().id)
    }

    @Test
    fun `template database opens data written through framework SQLite`() {
        val file = context.getDatabasePath("div.db")
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { oldDatabase ->
            oldDatabase.execSQL(
                "CREATE TABLE templates(template_id TEXT PRIMARY KEY, template_data BLOB NOT NULL)",
            )
            oldDatabase.execSQL(
                "CREATE TABLE template_usages(card_id TEXT, template_id TEXT, " +
                    "PRIMARY KEY(card_id, template_id))",
            )
            oldDatabase.execSQL("INSERT INTO templates VALUES ('template', X'0102')")
            oldDatabase.execSQL("INSERT INTO template_usages VALUES ('card', 'template')")
            oldDatabase.version = 1
        }

        DivDatabaseStorage(context).use { storage ->
            assertContentEquals(byteArrayOf(1, 2), storage.readTemplates("card")["template"])
        }
    }

    @Test
    fun `component creation defers database path lookup`() {
        var lookups = 0
        val trackingContext = object : ContextWrapper(context) {
            override fun getDatabasePath(name: String): File {
                lookups++
                return super.getDatabasePath(name)
            }
        }

        val component = DivStorageComponent.create(trackingContext)

        assertEquals(0, lookups)
        component.rawJsonRepository.get(listOf("stored_value"))
        assertEquals(1, lookups)
    }

    @Test
    fun `failed open histogram is reported once before the same database can be used`() {
        var reports = 0
        val reporter = object : HistogramReporterDelegate by HistogramReporterDelegate.NoOp {
            override fun reportDuration(histogramName: String, duration: Long, forceCallType: String?) {
                reports++
                error("Reporter failed")
            }
        }
        val helper = helper(reporter)

        assertFailsWith<IllegalStateException> { helper.readableDatabase }

        helper.readableDatabase.use { db ->
            db.rawQuery("SELECT value FROM records", null).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("saved", cursor.getString(0))
            }
        }
        assertEquals(1, reports)
    }

    @Test
    @Config(sdk = [35])
    fun `read snapshots from separate helpers can overlap on Android 15`() {
        helper().readableDatabase.use { first ->
            helper().readableDatabase.use { second ->
                for (db in listOf(first, second)) {
                    db.rawQuery("PRAGMA busy_timeout = 0", null).use { it.moveToFirst() }
                }
                val firstSnapshot = assertIs<TransactionOutcomeDatabase>(first)
                val secondSnapshot = assertIs<TransactionOutcomeDatabase>(second)
                assertTrue(firstSnapshot.beginReadTransaction())
                try {
                    first.rawQuery("SELECT value FROM records", null).use {
                        assertTrue(it.moveToFirst())
                    }
                    assertTrue(secondSnapshot.beginReadTransaction())
                    secondSnapshot.endTransaction()
                } finally {
                    firstSnapshot.endTransaction()
                }
            }
        }
    }

    @Test
    @Config(sdk = [35])
    fun `a WAL writer lets another thread read through the same helper`() {
        val helper = helper(openWritable = { file ->
            SQLiteDatabase.openOrCreateDatabase(file, null).also { db ->
                assertTrue(db.enableWriteAheadLogging())
            }
        })
        val reader = Executors.newSingleThreadExecutor()
        try {
            helper.writableDatabase.use { writer ->
                writer.beginTransaction()
                try {
                    writer.execSQL("UPDATE records SET value = 'pending'")
                    val read = reader.submit<String> {
                        helper.readableDatabase.use { db ->
                            db.rawQuery("SELECT value FROM records", null).use { cursor ->
                                assertTrue(cursor.moveToFirst())
                                cursor.getString(0)
                            }
                        }
                    }
                    assertEquals("saved", read.get(3, TimeUnit.SECONDS))
                } finally {
                    writer.endTransaction()
                }
            }
        } finally {
            reader.shutdownNow()
        }
    }

    @Test
    @Config(sdk = [35])
    fun `read snapshot forbids writes on a writable database on Android 15`() {
        helper().writableDatabase.use { db ->
            val reader = assertIs<TransactionOutcomeDatabase>(db)
            assertTrue(reader.beginReadTransaction())
            try {
                assertFailsWith<SQLiteException> {
                    db.execSQL("INSERT INTO records VALUES ('not allowed')")
                }
            } finally {
                reader.endTransaction()
            }
        }
    }

    @Test
    @Config(sdk = [35])
    fun `read only fallback leases the existing file and lets a writer recover`() {
        helper().writableDatabase.close()
        var writeDisabled = true
        val openingError = SQLiteCantOpenDatabaseException("writable open failed")
        val helper = helper(openWritable = { file ->
            if (writeDisabled) throw openingError
            SQLiteDatabase.openOrCreateDatabase(file, null)
        })

        helper.readableDatabase.use { reader ->
            assertTrue(ShadowLog.getLogsForTag("DivStorage").any { it.throwable === openingError })
            val snapshot = assertIs<TransactionOutcomeDatabase>(reader)
            assertTrue(snapshot.beginReadTransaction())
            try {
                reader.rawQuery("SELECT value FROM records", null).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("saved", cursor.getString(0))
                }
            } finally {
                snapshot.endTransaction()
            }
            assertEquals(openingError, assertFailsWith<SQLiteException> { helper.writableDatabase })

            writeDisabled = false
            helper.writableDatabase.use { writer ->
                reader.close()
                writer.execSQL("UPDATE records SET value = 'restored'")
                writer.rawQuery("SELECT value FROM records", null).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("restored", cursor.getString(0))
                }
            }
        }
    }

    private fun helper(
        reporter: HistogramReporterDelegate = HistogramReporterDelegate.NoOp,
        openWritable: (File) -> SQLiteDatabase = { SQLiteDatabase.openOrCreateDatabase(it, null) },
    ) =
        createAndroidDatabaseOpenHelper(
            context = context,
            name = "storage-test.db",
            version = 1,
            create = { db ->
                db.execSQL("CREATE TABLE records(value TEXT)")
                db.execSQL("INSERT INTO records VALUES ('saved')")
            },
            upgrade = { _, _, _ -> },
            reportOpenTime = HistogramRecorder(reporter, null)::reportDatabaseOpenTime,
            openWritable = openWritable,
        )
}
