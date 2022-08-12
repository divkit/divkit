package com.yandex.android.beacon

import android.content.Context
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.yandex.android.beacon.BeaconItem.NonPersistent
import com.yandex.android.net.CookieStorage
import com.yandex.div.core.util.Assert
import com.yandex.div.core.util.Log
import com.yandex.div.util.Clock
import com.yandex.div.util.SingleThreadExecutor
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayDeque
import java.util.Deque
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * A base of {@link SendBeaconManager} subsystem.
 * <p>
 * It is driven by {@link #onStart(Callback)} and {@link #onStop()} methods plus
 * it can try to send the request one time right away, given that some network-using activity
 * is responsible for this. The request are stores in SQL DB and will be retried also after
 * the app restart. There is a time expiration for requests that should help us to get rid of
 * malformed urls that are never to be successfully delivered.
 * <p>
 * All real work is done in a 'worker thread', which is a synthetic over a multi-thread executor.
 * If Android requires the work be stopped, it happens in-between sending requests, there is not
 * cancelling of a request already started.
 */
internal class SendBeaconWorkerImpl(
    private val context: Context,
    private val configuration: SendBeaconConfiguration
) : SendBeaconWorker {

    private val workerThreadExecutor = WorkerThreadExecutor(configuration.executor)
    private val implThread = ImplThread()
    private val runningJob = AtomicReference<RunningJob?>(null)

    private val requestExecutor: SendBeaconRequestExecutor
        get() = configuration.requestExecutor

    private val hostCallback: SendBeaconWorkerScheduler
        get() = configuration.workerScheduler

    private val extraLogger: SendBeaconPerWorkerLogger
        get() = configuration.perWorkerLogger

    // Update from worker thread
    @Volatile
    private var hasMoreWork: Boolean? = null

    init {
        Log.d(TAG, "SendBeaconWorker created")
    }

    /**
     * @param tryImmediately true if we want to send the url without starting a service.
     */
    fun add(url: Uri, headers: Map<String, String>, payload: JSONObject?, tryImmediately: Boolean) {
        Log.d(TAG, "Adding url $url")
        workerThreadExecutor.post {
            implThread.addUrl(url, headers, payload, tryImmediately)
        }
    }

    fun addNonPersistentUrl(
        url: Uri,
        headers: Map<String, String>,
        cookieStorage: CookieStorage,
        payload: JSONObject?,
        tryImmediately: Boolean
    ) {
        Log.d(TAG, "Adding non persistent url $url")
        workerThreadExecutor.post {
            implThread.addNonPersistentUrl(url, headers, cookieStorage, payload, tryImmediately)
        }
    }

    override fun onStart(callback: SendBeaconWorker.Callback): Boolean {
        Log.d(TAG, "Starting job")

        if (hasMoreWork == false) {
            Log.d(TAG, "Starting job, return false")
            return false
        }
        val newJob = RunningJob(callback)
        val previousJob = runningJob.getAndSet(newJob)
        Assert.assertNull(previousJob)

        workerThreadExecutor.post {
            implThread.executeJob(newJob)
        }
        Log.d(TAG, "Starting job, return true")
        return true
    }

    override fun onStop(): Boolean {
        Log.d(TAG, "Stopping job")

        // Android may or may not call stopJob if we called jobFinished.

        // First stop the job, then check if it has more work. Otherwise the worker thread can
        // add declare work (which we miss) and not schedule, because the job is still running.
        runningJob.set(null)

        // Our returning true here – is it going to overpower a previous back-off
        // request from other thread?
        val result = hasMoreWork != false
        Log.d(TAG, "Stopping job: $result")
        return result
    }

    private class WorkerThreadExecutor(
        executor: Executor
    ) : SingleThreadExecutor(executor, "SendBeacon") {

        override fun handleError(e: RuntimeException) = Unit // TODO
    }

    private class RunningJob(private val callback: SendBeaconWorker.Callback) {

        fun sendFinishToCallback(backingOff: Boolean) {
            callback.finish(backingOff)
        }
    }

    @WorkerThread
    private inner class ImplThread {

        private val workerData by lazy { WorkerData(context, configuration.databaseName) }

        fun addUrl(url: Uri, headers: Map<String, String>, payload: JSONObject?, tryImmediately: Boolean) {
            val nowMs = Clock.get().currentTimeMs

            // Initialization can be in this call.
            val beaconData = workerData.push(url, headers, nowMs, payload)
            addBeaconItem(tryImmediately, workerData, beaconData)
        }

        fun addNonPersistentUrl(
            url: Uri,
            headers: Map<String, String>,
            cookieStorage: CookieStorage,
            payload: JSONObject?,
            tryImmediately: Boolean
        ) {
            val nowMs = Clock.get().currentTimeMs

            // Initialization can be in this call.
            val beaconData = workerData.pushNonPersistent(url, headers, nowMs, cookieStorage, payload)
            addBeaconItem(tryImmediately, workerData, beaconData)
        }

        private fun addBeaconItem(tryImmediately: Boolean, workerData: WorkerData, beaconData: BeaconItem) {
            if (tryImmediately) {
                val sendResult = sendItem(beaconData)
                if (sendResult) {
                    workerData.pop()
                    return
                }
            }

            val job = runningJob.get()
            if (job == null) {
                hostCallback.schedule(this@SendBeaconWorkerImpl)
            } else {
                // Job is already scheduled from the main thread, but wasn't yet executed here.
                // No need for schedule.
            }
        }

        fun executeJob(job: RunningJob) {
            try {
                proceedJobImpl()
            } finally {
                val changed: Boolean = runningJob.compareAndSet(job, null)
                if (changed) {
                    val backingOff: Boolean
                    if (hasMoreWork == false) {
                        Log.d(TAG, "Finishing job")
                        backingOff = false
                    } else {
                        Log.d(TAG, "Giving up in the end")
                        backingOff = true
                    }
                    job.sendFinishToCallback(backingOff)
                }
            }
        }

        private fun proceedJobImpl() {
            // Initialization can be in this call.
            val nowMs = Clock.get().currentTimeMs

            val it = workerData.iterator()
            while (it.hasNext()) {
                val item = it.next()
                if (runningJob.get() == null) return

                if (item.addTimestamp + URL_EXPIRE_PERIOD_MS < nowMs) {
                    Log.w(TAG, "Drop outdated url: " + item.url)
                    it.remove()
                    continue
                }

                Log.d(TAG, "Trying to send " + item.url)
                val sendResult = sendItem(item)
                Log.d(TAG, "Trying to send, result $sendResult")
                if (sendResult) {
                    it.remove()
                }
            }
        }

        private fun sendItem(beaconData: BeaconItem): Boolean {
            val request = SendBeaconRequest.from(beaconData)
            val url = beaconData.url

            val s = request.url.toString()
            extraLogger.onTrySendUrl(s)

            return try {
                val response = requestExecutor.execute(request)
                when {
                    response.isValid() -> {
                        extraLogger.onSuccessSendUrl(s)
                        Log.d(TAG, "Sent url ok $url")
                        true
                    }

                    is5xxHttpCode(response) -> {
                        extraLogger.onFailedSendUrlDueServerError(s)
                        Log.e(TAG, "Failed to send url $url, but treat as sent.")
                        true
                    }

                    else -> {
                        extraLogger.onFailedSendUrl(s, false)
                        Log.e(TAG, "Failed to send url $url")
                        false
                    }
                }
            } catch (e: IOException) {
                extraLogger.onFailedSendUrl(s, true)
                Log.e(TAG, "Failed to send url $url", e)
                false
            }
        }

        private fun is5xxHttpCode(response: SendBeaconResponse): Boolean {
            return response.responseCode / 100 == 5
        }
    }

    @WorkerThread
    private inner class WorkerData(
        context: Context,
        databaseName: String
    ): Iterable<BeaconItem> {

        private val db = SendBeaconDb.factory.create(context, databaseName)
        private val itemCache: Deque<BeaconItem> = ArrayDeque(db.allItems())

        init {
            Log.e(TAG, "Reading from database, items count: " + itemCache.size)
            updateHasMoreWork()
        }

        fun push(url: Uri, headers: Map<String, String>, nowMs: Long, payload: JSONObject?): BeaconItem {
            val item = db.add(url, headers, nowMs, payload)
            itemCache.push(item)
            updateHasMoreWork()
            return item
        }

        fun pushNonPersistent(
            url: Uri,
            headers: Map<String, String>,
            nowMs: Long,
            cookieStorage: CookieStorage,
            payload: JSONObject?
        ): BeaconItem {
            val item = NonPersistent(url, headers, payload, nowMs, cookieStorage)
            itemCache.push(item)
            updateHasMoreWork()
            return item
        }

        fun pop() {
            val item = itemCache.pop()
            db.remove(item.asPersistent())
            updateHasMoreWork()
        }

        override fun iterator(): MutableIterator<BeaconItem> {
            val it = itemCache.iterator()
            return object : MutableIterator<BeaconItem> {

                var last: BeaconItem? = null

                override fun hasNext(): Boolean {
                    return it.hasNext()
                }

                override fun next(): BeaconItem {
                    val item = it.next()
                    last = item
                    return item
                }

                override fun remove() {
                    it.remove()
                    db.remove(last?.asPersistent())
                    updateHasMoreWork()
                }
            }
        }

        private fun updateHasMoreWork() {
            hasMoreWork = !itemCache.isEmpty()
        }
    }

    companion object {

        private const val TAG = "SendBeaconWorker"

        @VisibleForTesting
        @JvmField
        internal val URL_EXPIRE_PERIOD_MS = TimeUnit.DAYS.toMillis(1) // 1 day.
    }
}
