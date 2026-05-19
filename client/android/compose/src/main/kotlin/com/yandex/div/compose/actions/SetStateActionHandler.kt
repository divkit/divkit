package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PathFormatException
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div2.DivActionSetState
import javax.inject.Inject

internal class SetStateActionHandler @Inject constructor(
    private val reporter: DivReporter
) {

    fun handle(context: DivActionHandlingContext, action: DivActionSetState) {
        handleByPath(context = context, path = action.stateId.evaluate(context.expressionResolver))
    }

    fun handle(context: DivActionHandlingContext, action: DivUntypedAction.SetState) {
        handleByPath(context = context, path = action.id)
    }

    private fun handleByPath(context: DivActionHandlingContext, path: String) {
        val statePath = try {
            DivStatePath.parse(path)
        } catch (_: PathFormatException) {
            reporter.reportError("Invalid set_state path: $path")
            return
        }
        if (statePath.pathToLastState.isNullOrEmpty()) {
            reporter.reportError("Missing state in set_state path: $path")
            return
        }
        if (statePath.lastStateId.isNullOrEmpty()) {
            reporter.reportError("Empty state id in set_state path: $path")
            return
        }
        context.stateStorage.setActiveState(statePath)
    }
}
