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
    private val pendingTooltipPaths = mutableListOf<DivStatePath>()

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
        val (tooltipPaths, cardPaths) = paths.partition { it.namespace != null }
        if (cardPaths.isNotEmpty() || paths.isEmpty()) {
            if (pendingState != null && state != pendingState) {
                reset()
            }

            pendingState = state
            isPendingStateTemporary = isPendingStateTemporary && temporary
            pendingPaths += cardPaths
        }
        pendingTooltipPaths += tooltipPaths
        paths.forEach { path: DivStatePath ->
            divView.dataComponent.stateManager.updateStates(path, temporary)
        }

        if (bulkModeDepth == 0) {
            runBulkActions()
        }
    }

    private fun runBulkActions() {
        if (pendingTooltipPaths.isNotEmpty()) {
            val tooltipPaths = pendingTooltipPaths.immutableCopy()
            pendingTooltipPaths.clear()
            divView.div2Component.tooltipController.switchStates(divView, tooltipPaths)
        }
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
            viewComponent.stateSwitcher.switchStates(newState, expressionResolver, pendingPaths.immutableCopy())
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
