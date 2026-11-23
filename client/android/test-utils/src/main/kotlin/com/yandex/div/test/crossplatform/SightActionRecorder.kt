package com.yandex.div.test.crossplatform

import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData.Step

/**
 * Records `log_id` of visibility and disappear actions performed since the scenario started
 * and checks them in `verify_log` steps.
 */
class SightActionRecorder {

    private val actions = mutableListOf<Action>()

    fun record(source: Step.VerifyLog.Source, logId: String) {
        actions += Action(source, logId)
    }

    /** Whether waiting longer cannot change the result: `count: 0` is checked at once. */
    fun isSettled(step: Step.VerifyLog): Boolean = countOf(step) >= (step.count ?: 1)

    fun verify(step: Step.VerifyLog) {
        val actual = countOf(step)
        val expected = step.count
        if (expected?.let { actual == it } ?: (actual > 0)) {
            return
        }
        throw AssertionError(
            "verify_log expected ${expected?.let { "exactly $it" } ?: "at least one"} event(s) " +
                "with source=${step.source}, id=${step.id}; observed $actual; recorded: $actions"
        )
    }

    private fun countOf(step: Step.VerifyLog) =
        actions.count { it.source == step.source && it.logId == step.id }

    private class Action(val source: Step.VerifyLog.Source, val logId: String) {
        override fun toString() = "$source '$logId'"
    }

    companion object {
        // Longer than the longest action delay in the shared scenarios, 4000 ms.
        const val TIMEOUT_MS = 5_000L
        const val POLL_INTERVAL_MS = 50L
    }
}
