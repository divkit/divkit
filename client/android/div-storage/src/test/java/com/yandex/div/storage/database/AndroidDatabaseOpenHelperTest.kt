package com.yandex.div.storage.database

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.histogram.HistogramRecorder
import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AndroidDatabaseOpenHelperTest {
    private val histogramRecorder: HistogramRecorder = mock()

    private val underTest = AndroidDatabaseOpenHelper(
        context = ApplicationProvider.getApplicationContext(),
        name = javaClass.simpleName,
        version = 1,
        ccb = {},
        ucb = { _, _, _ -> },
        histogramRecorder = histogramRecorder,
    )

    @Test
    fun `database open time is reported only for actual readable opening`() {
        val firstDatabase = underTest.readableDatabase
        val secondDatabase = underTest.readableDatabase

        verify(histogramRecorder).reportDatabaseOpenTime(any())

        firstDatabase.close()
        secondDatabase.close()
    }

    @Test
    fun `database open time is reported only for actual writable opening`() {
        val firstDatabase = underTest.writableDatabase
        val secondDatabase = underTest.writableDatabase

        verify(histogramRecorder).reportDatabaseOpenTime(any())

        firstDatabase.close()
        secondDatabase.close()
    }

    @Test
    fun `readable database is closed after single open and close`() {
        val db = underTest.readableDatabase
        db.close()

        assertFailsWith<Exception> { db.rawQuery("SELECT 1", null) }
    }

    @Test
    fun `writable database is closed after single open and close`() {
        val db = underTest.writableDatabase
        db.close()

        assertFailsWith<Exception> { db.rawQuery("SELECT 1", null) }
    }

    @Test
    fun `readable database is not closed when opened multiple times and partially closed from single thread`() {
        val db1 = underTest.readableDatabase
        val db2 = underTest.readableDatabase
        val db3 = underTest.readableDatabase

        db1.close()
        db2.close()

        // db3 is still open — the underlying DB must remain accessible
        db3.rawQuery("SELECT 1", null).close()

        db3.close()
    }

    @Test
    fun `readable database is closed when all opens are matched with closes from single thread`() {
        val db1 = underTest.readableDatabase
        val db2 = underTest.readableDatabase
        val db3 = underTest.readableDatabase

        db1.close()
        db2.close()
        db3.close()

        assertFailsWith<Exception> { db1.rawQuery("SELECT 1", null) }
    }

    @Test
    fun `writable database is not closed when opened multiple times and partially closed from single thread`() {
        val db1 = underTest.writableDatabase
        val db2 = underTest.writableDatabase
        val db3 = underTest.writableDatabase

        db1.close()
        db2.close()

        // db3 is still open — the underlying DB must remain accessible
        db3.rawQuery("SELECT 1", null).close()

        db3.close()
    }
}
