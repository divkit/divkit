package com.yandex.div.core.view2

import androidx.core.view.isEmpty
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.StateConflictException
import com.yandex.div.internal.util.immutableCopy
import com.yandex.div2.DivData
import javax.inject.Inject

@DivViewScope
internal class BulkActionHandler @Inject constructor(private val divView: Div2View) {

    private var bulkModeDepth = 0
    private var pendingState: DivData.State? = null
    private var isPendingStateTemporary: Boolean = true
    private val pendingPaths = mutableListOf<DivStatePath>()

    fun bulkActions(function: () -> Unit = {}) {
        bulkModeDepth++
        function()
        bulkModeDepth--
        if (bulkModeDepth == 0) {
            runBulkActions()
        }
    }

    fun switchState(state: DivData.State?, path: DivStatePath, temporary: Boolean) =
        switchMultipleStates(state, listOf(path), temporary)

    fun switchMultipleStates(
        state: DivData.State?,
        paths: List<DivStatePath>,
        temporary: Boolean,
    ) {
        if (pendingState != null && state != pendingState) {
            reset()
        }

        pendingState = state
        isPendingStateTemporary = isPendingStateTemporary && temporary
        pendingPaths += paths
        paths.forEach { path: DivStatePath ->
            divView.div2Component.stateManager.updateStates(divView.divTag.id, path, temporary)
        }

        if (bulkModeDepth == 0) {
            runBulkActions()
        }
    }

    private fun runBulkActions() {
        val newState = pendingState ?: return
        divView.switchState(newState)
        reset()
    }

    private fun Div2View.switchState(newState: DivData.State) {
        if (newState.stateId != currentStateId) {
            switchToState(newState.stateId, isPendingStateTemporary)
            return
        }

        if (isEmpty()) return

        try {
            viewComponent.stateSwitcher.switchStates(bindingContext, newState, pendingPaths.immutableCopy())
        } catch (e: StateConflictException) {
            logError(e)
            resetToInitialState()
        }
    }

    private fun reset() {
        pendingState = null
        isPendingStateTemporary = true
        pendingPaths.clear()
    }
}
