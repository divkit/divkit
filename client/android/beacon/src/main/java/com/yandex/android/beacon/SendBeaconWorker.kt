package com.yandex.android.beacon

import androidx.annotation.AnyThread
import androidx.annotation.MainThread

/**
 * Send beacon worker interface definition.
 */
interface SendBeaconWorker {

    /**
     * Starts worker.
     *
     * @param callback callback to call after worker finishes its routine.
     * @return `true`, if worker started, `false` if already working on its tasks.
     */
    @MainThread
    fun onStart(callback: Callback): Boolean

    /**
     * Stops worker.
     *
     * @return `true`, if worker started, `false` if already working on its tasks.
     */
    @MainThread
    fun onStop(): Boolean

    /**
     * Callback to pass to [.onStart] in order to receive result notification.
     */
    fun interface Callback {
        /**
         * Notifies about current beacons batch processing is finished.
         *
         * @param needsReschedule `true`, if some beacons was not sent,
         * `false` otherwise.
         */
        @AnyThread
        fun finish(needsReschedule: Boolean)
    }
}
