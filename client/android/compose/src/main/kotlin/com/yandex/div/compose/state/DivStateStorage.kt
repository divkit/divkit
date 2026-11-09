package com.yandex.div.compose.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div2.DivState
import dev.zacsweers.metro.Inject

@DivViewScope
internal class DivStateStorage @Inject constructor() {
    private val activeStates = mutableStateMapOf<String, String>()
    private val swipeOutStates = mutableMapOf<Long, SwipeOutState>()
    private val variableSetters = mutableMapOf<String, (String) -> Unit>()

    fun getActiveState(parentPath: DivStatePath, divId: String): String? {
        return activeStates[storageKey(parentPath, divId)]
    }

    fun setActiveState(divStatePath: DivStatePath) {
        val key = divStatePath.pathToLastState ?: return
        val stateId = divStatePath.lastStateId ?: return
        activeStates[key] = stateId
        variableSetters[key]?.invoke(stateId)
    }

    fun getSwipeOutState(
        compositionKey: Long,
        state: DivState.State,
    ): MutableState<Boolean>? {
        if (state.swipeOutActions.isNullOrEmpty()) {
            swipeOutStates.remove(compositionKey)
            return null
        }

        val previous = swipeOutStates[compositionKey]
        if (previous?.stateId == state.stateId) {
            return previous.isDismissed
        }

        val isDismissed = mutableStateOf(false)
        swipeOutStates[compositionKey] = SwipeOutState(
            stateId = state.stateId,
            isDismissed = isDismissed,
        )
        return isDismissed
    }

    fun bindStateIdVariable(parentPath: DivStatePath, divId: String, setter: (String) -> Unit) {
        variableSetters[storageKey(parentPath, divId)] = setter
    }

    fun unbindStateIdVariable(parentPath: DivStatePath, divId: String) {
        variableSetters.remove(storageKey(parentPath, divId))
    }

    fun clear() {
        activeStates.clear()
        swipeOutStates.clear()
        variableSetters.clear()
    }

    private fun storageKey(parentPath: DivStatePath, divId: String): String =
        "${parentPath.statesString}/$divId"

    private class SwipeOutState(
        val stateId: String,
        val isDismissed: MutableState<Boolean>,
    )
}
