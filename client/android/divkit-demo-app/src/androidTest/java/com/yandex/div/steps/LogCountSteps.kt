package com.yandex.div.steps

import com.yandex.div.internal.KLog
import com.yandex.div.internal.LogListener
import com.yandex.test.util.StepsDsl

internal fun logCount(
    logTag: String,
    containsMessage: String,
    f: LogCountSteps.() -> Unit,
) = f(LogCountSteps(logTag, containsMessage))

@StepsDsl
class LogCountSteps(
    logTag: String,
    containsMessage: String,
) {
    private var logCount = 0
    private val logListener = object : LogListener {
        override fun onNewMessage(priority: Int, tag: String, message: String) {
            if (tag == logTag && message.contains(containsMessage)) {
                logCount++
            }
        }
    }

    init {
        KLog.listeners.add(logListener)
    }

    fun assert(f: LogCountAssertions.() -> Unit) = f(LogCountAssertions(logCount, logListener))
}

@StepsDsl
class LogCountAssertions(
    private val logCount: Int,
    private val logListener: LogListener,
) {

    fun logCountLessOrEquals(max: Int) {
        KLog.listeners.remove(logListener)

        assert(logCount <= max) { "$MESSAGE_TOO_MANY_FAILS: $logCount" }
    }

    fun logCountEquals(expected: Int) {
        KLog.listeners.remove(logListener)

        assert(logCount == expected) { "$MESSAGE_TOO_MANY_FAILS: $logCount" }
    }

    companion object {
        private const val MESSAGE_TOO_MANY_FAILS = "Too many holder item reuse fails"
    }
}
