package com.yandex.divkit.benchmark

import android.os.Trace
import androidx.annotation.RequiresApi
import com.yandex.div.histogram.HistogramBridge
import com.yandex.divkit.benchmark.div.histogram.LoggingHistogramBridge
import java.util.concurrent.TimeUnit

@RequiresApi(29)
internal class MacrobenchmarkHistogramBridge(
    private val delegate: HistogramBridge = LoggingHistogramBridge()
) : HistogramBridge by delegate {

    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: TimeUnit,
        bucketCount: Int
    ) {
        val counter = when (name) {
            "DivCompose.Render.Composition.Cold" -> "Div.Composition"
            "DivCompose.Render.Effects.Cold" -> "Div.RenderEffects"
            else -> null
        }
        if (counter != null) {
            // The histogram already contains the duration; a trace slice here would
            // measure the callback instead. Counters store the original microseconds.
            Trace.setCounter(counter, unit.toMicros(duration))
        }
        delegate.recordTimeHistogram(name, duration, min, max, unit, bucketCount)
    }
}
