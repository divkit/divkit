package com.yandex.test.util

import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

private const val MIN_SLEEP_TIME_MS = 1L
private const val MAX_SLEEP_TIME_MS = 100L

@Throws(InterruptedException::class)
internal fun Process.waitFor(timeout: Long, unit: TimeUnit, block: () -> Unit): Boolean {
    var remainingNs = unit.toNanos(timeout)
    val deadlineNs = System.nanoTime() + remainingNs

    do {
        try {
            block()
            exitValue()
            return true
        } catch (e: IllegalThreadStateException) {
            if (remainingNs > 0) {
                val remainingMs = TimeUnit.NANOSECONDS.toMillis(remainingNs)
                sleep(remainingMs.coerceIn(MIN_SLEEP_TIME_MS, MAX_SLEEP_TIME_MS))
            }
            remainingNs = deadlineNs - System.nanoTime()
        }
    } while (remainingNs > 0)
    return false
}
