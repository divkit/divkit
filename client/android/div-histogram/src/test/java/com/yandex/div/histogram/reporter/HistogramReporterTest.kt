package com.yandex.div.histogram.reporter

import com.yandex.div.core.histogram.HistogramRecorder
import com.yandex.div.histogram.HistogramCallTypeProvider
import com.yandex.div.histogram.HistogramColdTypeChecker
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.TaskExecutor
import org.junit.Test
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.util.concurrent.TimeUnit

class HistogramReporterTest {

    private val histogramRecorder = mock<HistogramRecorder>()
    private val coldTypeChecker = HistogramColdTypeChecker()
    private val histogramCallTypeProvider = HistogramCallTypeProvider { coldTypeChecker }
    private val histogramRecordConfig = mock<HistogramRecordConfiguration> {
        on { isColdRecordingEnabled } doReturn true
        on { isCoolRecordingEnabled } doReturn true
        on { isWarmRecordingEnabled } doReturn true
    }
    private val underTest = HistogramReporter(
        HistogramReporterDelegateImpl(
            { histogramRecorder },
            histogramCallTypeProvider,
            histogramRecordConfig,
        ) { ImmediateTaskExecutor() }
    )

    @Test
    fun `report duration histogram without component`() {
        underTest.reportDuration(TEST_HISTOGRAM, 100)

        verify(histogramRecorder).recordShortTimeHistogram(
            "$TEST_HISTOGRAM.Cold",
            100,
            TimeUnit.MILLISECONDS
        )
    }

    @Test
    fun `report duration histogram with component`() {
        underTest.reportDuration(TEST_HISTOGRAM, 100, TEST_COMPONENT)

        verify(histogramRecorder).recordShortTimeHistogram(
            "$TEST_HISTOGRAM.Cold",
            100,
            TimeUnit.MILLISECONDS
        )
        verify(histogramRecorder).recordShortTimeHistogram(
            "$TEST_COMPONENT.$TEST_HISTOGRAM.Cold",
            100,
            TimeUnit.MILLISECONDS
        )
    }

    @Test
    fun `report size histogram without component`() {
        underTest.reportSize(TEST_HISTOGRAM, 100)

        verify(histogramRecorder).recordCount100KHistogram("$TEST_HISTOGRAM.Size", 100)
    }

    @Test
    fun `report size histogram with component`() {
        underTest.reportSize(TEST_HISTOGRAM, 100, TEST_COMPONENT)

        verify(histogramRecorder).recordCount100KHistogram(
            "$TEST_COMPONENT.$TEST_HISTOGRAM.Size",
            100
        )
    }

    @Test
    fun `report warm histogram without component and cold with component`() {
        underTest.reportDuration(TEST_HISTOGRAM, 100)

        clearInvocations(histogramRecorder)

        underTest.reportDuration(TEST_HISTOGRAM, 100, TEST_COMPONENT)

        verify(histogramRecorder).recordShortTimeHistogram(
            "$TEST_HISTOGRAM.Warm", 100,
            TimeUnit.MILLISECONDS
        )
        verify(histogramRecorder).recordShortTimeHistogram(
            "$TEST_COMPONENT.$TEST_HISTOGRAM.Cold",
            100,
            TimeUnit.MILLISECONDS
        )
    }

    @Test
    fun `do not report general but report component`() {
        underTest.reportDuration(TEST_HISTOGRAM, 100, TEST_COMPONENT,
            filter = {
                when (it) {
                    null -> false
                    else -> true
                }
            }
        )

        verify(histogramRecorder).recordShortTimeHistogram(
            "$TEST_COMPONENT.$TEST_HISTOGRAM.Cold", 100,
            TimeUnit.MILLISECONDS
        )
        verifyNoMoreInteractions(histogramRecorder)
    }

    private class ImmediateTaskExecutor : TaskExecutor {
        override fun post(task: () -> Unit) = task()
    }

    private companion object {
        private const val TEST_COMPONENT = "TestComponent"
        private const val TEST_HISTOGRAM = "Div.Test"
    }
}
