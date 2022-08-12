package com.yandex.div.histogram.reporter

import com.yandex.div.core.histogram.HistogramRecorder
import com.yandex.div.histogram.HistogramCallTypeProvider
import com.yandex.div.histogram.HistogramColdTypeChecker
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.TaskExecutor
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.security.SecureRandom
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HistogramReporterDelegateImplTest {

    private val coldTypeChecker = HistogramColdTypeChecker()
    private val callTypeProvider = HistogramCallTypeProvider { coldTypeChecker }
    private val taskExecutor: TaskExecutor = object : TaskExecutor {
        override fun post(task: () -> Unit) = Unit
    }
    private val histogramRecorder = mock<HistogramRecorder>()
    private val histogramRecordConfig = mock<HistogramRecordConfiguration> {
        on { isColdRecordingEnabled } doReturn true
        on { isCoolRecordingEnabled } doReturn true
        on { isWarmRecordingEnabled } doReturn true
    }

    private val underTest = HistogramReporterDelegateImpl(
        { histogramRecorder },
        callTypeProvider,
        histogramRecordConfig,
        { taskExecutor }
    )

    private val latch = CountDownLatch(TEST_NUMBER)

    @Test
    fun `thread safe report duration`() {
        repeat(TEST_NUMBER) {
            Thread {
                underTest.reportDuration(randomHistogramName(), 100L)
                latch.countDown()
            }.start()
        }
        Assert.assertTrue(
            "Failed to report all durations. Left ${latch.count}",
            latch.await(1, TimeUnit.MINUTES)
        )
    }

    private fun randomHistogramName(): String {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        return String(bytes)
    }

    private companion object {
        const val TEST_NUMBER = 10000
    }
}
