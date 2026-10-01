package com.yandex.div.core.histogram

import kotlin.time.DurationUnit

/**
 * Common interface to implement histogram consumers.
 */
public interface HistogramBridge {
    /**
     * Records a sample in a boolean histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, either true or false
     */
    public fun recordBooleanHistogram(name: String, sample: Boolean)

    /**
     * Records a sample in an enumerated histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 0 and at most |boundary| - 1
     * @param boundary upper bound for legal sample values - all sample values have to be strictly lower than |boundary|
     */
    public fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int)

    /**
     * Records a sample in a linear histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least |min| and at most |max| - 1.
     * @param min lower bound for expected sample values, should be at least 1.
     * @param max upper bounds for expected sample values
     * @param bucketCount the number of buckets
     */
    public fun recordLinearCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int)

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least |min| and at most |max| - 1
     * @param min lower bound for expected sample values. It must be >= 1
     * @param max upper bounds for expected sample values
     * @param bucketCount the number of buckets
     */
    public fun recordCountHistogram(name: String, sample: Int, min: Int, max: Int, bucketCount: Int)

    /**
     * Records a sample in a histogram of times.
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param min the minimum bucket value
     * @param max the maximum bucket value
     * @param unit the unit of the duration, min, and max arguments
     * @param bucketCount the number of buckets
     */
    public fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: DurationUnit,
        bucketCount: Int
    )

    /**
     * Records a sample in a sparse histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded. All values of |sample| are valid, including negative values.
     */
    public fun recordSparseSlowlyHistogram(name: String, sample: Int)
}
