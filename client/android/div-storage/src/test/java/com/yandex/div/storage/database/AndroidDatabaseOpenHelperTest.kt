package com.yandex.div.storage.database

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.histogram.HistogramRecorder
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AndroidDatabaseOpenHelperTest {
    private val histogramRecorder: HistogramRecorder = mock()

    @Test
    fun `database open time is reported only for actual readable opening`() {
        val underTest = createSUT()

        val firstDatabase = underTest.readableDatabase
        val secondDatabase = underTest.readableDatabase

        verify(histogramRecorder, times(1)).reportDatabaseOpenTime(any())
        firstDatabase.close()
        secondDatabase.close()
    }

    @Test
    fun `database open time is reported only for actual writable opening`() {
        val underTest = createSUT()

        val firstDatabase = underTest.writableDatabase
        val secondDatabase = underTest.writableDatabase

        verify(histogramRecorder, times(1)).reportDatabaseOpenTime(any())
        firstDatabase.close()
        secondDatabase.close()
    }

    private fun createSUT() = AndroidDatabaseOpenHelper(
            context = ApplicationProvider.getApplicationContext(),
            name = javaClass.simpleName,
            version = 1,
            ccb = {},
            ucb = { _, _, _ -> },
            histogramRecorder = histogramRecorder,
    )
}
