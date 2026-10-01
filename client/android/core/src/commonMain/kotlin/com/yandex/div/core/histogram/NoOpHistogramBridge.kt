package com.yandex.div.core.histogram

import kotlin.time.DurationUnit

public class NoOpHistogramBridge : HistogramBridge {

    override fun recordBooleanHistogram(name: String, sample: Boolean): Unit = Unit

    override fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int): Unit = Unit

    override fun recordLinearCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ): Unit = Unit

    override fun recordCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int): Unit = Unit

    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: DurationUnit,
        bucketCount: Int
    ): Unit = Unit

    override fun recordSparseSlowlyHistogram(name: String, sample: Int): Unit = Unit
}
