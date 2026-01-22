package com.yandex.div.histogram.util

import com.yandex.div.internal.util.Clock
import java.util.concurrent.TimeUnit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Use this clock for recording all duration-related histograms to
 * keep time units synced.
 */
object HistogramClock {
    fun uptime(c: Clock = Clock.get()): Long = c.uptimeMicros

    val durationUnit: TimeUnit = TimeUnit.MICROSECONDS

    @OptIn(ExperimentalContracts::class)
    inline fun measureTime(c: Clock = Clock.get(), block: () -> Unit): Long {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val start = uptime(c)
        block()
        return uptime(c) - start
    }

}
