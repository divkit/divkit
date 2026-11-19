package com.yandex.div.compose.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.context.DivViewContextStorage
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.compose.utils.children
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivState
import dev.zacsweers.metro.Inject

@DivViewScope
@Inject
internal class DivStateStorage(
    private val data: DivData,
    private val viewContextStorage: DivViewContextStorage,
) {
    private var activeRootStateId: Long? by mutableStateOf(null)
    private val activeStates = mutableStateMapOf<String, String>()
    private val swipeOutStates = mutableMapOf<Long, SwipeOutState>()
    private val variableSetters = mutableMapOf<String, (String) -> Unit>()

    fun getActiveRootState(states: List<DivData.State>): DivData.State? {
        return states.find { it.stateId == activeRootStateId } ?: states.firstOrNull()
    }

    fun getActiveState(parentPath: DivStatePath, divId: String): String? {
        return activeStates[storageKey(parentPath, divId)]
    }

    fun setActiveState(divStatePath: DivStatePath) {
        val viewContext = viewContextStorage.get(data)
        var div = viewContext?.component?.states?.value
            ?.find { it.stateId == divStatePath.topLevelStateId }?.div
        var localComponent = viewContext?.rootLocalComponent
        var parentPath = DivStatePath.fromState(divStatePath.topLevelStateId)
        for ((divId, stateId) in divStatePath.getStates()) {
            val target = if (div != null && localComponent != null && viewContext != null) {
                findStateTarget(div, divId, localComponent, viewContext)
            } else {
                null
            }
            applyState(parentPath, divId, stateId, target)
            div = target?.state?.states?.find { it.stateId == stateId }?.div
            localComponent = target?.component
            parentPath = parentPath.append(divId, stateId, stateId)
        }
        activeRootStateId = divStatePath.topLevelStateId
    }

    private fun applyState(
        parentPath: DivStatePath,
        divId: String,
        stateId: String,
        target: StateTarget?,
    ) {
        val key = storageKey(parentPath, divId)
        val setter = variableSetters[key]
        val variableName = target?.state?.stateIdVariable
        if (setter != null) {
            activeStates.remove(key)
            setter(stateId)
        } else if (variableName != null) {
            activeStates.remove(key)
            setStateVariable(target.component, variableName, stateId)
        } else {
            activeStates[key] = stateId
        }
    }

    private fun setStateVariable(component: DivLocalComponent, name: String, stateId: String) {
        val variable = component.expressionResolver.getVariable(name)
        if (variable == null) {
            component.reporter.reportError("Unknown variable: $name")
            return
        }
        val isString = variable is Variable.StringVariable ||
            variable is Variable.PropertyVariable && variable.valueType == DivEvaluableType.STRING
        if (!isString) {
            component.reporter.reportError("variable [$name] is not a string variable")
            return
        }
        try {
            variable.setValueDirectly(stateId)
        } catch (e: VariableMutationException) {
            component.reporter.reportError(e)
        }
    }

    private fun findStateTarget(
        div: Div,
        divId: String,
        parentComponent: DivLocalComponent,
        viewContext: DivViewContext,
    ): StateTarget? {
        val component = viewContext.getOwnComponent(div.value(), parentComponent)
        if (div is Div.State) {
            return if (div.value.id == divId) {
                StateTarget(div.value, component)
            } else {
                null
            }
        }
        for (child in div.children()) {
            findStateTarget(child, divId, component, viewContext)?.let { return it }
        }
        return null
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
        val key = storageKey(parentPath, divId)
        variableSetters[key] = setter
        activeStates.remove(key)?.let(setter)
    }

    fun unbindStateIdVariable(parentPath: DivStatePath, divId: String) {
        variableSetters.remove(storageKey(parentPath, divId))
    }

    fun clear() {
        activeRootStateId = null
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

    private class StateTarget(val state: DivState, val component: DivLocalComponent)
}
