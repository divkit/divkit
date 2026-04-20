package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.utils.expressionResolver
import com.yandex.div.compose.utils.observedVariableValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.reportWarning
import com.yandex.div2.DivState

@Composable
internal fun DivState.observeActiveState(): DivState.State? {
    val stateVariable = stateIdVariable
    if (stateVariable == null) {
        reportWarning("div-state: state switching is not supported without state_id_variable")
    }

    val activeStateId = stateVariable?.let {
        LocalComponent.current.variableController.observedVariableValue(it)
    }

    val resolver = expressionResolver
    val stateId = activeStateId ?: remember(defaultStateId, resolver) {
        defaultStateId?.evaluate(resolver)
    }
    val activeState = stateId?.let { id -> states.find { it.stateId == id } }
    if (activeState == null && stateId != null) {
        reportError("div-state: state with id [$stateId] not found")
    }
    return activeState ?: states.firstOrNull()
}
