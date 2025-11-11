package com.yandex.div.core.util.hotreload

import androidx.annotation.WorkerThread
import com.yandex.div.DivDataTag
import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

internal class HotReloadController(
    private val divView: Div2View,
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
) {
    var host: String = "10.0.2.2"
    val receivePort: Int = 7969
    val sendPort: Int = 7970

    private val notificationObservers = ObserverList<(HotReloadStatus) -> Unit>()
    private var appliedJsonHash: Int = 0

    var active: Boolean = false
        set(value) {
            field = value

            if (value) {
                start()
            } else {
                stop()
            }
        }
    private val isRunning = AtomicBoolean(false)
    private val originData = AtomicReference<DivData?>(null)

    private var pollingJob: ScheduledFuture<*>? = null
    private var pollingRunnable = Runnable {
        if (!isRunning.get()) {
            return@Runnable
        }

        doHotReload()
        scheduleNextHotReload()
    }

    private fun scheduleNextHotReload() {
        KLog.i(TAG) { "Scheduling next hot reload!" }
        pollingJob = executor.schedule(pollingRunnable, 2L, TimeUnit.SECONDS)
    }

    fun start() {
        if (isRunning.compareAndSet(false, true)) {
            KLog.i(TAG) { "Connecting to hot reload server: $host:$receivePort" }
            pollingJob?.cancel(false)
            pollingJob = null
            executor.execute(pollingRunnable)
        }
    }

    private fun stop() {
        if (isRunning.compareAndSet(true, false)) {
            KLog.i(TAG) { "Stopping hot reloading!" }
            pollingJob?.cancel(false)
            pollingJob = null
            divView.post {
                originData.get()?.let {
                    divView.setData(it, divView.dataTag)
                }
            }
        }
    }

    private fun doHotReload() {
        KLog.i(TAG) { "Connecting to socket server at $host:$receivePort" }

        val divJsonReceiver = DivJsonReceiver(host, receivePort)
        updateStatus(HotReloadStatus.Reloading)
        divJsonReceiver.captureDivJson()
            .onFailure {
                KLog.i(TAG, it) { "Hot reload failed!" }
                updateStatus(HotReloadStatus.Failure(it))
                it.printStackTrace()
            }
            .onSuccess {
                KLog.i(TAG) { "Hot reload success!" }
                updateDivFromJson(it)
            }

    }

    private fun updateStatus(status: HotReloadStatus) {
        notificationObservers.forEach { it.invoke(status) }
    }

    private fun updateDivFromJson(jsonString: String) {
        val errors = mutableListOf<Throwable>()
        val errorLogger = ParsingErrorLogger {
            errors.add(it)
        }

        val newJsonHash = jsonString.hashCode()

        if (newJsonHash == appliedJsonHash) {
            KLog.i(TAG) { "Skip hot reload since json now changed!"}
            // Add small delay to make shift between Reloading and Skipped noticable to user
            divView.postDelayed({
                updateStatus(HotReloadStatus.Skipped)
            }, 250L)
            return
        }

        val divData = parseJsonToDivData(jsonString, errorLogger)
            .onFailure {
                KLog.e(TAG, it) { "Error parsing JSON to DivData" }
                updateStatus(HotReloadStatus.Failure(it))
            }.getOrNull() ?: return

        val currentData: DivData? = divView.divData
        if (originData.get() == null && currentData != null) {
            KLog.i(TAG) { "DivView will send OWN div json to server" }
            sendOwnDivJsonToServer(currentData)
            KLog.i(TAG) { "DivView original data saved" }
            originData.set(currentData)
        }

        UiThreadHandler.executeOnMainThread {
            divView.clearSubscriptions()
            // Re-new tag to evade previous state.
            val dataTag = DivDataTag(UUID.randomUUID().toString())
            divView.setData(divData, dataTag)
            divView.viewComponent.errorCollectors.getOrNull(divView.dataTag, divData)
                ?.cleanRuntimeWarningsAndErrors()
            errors.forEach {
                logError(it)
            }
            appliedJsonHash = newJsonHash
            updateStatus(HotReloadStatus.Applied)
            KLog.i(TAG) { "DivView updated successfully" }
        }
    }

    private fun sendOwnDivJsonToServer(d: DivData) {
        KLog.i(TAG) { "Sending JSON to server at $host:$sendPort" }
        val divJson = d.writeToJSON()
        val sender = DivJsonSender(host, sendPort)
        sender.sendDivJson(divJson.toString()).onFailure {
            KLog.e(TAG, it) { "Error sending JSON to server" }
            it.printStackTrace()
            logError(it)
        }.onSuccess {
            KLog.i(TAG) { "Successfully sent JSON to server" }
        }
    }

    private fun logError(error: Throwable) {
        UiThreadHandler.executeOnMainThread {
            divView.logError(error)
        }
    }

    private fun parseJsonToDivData(
        jsonString: String,
        logger: ParsingErrorLogger,
    ): Result<DivData> = runCatching {
        val json = JSONObject(jsonString)
        val environment = DivParsingEnvironment(logger)
        when {
            // Case 1: { "card": {...}, "templates": {...} }
            json.has("card") && json.has("templates") -> {
                val templates = json.getJSONObject("templates")
                val card = json.getJSONObject("card")
                environment.parseTemplates(templates)
                DivData(environment, card)
            }
            // Case 2: { "card": {...} }
            json.has("card") -> {
                val card = json.getJSONObject("card")
                DivData(environment, card)
            }
            // Case 3: Direct div JSON
            else -> {
                DivData(environment, json)
            }
        }
    }

    fun observeStatusNotifications(observer: (HotReloadStatus) -> Unit): Disposable {
        notificationObservers.addObserver(observer)
        return Disposable {
            notificationObservers.removeObserver(observer)
        }
    }
}

private class DivJsonReceiver(
    private val host: String,
    private val port: Int,
) {
    @WorkerThread
    fun captureDivJson(): Result<String> = runCatching {
        val socket = Socket(host, port)
        try {
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            val rawJson = reader.readLines().joinToString(separator = "")
            reader.close()
            rawJson
        } finally {
            socket.close()
        }
    }
}

private class DivJsonSender(
    private val host: String,
    private val port: Int,
) {
    @WorkerThread
    fun sendDivJson(json: String): Result<Unit> = runCatching {
        val socket = Socket(host, port)
        try {
            val writer = PrintWriter(OutputStreamWriter(socket.outputStream), true)
            writer.println(json)
            writer.flush()
        } finally {
            socket.close()
        }
    }
}

private const val TAG = "HotReloadController"
