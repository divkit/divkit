package com.yandex.div.compose.views.state

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.state.LocalDivStatePath
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.variables.mutableStateFromStringVariable
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div.core.state.DivStatePath
import com.yandex.div2.DivState

@Composable
internal fun DivStateView(
    modifier: Modifier,
    data: DivState
) {
    val modifier = if (data.clipToBounds.observedValue()) {
        modifier.clipToBounds()
    } else {
        modifier
    }

    WithDivState(data) { activeState ->
        val activeDiv = activeState.div ?: return@WithDivState
        Box(modifier = modifier.padding(data.paddings)) {
            DivBlockView(activeDiv)
        }
    }
}

@Composable
internal fun WithDivState(
    divState: DivState,
    content: @Composable (DivState.State) -> Unit,
) {
    val parentPath = LocalDivStatePath.current
    val divId = divState.id
    if (divId == null) {
        reportError("DivState id is missing at path '$parentPath'")
    }

    val activeState = divState.rememberActiveState(parentPath) ?: return

    val childPath = parentPath.append(
        divId.orEmpty(),
        activeState.stateId,
        activeState.stateId
    )
    CompositionLocalProvider(LocalDivStatePath provides childPath) {
        content(activeState)
    }
}

@Composable
private fun DivState.rememberActiveState(
    parentPath: DivStatePath
): DivState.State? {
    val stateStorage = LocalDivViewContext.current.stateStorage
    val resolver = expressionResolver
    val divId = id

    val variableState = stateIdVariable?.let { mutableStateFromStringVariable(it) }
    val storedStateId = divId?.let { stateStorage.getActiveState(parentPath, it) }

    if (divId != null && variableState != null) {
        DisposableEffect(parentPath, divId, variableState) {
            stateStorage.bindStateIdVariable(parentPath, divId) { variableState.value = it }
            onDispose { stateStorage.unbindStateIdVariable(parentPath, divId) }
        }
    }

    val activeStateId = variableState?.value
        ?: storedStateId
        ?: remember(defaultStateId, resolver) {
            defaultStateId?.evaluate(resolver)
        }

    return resolveState(activeStateId)
}

@Composable
private fun DivState.resolveState(activeStateId: String?): DivState.State? {
    val resolvedState = activeStateId?.let { id -> states.find { it.stateId == id } }
    if (resolvedState == null && activeStateId != null) {
        reportError("State with id '$activeStateId' not found")
    }
    return resolvedState
        ?: states.firstOrNull()
        ?: run {
            reportError("DivState has no states")
            null
        }
}
