package com.yandex.div.compose.timers

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
internal class TimerController(
    private val timer: DivTimer,
    private val actionHandler: DivActionHandler,
    private val actionHandlingContext: DivActionHandlingContext,
    private val coroutineScope: CoroutineScope,
    private val expressionResolver: ExpressionResolver,
    private val reporter: DivReporter,
    private val timeSource: TimeSource,
    private val variableController: DivVariableController
) {
    private val valueVariable: Variable.IntegerVariable?

    private var totalDuration = Duration.INFINITE
    private var tickDuration = Duration.INFINITE
    private var state: State = State.STOPPED
    private var nextTickJob: Job? = null
    private var currentTickMark: TimeMark? = null
    private var shouldResumeWhenEnabled = false

    private var elapsedDuration: Duration = Duration.ZERO
        set(value) {
            if (field != value) {
                field = value
                valueVariable?.set(value.inWholeMilliseconds)
            }
        }

    init {
        valueVariable = getValueVariable()
    }

    var isEnabled: Boolean = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            if (value && shouldResumeWhenEnabled) {
                shouldResumeWhenEnabled = false
                resume()
            }

            if (!value && state == State.STARTED) {
                shouldResumeWhenEnabled = true
                pause()
            }
        }

    fun start() {
        if (state != State.STOPPED) {
            reporter.reportError("Timer is already started: ${timer.id}")
            return
        }

        val duration = timer.duration.evaluate(expressionResolver).milliseconds
        totalDuration = if (duration == Duration.ZERO) Duration.INFINITE else duration

        tickDuration = timer.tickInterval?.evaluate(expressionResolver)?.milliseconds
            ?: Duration.INFINITE

        elapsedDuration = Duration.ZERO
        state = State.STARTED
        scheduleNextTick()
    }

    fun stop() {
        if (state == State.STOPPED) {
            reporter.reportError("Timer is already stopped: ${timer.id}")
            return
        }

        cancelJob()
        state = State.STOPPED

        onEnd()
    }

    fun pause() {
        if (state != State.STARTED) {
            reporter.reportError("Timer is not started: ${timer.id}")
            return
        }

        elapsedDuration += currentTickMark?.elapsedNow() ?: Duration.ZERO
        cancelJob()
        state = State.PAUSED
    }

    fun resume() {
        if (state != State.PAUSED) {
            reporter.reportError("Timer is not paused: ${timer.id}")
            return
        }

        state = State.STARTED
        scheduleNextTick()
    }

    fun cancel() {
        cancelJob()
        state = State.STOPPED
    }

    private fun getValueVariable(): Variable.IntegerVariable? {
        val variableName = timer.valueVariable ?: return null

        val variable = variableController.get(variableName)
        if (variable == null) {
            reporter.reportError("Timer variable is not declared: $variableName")
            return null
        }

        val integerVariable = variable as? Variable.IntegerVariable
        if (integerVariable == null) {
            reporter.reportError("Timer variable must be an integer variable: $variableName")
            return null
        }

        return integerVariable
    }

    private fun scheduleNextTick() {
        val isInfinite = totalDuration.isInfinite()
        val hasTicks = !tickDuration.isInfinite()
        if (isInfinite && !hasTicks) {
            reporter.reportError("Timer must have either finite duration or tick_interval: ${timer.id}")
            cancel()
            return
        }

        val leftDuration = if (isInfinite) {
            Duration.INFINITE
        } else {
            totalDuration - elapsedDuration
        }
        val nextTickDuration = if (hasTicks) {
            val elapsedInTick =
                (elapsedDuration.inWholeMilliseconds % tickDuration.inWholeMilliseconds).milliseconds
            val tickLeftDuration = tickDuration - elapsedInTick
            if (tickLeftDuration < leftDuration) tickLeftDuration else leftDuration
        } else {
            leftDuration
        }

        currentTickMark = timeSource.markNow()
        nextTickJob = coroutineScope.launch {
            delay(nextTickDuration)
            elapsedDuration += nextTickDuration

            // ignore tick actions for the last diminished tick
            if (hasTicks &&
                elapsedDuration.inWholeMilliseconds % tickDuration.inWholeMilliseconds == 0L
            ) {
                onTick()
            }

            if (elapsedDuration >= totalDuration) {
                elapsedDuration = totalDuration
                currentTickMark = null
                nextTickJob = null
                state = State.STOPPED
                onEnd()
            } else {
                scheduleNextTick()
            }
        }
    }

    private fun cancelJob() {
        nextTickJob?.cancel()
        nextTickJob = null
        currentTickMark = null
    }

    private fun onTick() {
        timer.tickActions?.let {
            actionHandler.handle(actionHandlingContext, it, DivActionSource.TIMER)
        }
    }

    private fun onEnd() {
        timer.endActions?.let {
            actionHandler.handle(actionHandlingContext, it, DivActionSource.TIMER)
        }
    }

    private enum class State {
        STOPPED,
        STARTED,
        PAUSED
    }
}
