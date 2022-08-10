package com.yandex.divkit.demo.beacon

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.yandex.android.beacon.SendBeaconWorker
import com.yandex.android.beacon.SendBeaconWorkerScheduler
import com.yandex.div.core.DivKit
import com.yandex.div.core.util.KLog
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class SendBeaconWorkerSchedulerImpl(
    private val context: Context
) : SendBeaconWorkerScheduler {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    override fun schedule(worker: SendBeaconWorker) {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWork>()
            .setConstraints(workConstraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 2L, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, workRequest)
    }

    class ScheduleWork(
        context: Context,
        params: WorkerParameters,
    ) : CoroutineWorker(context, params) {

        override suspend fun doWork(): Result {
            val divKit = DivKit.getInstance(applicationContext)
            val sendBeaconManager = divKit.sendBeaconManager ?: return Result.failure()

            val needsReschedule = suspendWorkerCallback { callback ->
                KLog.i(TAG) { "Starting sendBeacon worker" }
                sendBeaconManager.onStart(callback)
            }

            KLog.i(TAG) { "Stopping sendBeacon worker" }
             sendBeaconManager.onStop()

            return if (needsReschedule) {
                KLog.i(TAG) { "Reschedule sendBeacon worker" }
                Result.retry()
            } else {
                Result.success()
            }
        }

        private suspend fun suspendWorkerCallback(block: (SendBeaconWorker.Callback) -> Unit): Boolean {
            return suspendCancellableCoroutine { continuation ->
                block(SendBeaconWorker.Callback { needsReschedule -> continuation.resume(needsReschedule) })
            }
        }
    }

    private companion object {
        private const val TAG = "SendBeaconWorkerSchedulerImpl"
        private const val WORK_NAME = "SendBeaconSchedule"
    }
}
