package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PathFormatException
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div2.DivActionSetState
import com.yandex.div2.DivData
import dev.zacsweers.metro.Inject

@Inject
internal class SetStateActionHandler(
    private val reporter: DivReporter,
    private val stateStorage: DivStateStorage,
    private val data: DivData,
) {

    fun handle(context: DivActionHandlingContext, action: DivActionSetState) {
        setState(path = action.stateId.evaluate(context.expressionResolver))
    }

    fun handle(action: DivUntypedAction.SetState) {
        setState(path = action.id)
    }

    private fun setState(path: String) {
        val statePath = try {
            DivStatePath.parse(path)
        } catch (_: PathFormatException) {
            reporter.reportError("Invalid set_state path: $path")
            return
        }
        if (data.states.none { it.stateId == statePath.topLevelStateId }) {
            reporter.reportError("Root state with id '${statePath.topLevelStateId}' not found")
            return
        }
        if (!statePath.isRootPath() && statePath.lastStateId.isNullOrEmpty()) {
            reporter.reportError("Empty state id in set_state path: $path")
            return
        }
        stateStorage.setActiveState(statePath)
    }
}
