package com.yandex.div.rule

import android.os.SystemClock
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.test.idling.SimpleIdlingResource

class ComposeIdlingResource(
    private val activityRule: ActivityScenarioRule<DivComposeScreenshotActivity>,
    private val quietPeriodMillis: Long = DEFAULT_QUIET_PERIOD_MS,
) : SimpleIdlingResource(pollingIntervalMillis = 16, description = "ComposeIdlingResource") {

    private var observer: ObserverHandle? = null
    @Volatile
    private var lastChangeUptime = SystemClock.uptimeMillis()

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        super.registerIdleTransitionCallback(resourceCallback)
        lastChangeUptime = SystemClock.uptimeMillis()
        observer = Snapshot.registerApplyObserver { _, _ ->
            lastChangeUptime = SystemClock.uptimeMillis()
        }
    }

    override fun checkIdle(): Boolean {
        var isIdle = false
        activityRule.scenario.onActivity {
            isIdle = it.imageLoadingTracker.isIdle
        }
        return isIdle && SystemClock.uptimeMillis() - lastChangeUptime >= quietPeriodMillis
    }

    override fun close() {
        super.close()
        observer?.dispose()
        observer = null
    }

    private companion object {
        const val DEFAULT_QUIET_PERIOD_MS = 64L
    }
}
