package com.yandex.div.core.histogram;

import androidx.annotation.NonNull;
import com.yandex.div.core.annotations.PublicApi;
import java.util.concurrent.TimeUnit;

/**
 * Common interface to implement switchable histogram consumers for {@link HistogramRecorder}.
 */
@PublicApi
public interface HistogramBridge {

    /**
     * Records a sample in a boolean histogram.
     * @see HistogramRecorder#recordBooleanHistogram(String, boolean)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded, either true or false
     */
    void recordBooleanHistogram(@NonNull String name, boolean sample);

    /**
     * Records a sample in an enumerated histogram.
     * @see HistogramRecorder#recordEnumeratedHistogram(String, int, int)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 0 and at most |boundary| - 1
     * @param boundary upper bound for legal sample values - all sample values have to be strictly lower than |boundary|
     */
    void recordEnumeratedHistogram(@NonNull String name, int sample, int boundary);

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
    void recordLinearCountHistogram(@NonNull String name, int sample, int min, int max, int bucketCount);

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
    void recordCountHistogram(@NonNull String name, int sample, int min, int max, int bucketCount);

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
    default void recordTimeHistogram(@NonNull String name, long duration, long min, long max, @NonNull TimeUnit unit, int bucketCount) {
        recordTimeHistogram(name, duration, min, max, unit, (long) bucketCount);
    }

    @Deprecated
    void recordTimeHistogram(@NonNull String name, long duration, long min, long max, @NonNull TimeUnit unit, long bucketCount);

    /**
     * Records a sample in a sparse histogram.
     * @see HistogramRecorder#recordSparseSlowlyHistogram(String, int)
     *
     * @param name name of the histogram
     * @param sample sample to be recorded. All values of |sample| are valid, including negative values.
     */
    void recordSparseSlowlyHistogram(@NonNull String name, int sample);
}
