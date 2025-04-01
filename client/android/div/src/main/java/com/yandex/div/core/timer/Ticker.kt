package com.yandex.div.core.timer

import android.os.SystemClock
import com.yandex.div.core.view2.errors.ErrorCollector

internal class Ticker(
    private val name: String,
    private val onInterrupt: (Long) -> Unit,
    private val onStart: (Long) -> Unit,
    private val onEnd: (Long) -> Unit,
    private val onTick: (Long) -> Unit,
    private val errorCollector: ErrorCollector?
) {
    private var duration: Long? = null
    private var interval: Long? = null

    private var currentDuration: Long? = null
    private var currentInterval: Long? = null

    private var state = State.STOPPED

    private var workTimeFromPrevious: Long = 0

    private var startedAt: Long = DEFAULT_VALUE

    private var interruptedAt: Long = DEFAULT_VALUE

    private val currentTime: Long
        get() = SystemClock.elapsedRealtime()

    private val timer by lazy(LazyThreadSafetyMode.NONE) {
        FixedRateScheduler()
    }

    private fun setupTimer(
        period: Long,
        initialDelay: Long = period,
        onTick: () -> Unit
    ) {
        startedAt = currentTime

        timer.scheduleAtFixedRate(initialDelay, period, onTick)
    }

    private fun cleanTicker() {
        timer.cancel()
    }

    private fun resetTickerState() {
        startedAt = DEFAULT_VALUE
        interruptedAt = DEFAULT_VALUE
        workTimeFromPrevious = 0
    }

    fun update(
        duration: Long,
        interval: Long?
    ) {
        this.interval = interval
        this.duration = if (duration == 0L) null else duration
    }

    private val workTime: Long
        get() = if (startedAt == DEFAULT_VALUE) 0 else currentTime - startedAt

    private val totalWorkTime: Long
        get() = workTime + workTimeFromPrevious

    private fun coercedTick() {
        val maxTotalWorkTime = duration

        if (maxTotalWorkTime != null) {
            onTick(totalWorkTime.coerceAtMost(maxTotalWorkTime))
        } else {
            onTick(totalWorkTime)
        }
    }

    private fun runTimer() {
        val currentInterval = currentInterval
        val currentDuration = currentDuration

        /*
        * Check the time from the last tick and if it is greater than the interval (this happens
        * in case of exiting long-term work in the background), then we call onTick for invalidation
        */
        if (currentInterval != null
            && interruptedAt != DEFAULT_VALUE
            && currentTime - interruptedAt > currentInterval
        ) {
            coercedTick()
        }

        when {
            currentInterval == null && currentDuration != null ->
                runCountDownTimer(currentDuration)
            currentInterval != null && currentDuration != null ->
                runTickTimer(currentDuration, currentInterval)
            currentInterval != null && currentDuration == null ->
                runEndlessTimer(currentInterval)
        }
    }

    private fun runCountDownTimer(duration: Long) {
        val timeLeft = duration - totalWorkTime

        if (timeLeft >= 0) {
            setupTimer(timeLeft) {
                cleanTicker()

                onEnd(duration)

                state = State.STOPPED

                resetTickerState()
            }
        } else {
            onEnd(duration)

            resetTickerState()
        }
    }

    private fun runTickTimer(duration: Long, interval: Long) {
        val initialDelay = interval - (totalWorkTime % interval)

        var ticksLeft = (duration / interval) - (totalWorkTime / interval)

        val processTick = {
            if (ticksLeft > 0) {
                onTick(duration)
            }

            onEnd(duration)

            cleanTicker()
            resetTickerState()
            state = State.STOPPED
        }

        setupTimer(interval, initialDelay) {
            val timeLeft = duration - totalWorkTime

            coercedTick()
            ticksLeft--

            if (timeLeft in 1 until interval) {
                cleanTicker()

                setupTimer(timeLeft) {
                    processTick()
                }
            } else if (timeLeft <= 0) {
                processTick()
            }
        }
    }

    private fun runEndlessTimer(interval: Long) {
        val firstTickDelay = interval - (totalWorkTime % interval)

        setupTimer(interval, firstTickDelay) {
            coercedTick()
        }
    }

    private fun onError(message: String) {
        errorCollector?.logError(IllegalArgumentException(message))
    }

    fun saveState() {
        if (startedAt != DEFAULT_VALUE) {
            val workTime = currentTime - startedAt

            workTimeFromPrevious += workTime

            interruptedAt = currentTime

            startedAt = DEFAULT_VALUE
        }

        cleanTicker()
    }

    fun restoreState(fromPreviousPoint: Boolean) {
        if (!fromPreviousPoint) {
            interruptedAt = DEFAULT_VALUE
        }

        runTimer()
    }

    fun start() {
        when (state) {
            State.STOPPED -> {
                cleanTicker()

                currentDuration = duration
                currentInterval = interval

                state = State.WORKING

                onStart(totalWorkTime)

                runTimer()
            }
            State.WORKING -> onError("The timer '$name' already working!")
            State.PAUSED -> onError("The timer '$name' paused!")
        }
    }

    fun stop() {
        when (state) {
            State.STOPPED -> onError("The timer '$name' already stopped!")
            State.WORKING, State.PAUSED -> {
                state = State.STOPPED

                onEnd(totalWorkTime)

                cleanTicker()

                resetTickerState()
            }
        }
    }

    fun pause() {
        when (state) {
            State.STOPPED -> onError("The timer '$name' already stopped!")
            State.WORKING -> {
                state = State.PAUSED

                onInterrupt(totalWorkTime)

                saveState()

                startedAt = DEFAULT_VALUE
            }
            State.PAUSED -> onError("The timer '$name' already paused!")
        }
    }

    fun resume() {
        when (state) {
            State.STOPPED -> onError("The timer '$name' is stopped!")
            State.WORKING -> onError("The timer '$name' already working!")
            State.PAUSED -> {
                state = State.WORKING

                restoreState(fromPreviousPoint = false)
            }
        }
    }

    fun cancel() {
        when (state) {
            State.STOPPED -> Unit
            State.WORKING, State.PAUSED -> {
                state = State.STOPPED

                cleanTicker()

                onInterrupt(totalWorkTime)

                resetTickerState()
            }
        }
    }

    fun reset() {
        cancel()
        start()
    }

    enum class State {
        STOPPED,
        WORKING,
        PAUSED
    }

    companion object {
        private const val DEFAULT_VALUE = -1L
    }
}
