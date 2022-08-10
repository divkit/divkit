package com.yandex.div.core.histogram

import java.util.concurrent.TimeUnit

class NoOpHistogramBridge : HistogramBridge {

    override fun recordBooleanHistogram(name: String, sample: Boolean) = Unit

    override fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int) = Unit

    override fun recordLinearCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int) = Unit

    override fun recordCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int) = Unit

    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: TimeUnit,
        bucketCount: Long
    ) = Unit

    override fun recordSparseSlowlyHistogram(name: String, sample: Int) = Unit
}
