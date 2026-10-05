package com.yandex.div.core.actions

import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.ActiveStateProvider
import com.yandex.div.core.util.DivBlockLocator
import com.yandex.div.core.util.getDefaultState
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock

internal fun Div2View.actionTargetBlockLocator(): DivBlockLocator? {
    val data = divData ?: return null
    return data.states.firstOrNull { it.stateId == currentStateId }?.let { state ->
        DivBlockLocator(
            state = state,
            rootResolver = expressionResolver,
            activeStates = ActiveStateProvider(::findActiveStatePaths),
        )
    }
}

private fun Div2View.findActiveStatePaths(): Set<DivStatePath> {
    val state = divData?.states?.firstOrNull { it.stateId == currentStateId } ?: return emptySet()
    val blocks = state.div.walk(
        resolver = expressionResolver,
        path = currentRootPath,
    )
    return blocks.filterIsInstance<DivBlock.State>().mapNotNullTo(mutableSetOf()) { block ->
        val statePath = "${block.path.statesString}/${block.divValue.getId()}"
        val stateId = dataComponent.stateManager.getState(block.divValue, block.expressionResolver, statePath)
        val selectedStateId = block.divValue.states.find { it.stateId == stateId }?.stateId
            ?: block.divValue.getDefaultState(block.expressionResolver)?.stateId
        selectedStateId?.let { DivStatePath.parse("$statePath/$it") }
    }
}
