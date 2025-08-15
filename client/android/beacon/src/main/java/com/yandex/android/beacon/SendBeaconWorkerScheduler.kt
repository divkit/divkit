package com.yandex.android.beacon

import androidx.annotation.AnyThread

/**
 * Callback to schedule worker's new routine.
 */
interface SendBeaconWorkerScheduler {

    @AnyThread
    fun schedule(worker: SendBeaconWorker)
}
