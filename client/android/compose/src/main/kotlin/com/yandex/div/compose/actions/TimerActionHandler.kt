package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.timers.TimerStorage
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div2.DivActionTimer
import javax.inject.Inject

internal class TimerActionHandler @Inject constructor(
    private val reporter: DivReporter,
    private val timerStorage: TimerStorage
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionTimer
    ) {
        val expressionResolver = context.expressionResolver
        execute(
            id = action.id.evaluate(expressionResolver),
            action = action.action.evaluate(expressionResolver)
        )
    }

    fun handle(action: DivUntypedAction.Timer) {
        val typedAction = DivActionTimer.Action.fromString(action.action)
        if (typedAction == null) {
            reporter.reportError("Invalid timer action: ${action.action}")
        } else {
            execute(id = action.id, action = typedAction)
        }
    }

    private fun execute(id: String, action: DivActionTimer.Action) {
        when (action) {
            DivActionTimer.Action.START -> timerStorage.start(id)
            DivActionTimer.Action.STOP -> timerStorage.stop(id)
            DivActionTimer.Action.PAUSE -> timerStorage.pause(id)
            DivActionTimer.Action.RESUME -> timerStorage.resume(id)
            DivActionTimer.Action.CANCEL -> timerStorage.cancel(id)
            DivActionTimer.Action.RESET -> timerStorage.reset(id)
        }
    }
}
