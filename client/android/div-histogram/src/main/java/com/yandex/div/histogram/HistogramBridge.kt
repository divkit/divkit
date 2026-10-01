package com.yandex.div.histogram

import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toTimeUnit

/**
 * Common interface to implement switchable histogram consumers for {@link HistogramRecorder}.
 */
interface HistogramBridge : com.yandex.div.core.histogram.HistogramBridge {

    /**
     * Records a sample in a boolean histogram.
     * @see HistogramRecorder#recordBooleanHistogram(String, boolean)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded, either true or false
     */
    abstract override fun recordBooleanHistogram(name: String, sample: Boolean)

    /**
     * Records a sample in an enumerated histogram.
     * @see HistogramRecorder#recordEnumeratedHistogram(String, int, int)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 0 and at most |boundary| - 1
     * @param boundary upper bound for legal sample values - all sample values have to be strictly lower than |boundary|
     */
    abstract override fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int)

    /**
     * Records a sample in a linear histogram.
     * @see HistogramRecorder#recordLinearCountHistogram(String, int, int, int, int)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded, at least |min| and at most |max| - 1.
     * @param min lower bound for expected sample values, should be at least 1.
     * @param max upper bounds for expected sample values
     * @param bucketCount the number of buckets
     */
    abstract override fun recordLinearCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int)

    /**
     * Records a sample in a count histogram.
     * @see HistogramRecorder#recordCustomCountHistogram(String, int, int, int, int)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded, at least |min| and at most |max| - 1
     * @param min lower bound for expected sample values. It must be >= 1
     * @param max upper bounds for expected sample values
     * @param bucketCount the number of buckets
     */
    abstract override fun recordCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int)

    /**
     * Records a sample in a histogram of times.
     * @see HistogramRecorder#recordCustomTimeHistogram(String, long, long, long, TimeUnit, int)
     *
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param min the minimum bucket value
     * @param max the maximum bucket value
     * @param unit the unit of the duration, min, and max arguments
     * @param bucketCount the number of buckets
     */
    fun recordTimeHistogram(name: String, duration: Long, min: Long, max: Long, unit: TimeUnit, bucketCount: Int)

    /**
     * Records a sample in a histogram of times.
     * @see HistogramRecorder#recordCustomTimeHistogram(String, long, long, long, TimeUnit, int)
     *
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param min the minimum bucket value
     * @param max the maximum bucket value
     * @param unit the unit of the duration, min, and max arguments
     * @param bucketCount the number of buckets
     */
    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: DurationUnit,
        bucketCount: Int
    ) {
        recordTimeHistogram(name, duration, min, max, unit.toTimeUnit(), bucketCount)
    }

    /**
     * Records a sample in a sparse histogram.
     * @see HistogramRecorder#recordSparseSlowlyHistogram(String, int)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded. All values of |sample| are valid, including negative values.
     */
    abstract override fun recordSparseSlowlyHistogram(name: String, sample: Int)
}
