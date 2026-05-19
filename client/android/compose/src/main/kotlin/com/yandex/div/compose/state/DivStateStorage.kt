package com.yandex.div.compose.state

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.core.state.DivStatePath
import javax.inject.Inject

@DivViewScope
internal class DivStateStorage @Inject constructor() {
    private val activeStates: SnapshotStateMap<String, String> = mutableStateMapOf()

    private val variableSetters: MutableMap<String, (String) -> Unit> = mutableMapOf()

    fun setActiveState(divStatePath: DivStatePath) {
        val key = divStatePath.pathToLastState ?: return
        val stateId = divStatePath.lastStateId ?: return
        activeStates[key] = stateId
        variableSetters[key]?.invoke(stateId)
    }

    fun getActiveState(parentPath: DivStatePath, divId: String): String? =
        activeStates[storageKey(parentPath, divId)]

    fun bindStateIdVariable(parentPath: DivStatePath, divId: String, setter: (String) -> Unit) {
        variableSetters[storageKey(parentPath, divId)] = setter
    }

    fun unbindStateIdVariable(parentPath: DivStatePath, divId: String) {
        variableSetters.remove(storageKey(parentPath, divId))
    }

    fun clear() {
        activeStates.clear()
        variableSetters.clear()
    }

    private fun storageKey(parentPath: DivStatePath, divId: String): String =
        "${parentPath.statesString}/$divId"
}
