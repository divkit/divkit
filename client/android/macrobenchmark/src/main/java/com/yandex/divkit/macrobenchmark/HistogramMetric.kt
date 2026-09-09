package com.yandex.divkit.macrobenchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.TraceMetric
import androidx.benchmark.traceprocessor.TraceProcessor

/** Reads a duration reported by HistogramBridge as a microsecond trace counter. */
@OptIn(ExperimentalMetricApi::class)
internal class HistogramMetric(private val counterName: String) : TraceMetric() {

    override fun getMeasurements(
        captureInfo: CaptureInfo,
        traceSession: TraceProcessor.Session
    ): List<Measurement> {
        val counter = counterName.replace("'", "''")
        val process = captureInfo.targetPackageName.replace("'", "''")
        val rows = traceSession.query(
            """
            SELECT counter.value
            FROM counter
            JOIN process_counter_track ON counter.track_id = process_counter_track.id
            JOIN process ON process_counter_track.upid = process.upid
            WHERE process_counter_track.name = '$counter' AND process.name = '$process'
            ORDER BY counter.ts
            LIMIT 1
            """.trimIndent()
        )
        val durationUs = checkNotNull(rows.firstOrNull()) {
            "Missing histogram counter $counterName in ${captureInfo.targetPackageName}"
        }.double("value")
        check(durationUs.isFinite() && durationUs >= 0) {
            "Invalid duration for $counterName: $durationUs"
        }
        return listOf(Measurement("${counterName}Ms", durationUs / 1_000.0))
    }
}
