package com.yandex.divkit.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.histogram.HistogramBridge
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.shadows.ShadowTrace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.DurationUnit

@RunWith(AndroidJUnit4::class)
class MacrobenchmarkHistogramBridgeTest {
    private val delegate = mock<HistogramBridge>()
    private val bridge: HistogramBridge = MacrobenchmarkHistogramBridge(delegate)

    @Test
    fun `common bridge records composition trace counter and forwards the histogram`() {
        bridge.recordTimeHistogram(
            name = "DivCompose.Render.Composition.Cold",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = DurationUnit.MICROSECONDS,
            bucketCount = 50
        )

        val counter = ShadowTrace.getCounters().single()
        assertEquals("Div.Composition", counter.name)
        assertEquals(1234L, counter.value)
        verify(delegate).recordTimeHistogram(
            name = "DivCompose.Render.Composition.Cold",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = DurationUnit.MICROSECONDS,
            bucketCount = 50
        )
        verifyNoMoreInteractions(delegate)
    }

    @Test
    fun `common bridge records effects trace counter in microseconds`() {
        bridge.recordTimeHistogram(
            name = "DivCompose.Render.Effects.Cold",
            duration = 2,
            min = 1,
            max = 10_000,
            unit = DurationUnit.MILLISECONDS,
            bucketCount = 50
        )

        val counter = ShadowTrace.getCounters().single()
        assertEquals("Div.RenderEffects", counter.name)
        assertEquals(2000L, counter.value)
    }

    @Test
    fun `common bridge forwards warm histograms without trace counters`() {
        bridge.recordTimeHistogram(
            name = "DivCompose.Render.Composition.Warm",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = DurationUnit.MICROSECONDS,
            bucketCount = 50
        )

        assertTrue(ShadowTrace.getCounters().isEmpty())
        verify(delegate).recordTimeHistogram(
            name = "DivCompose.Render.Composition.Warm",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = DurationUnit.MICROSECONDS,
            bucketCount = 50
        )
        verifyNoMoreInteractions(delegate)
    }
}
