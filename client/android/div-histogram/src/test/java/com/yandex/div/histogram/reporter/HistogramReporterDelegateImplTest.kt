package com.yandex.div.histogram.reporter

import com.yandex.div.histogram.HistogramCallTypeProvider
import com.yandex.div.histogram.HistogramColdTypeChecker
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.histogram.TaskExecutor
import kotlin.test.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HistogramReporterDelegateImplTest {

    private val coldTypeChecker = HistogramColdTypeChecker()
    private val histogramRecorder = mock<HistogramRecorder>()

    private val taskExecutor: TaskExecutor = object : TaskExecutor {
        override fun post(task: () -> Unit) = Unit
    }

    private val histogramRecordConfig = mock<HistogramRecordConfiguration> {
        on { isColdRecordingEnabled } doReturn true
        on { isCoolRecordingEnabled } doReturn true
        on { isWarmRecordingEnabled } doReturn true
    }

    private val underTest = HistogramReporterDelegateImpl(
        { histogramRecorder },
        HistogramCallTypeProvider { coldTypeChecker },
        histogramRecordConfig,
        { taskExecutor }
    )

    @Test
    fun `thread safe report duration`() {
        val executor = Executors.newFixedThreadPool(WORKER_COUNT)
        val start = CyclicBarrier(WORKER_COUNT)
        try {
            val workers = List(WORKER_COUNT) { worker ->
                executor.submit {
                    start.await(1, TimeUnit.SECONDS)
                    repeat(TEST_NUMBER / WORKER_COUNT) { index ->
                        underTest.reportDuration("histogram-$worker-$index", 100L)
                    }
                }
            }
            workers.forEach { it.get(1, TimeUnit.MINUTES) }
        } finally {
            executor.shutdownNow()
            executor.awaitTermination(1, TimeUnit.SECONDS)
        }
    }
}

private const val WORKER_COUNT = 8
private const val TEST_NUMBER = 10000
