package com.yandex.divkit.demo.screenshot

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import androidx.test.espresso.IdlingResource
import java.io.Closeable

class ComposeSnapshotIdlingResource(
    private val quietPeriodMillis: Long = DEFAULT_QUIET_PERIOD_MS,
) : IdlingResource, Closeable {

    private val handler = Handler(Looper.getMainLooper())
    private var observer: ObserverHandle? = null
    @Volatile
    private var lastChangeUptime = SystemClock.uptimeMillis()
    private var callback: IdlingResource.ResourceCallback? = null

    /** Starts observing snapshot applies. Call as early as possible so no change is missed. */
    fun start() {
        if (observer != null) return
        lastChangeUptime = SystemClock.uptimeMillis()
        observer = Snapshot.registerApplyObserver { _, _ ->
            lastChangeUptime = SystemClock.uptimeMillis()
        }
    }

    override fun getName(): String = "ComposeSnapshotIdlingResource"

    override fun isIdleNow(): Boolean {
        val idle = isQuiet()
        if (!idle) {
            // Reschedule a check so we still transition to idle after the quiet period even when the
            // looper has no other pending work (Choreographer frames may have stopped).
            handler.removeCallbacks(idleCheck)
            handler.postDelayed(idleCheck, POLL_INTERVAL_MS)
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }

    override fun close() {
        handler.removeCallbacks(idleCheck)
        observer?.dispose()
        observer = null
    }

    private val idleCheck = object : Runnable {
        override fun run() {
            if (isQuiet()) {
                callback?.onTransitionToIdle()
            } else {
                handler.postDelayed(this, POLL_INTERVAL_MS)
            }
        }
    }

    private fun isQuiet(): Boolean =
        observer != null && SystemClock.uptimeMillis() - lastChangeUptime >= quietPeriodMillis

    private companion object {
        const val POLL_INTERVAL_MS = 16L
        const val DEFAULT_QUIET_PERIOD_MS = 64L
    }
}
