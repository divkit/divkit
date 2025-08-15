package com.yandex.div.histogram;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * API for recording UMA histograms.
 */
@SuppressWarnings("unused")
public class HistogramRecorder {

    @NonNull
    private final HistogramBridge mBridge;

    public HistogramRecorder(@NonNull HistogramBridge bridge) {
        mBridge = bridge;
    }

    /**
     * Records a sample in a boolean histogram of the given name. Boolean histogram has two
     * buckets, corresponding to success (true) and failure (false).
     * @param name name of the histogram
     * @param sample sample to be recorded, either true or false
     */
    public void recordBooleanHistogram(@NonNull String name, boolean sample) {
        mBridge.recordBooleanHistogram(name, sample);
    }

    /**
     * Records a sample in an enumerated histogram of the given name and boundary. Note that
     * |boundary| identifies the histogram - it should be the same at every invocation.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 0 and at most |boundary| - 1
     * @param boundary upper bound for legal sample values - all sample values have to be strictly lower than |boundary|
     */
    public void recordEnumeratedHistogram(@NonNull String name, int sample, int boundary) {
        mBridge.recordEnumeratedHistogram(name, sample, boundary);
    }

    /**
     * Records a sample in a percentage histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 0 and at most 100.
     */
    public void recordPercentageHistogram(@NonNull String name, int sample) {
        mBridge.recordLinearCountHistogram(name, sample, 1, 101, 102);
    }

    /**
     * Records a sample in a linear histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least |min| and at most |max| - 1.
     * @param min lower bound for expected sample values, should be at least 1.
     * @param max upper bounds for expected sample values
     * @param bucketCount the number of buckets
     */
    public void recordLinearCountHistogram(@NonNull String name, int sample, int min, int max, int bucketCount) {
        mBridge.recordLinearCountHistogram(name, sample, min, max, bucketCount);
    }

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 1 and at most 99
     */
    public void recordCount100Histogram(@NonNull String name, int sample) {
        mBridge.recordCountHistogram(name, sample, 1, 100, 50);
    }

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 1 and at most 999
     */
    public void recordCount1KHistogram(@NonNull String name, int sample) {
        mBridge.recordCountHistogram(name, sample, 1, 1_000, 50);
    }

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 1 and at most 9999
     */
    public void recordCount10KHistogram(@NonNull String name, int sample) {
        mBridge.recordCountHistogram(name, sample, 1, 10_000, 50);
    }

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 1 and at most 99999
     */
    public void recordCount100KHistogram(@NonNull String name, int sample) {
        mBridge.recordCountHistogram(name, sample, 1, 100_000, 50);
    }

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least 1 and at most 999999
     */
    public void recordCount1MHistogram(@NonNull String name, int sample) {
        mBridge.recordCountHistogram(name, sample, 1, 1_000_000, 50);
    }

    /**
     * Records a sample in a count histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded, at least |min| and at most |max| - 1
     * @param min lower bound for expected sample values. It must be >= 1
     * @param max upper bounds for expected sample values
     * @param bucketCount the number of buckets
     */
    public void recordCustomCountHistogram(@NonNull String name, int sample, int min, int max, int bucketCount) {
        mBridge.recordCountHistogram(name, sample, min, max, bucketCount);
    }

    /**
     * Records a sample in a histogram of times. Useful for recording short durations.
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param unit the unit of the duration argument
     */
    public void recordShortTimeHistogram(@NonNull String name, long duration, @NonNull TimeUnit unit) {
        mBridge.recordTimeHistogram(name, unit.toMillis(duration), 1L, 10_000L, TimeUnit.MILLISECONDS, 50);
    }

    /**
     * Records a sample in a histogram of times. Useful for recording medium durations.
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param unit the unit of the duration argument
     */
    public void recordMediumTimeHistogram(@NonNull String name, long duration, @NonNull TimeUnit unit) {
        mBridge.recordTimeHistogram(name, unit.toMillis(duration), 1L, 180_000L, TimeUnit.MILLISECONDS, 50);
    }

    /**
     * Records a sample in a histogram of times. Useful for recording long durations.
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param unit the unit of the duration argument
     */
    public void recordLongTimeHistogram(@NonNull String name, long duration, @NonNull TimeUnit unit) {
        mBridge.recordTimeHistogram(name, unit.toMillis(duration), 1L, 3_600_000L, TimeUnit.MILLISECONDS, 50);
    }

    /**
     * Records a sample in a histogram of times with custom buckets.
     * @param name name of the histogram
     * @param duration duration to be recorded
     * @param min the minimum bucket value
     * @param max the maximum bucket value
     * @param unit the unit of the duration, min, and max arguments
     * @param bucketCount the number of buckets
     */
    public void recordCustomTimeHistogram(@NonNull String name, long duration, long min, long max, @NonNull TimeUnit unit, int bucketCount) {
        mBridge.recordTimeHistogram(name, unit.toMillis(duration), unit.toMillis(min), unit.toMillis(max), TimeUnit.MILLISECONDS, bucketCount);
    }

    /**
     * Records a sample in a histogram of sizes in KB. Good for sizes up to about 500MB.
     * @param name name of the histogram
     * @param sizeKb sample to be recorded in KB
     */
    public void recordMemoryKbHistogram(@NonNull String name, int sizeKb) {
        mBridge.recordCountHistogram(name, sizeKb, 1000, 500_000, 50);
    }

    /**
     * Records a sample in a histogram of sizes in MB. Good for sizes up to about 1GB.
     * @param name name of the histogram
     * @param sizeMb sample to be recorded in MB
     */
    public void recordMemoryMbHistogram(@NonNull String name, int sizeMb) {
        mBridge.recordCountHistogram(name, sizeMb, 1, 1_000, 50);
    }

    /**
     * Records a sample in a histogram of large sizes in MB. Good for sizes up to about 64GB.
     * @param name name of the histogram
     * @param sizeMb sample to be recorded in MB
     */
    public void recordLargeMemoryMbHistogram(@NonNull String name, int sizeMb) {
        mBridge.recordCountHistogram(name, sizeMb, 1, 64_000, 100);
    }

    /**
     * Records a sample in a sparse histogram.
     * @param name name of the histogram
     * @param sample sample to be recorded. All values of |sample| are valid, including negative values.
     */
    public void recordSparseSlowlyHistogram(@NonNull String name, int sample) {
        mBridge.recordSparseSlowlyHistogram(name, sample);
    }
}
