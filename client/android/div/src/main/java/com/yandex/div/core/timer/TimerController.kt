package com.yandex.div.core.timer

import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTimer
import java.util.Timer

internal class TimerController(
    val divTimer: DivTimer,
    private val divActionBinder: DivActionBinder,
    private val errorCollector: ErrorCollector,
    private val expressionResolver: ExpressionResolver
) {
    private var div2View: Div2View? = null

    private val id = divTimer.id

    private val valueVariable = divTimer.valueVariable
    private val endActions = divTimer.endActions
    private val tickActions = divTimer.tickActions

    private var savedForBackground = false

    private val ticker = Ticker(
        name = id,
        onInterrupt = ::updateTimerVariable,
        onStart = ::updateTimerVariable,
        onEnd = ::onEnd,
        onTick = ::onTick,
        errorCollector = errorCollector
    )

    init {
        divTimer.duration.observeAndGet(expressionResolver) { updateTimer() }
        divTimer.tickInterval?.observeAndGet(expressionResolver) { updateTimer() }
    }

    private fun updateTimer() {
        ticker.update(
            divTimer.duration.evaluate(expressionResolver).toLong(),
            divTimer.tickInterval?.evaluate(expressionResolver)?.toLong()
        )
    }

    fun onAttach(
        view: Div2View,
        timer: Timer
    ) {
        div2View = view

        ticker.attachToTimer(timer)

        if (savedForBackground) {
            ticker.restoreState(fromPreviousPoint = true)

            savedForBackground = false
        }
    }

    fun onDetach() {
        div2View = null

        ticker.saveState()
        ticker.detachFromTimer()

        savedForBackground = true
    }

    fun applyCommand(command: String) {
        when (command) {
            START_COMMAND -> ticker.start()
            STOP_COMMAND -> ticker.stop()
            PAUSE_COMMAND -> ticker.pause()
            RESUME_COMMAND -> ticker.resume()
            CANCEL_COMMAND -> ticker.cancel()
            RESET_COMMAND -> ticker.reset()
            else -> errorCollector
                .logError(IllegalArgumentException("$command is unsupported timer command!"))
        }
    }

    private fun onTick(time: Long) {
        updateTimerVariable(time)

        UiThreadHandler.executeOnMainThread {
            div2View?.let {
                divActionBinder.handleActions(it, tickActions, DivActionReason.TIMER)
            }
        }
    }

    private fun onEnd(time: Long) {
        updateTimerVariable(time)

        UiThreadHandler.executeOnMainThread {
            div2View?.let {
                divActionBinder.handleActions(it, endActions, DivActionReason.TIMER)
            }
        }
    }

    private fun updateTimerVariable(value: Long) {
        if (valueVariable != null) {
            UiThreadHandler.executeOnMainThread {
                div2View?.setVariable(valueVariable, value.toString())
            }
        }
    }

    companion object {
        const val START_COMMAND = "start"
        const val STOP_COMMAND = "stop"
        const val PAUSE_COMMAND = "pause"
        const val RESUME_COMMAND = "resume"
        const val CANCEL_COMMAND = "cancel"
        const val RESET_COMMAND = "reset"
    }
}
