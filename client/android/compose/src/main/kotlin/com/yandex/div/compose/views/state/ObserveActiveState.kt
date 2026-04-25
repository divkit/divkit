package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.utils.expressionResolver
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.reportWarning
import com.yandex.div.compose.utils.variables.mutableStateFromStringVariable
import com.yandex.div2.DivState

@Composable
internal fun DivState.observeActiveState(): DivState.State? {
    val stateVariable = stateIdVariable
    if (stateVariable == null) {
        reportWarning("div-state: state switching is not supported without state_id_variable")
    }
    val resolver = expressionResolver

    val activeStateId = stateVariable?.let {
        mutableStateFromStringVariable(it)?.value
    } ?: remember(defaultStateId, resolver) {
        defaultStateId?.evaluate(resolver)
    }

    val activeState = activeStateId?.let { id -> states.find { it.stateId == id } }
    if (activeState == null && activeStateId != null) {
        reportError("div-state: state with id [$activeStateId] not found")
    }
    return activeState ?: states.firstOrNull()
}
