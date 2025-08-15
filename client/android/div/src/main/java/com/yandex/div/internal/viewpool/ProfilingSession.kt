package com.yandex.div.internal.viewpool

import androidx.annotation.VisibleForTesting

internal class ProfilingSession {

    private val mOverallTimeAccumulator = Accumulator()
    private val mLongRequestAccumulator = Accumulator()
    private val mBlockedViewAccumulators = androidx.collection.ArrayMap<String, Accumulator>()

    companion object {

        private const val LONG_OPERATION_THRESHOLD_NS = 1_000_000L

        private fun toMicroseconds(timeNs: Long): Long = timeNs / 1000L
    }

    fun viewObtainedWithoutBlock(durationNs: Long) {
        mOverallTimeAccumulator.addAndIncrement(durationNs)
    }

    fun viewObtainedWithBlock(viewName: String, durationNs: Long) {
        mOverallTimeAccumulator.addAndIncrement(durationNs)

        val blockedViewAccumulator: Accumulator = mBlockedViewAccumulators.getOrPut(viewName) { Accumulator() }
        blockedViewAccumulator.addAndIncrement(durationNs)
    }

    fun viewRequested(durationNs: Long) {
        mOverallTimeAccumulator.add(durationNs)
        if (durationNs >= LONG_OPERATION_THRESHOLD_NS) mLongRequestAccumulator.addAndIncrement(durationNs)
    }

    fun hasLongEvents(): Boolean {
        if (mLongRequestAccumulator.count > 0) return true

        for ((_, acc) in mBlockedViewAccumulators) {
            if (acc.count > 0) return true
        }

        return false
    }

    fun flush(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result["view obtaining - total count"] = mOverallTimeAccumulator.count
        result["view obtaining - total time (µs)"] = toMicroseconds(mOverallTimeAccumulator.accumulated).roundRoughly()

        for ((name, acc) in mBlockedViewAccumulators) {
            if (acc.count > 0) {
                result["blocking view obtaining for $name - count"] = acc.count
                result["blocking view obtaining for $name - avg time (µs)"] = toMicroseconds(acc.avg).roundRoughly()
            }
        }

        if (mLongRequestAccumulator.count > 0) {
            result["long view requests - count"] = mLongRequestAccumulator.count
            result["long view requests - avg time (µs)"] = toMicroseconds(mLongRequestAccumulator.avg).roundRoughly()
        }

        return result
    }

    fun clear() {
        mOverallTimeAccumulator.reset()
        mLongRequestAccumulator.reset()
        mBlockedViewAccumulators.forEach { (_, acc) -> acc.reset() }
    }

    private class Accumulator {

        var accumulated = 0L
            private set

        var count = 0
            private set

        val avg: Long
            get() = if (count == 0) 0 else accumulated / count

        fun add(value: Long) {
            accumulated += value
        }

        fun addAndIncrement(value: Long) {
            add(value)
            count++
        }

        fun reset() {
            accumulated = 0L
            count = 0
        }
    }
}

/**
 * Returns the largest value that is less than or equal to the
 * argument and is divisible by a some factor. Factor can be:
 * <ol>
 *     <li>20 - value in [0, 100)</li>
 *     <li>100 - value in [100, 1000)</li>
 *     <li>200 - value in [1000, 2000)</li>
 *     <li>500 - value in [2000, 5000)</li>
 *     <li>1000 - value in [5000, 10000)</li>
 *     <li>2000 - value in [10000, 20000)</li>
 *     <li>5000 - value in [20000, 50000)</li>
 * </ol>
 *
 * Special cases: it always returns 0 for negative values and 50000 for values greater than 50000.
 */
@VisibleForTesting
internal fun Long.roundRoughly(): Long {
    val rounded = when {
        this < 0L  -> 0L
        this < 100L  -> this floorTo 20L
        this < 1000L -> this floorTo 100L
        this < 2000L -> this floorTo 200L
        this < 5000L -> this floorTo 500L
        this < 10000L -> this floorTo 1000L
        this < 20000L -> this floorTo 2000L
        this < 50000L -> this floorTo 5000L
        else -> 50000L
    }
    return rounded
}

private infix fun Long.floorTo(e: Long) = (this / e) * e
