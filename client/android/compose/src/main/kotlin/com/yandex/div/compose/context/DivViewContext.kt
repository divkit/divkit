package com.yandex.div.compose.context

import androidx.compose.runtime.compositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.expressions.toLocalFunctions
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import kotlin.collections.forEach
import kotlin.collections.orEmpty

internal class DivViewContext(
    data: DivData,
    private val component: DivViewComponent
) {

    val rootLocalComponent: DivLocalComponent

    val visibilityActionTracker: VisibilityActionTracker
        get() = component.visibilityActionTracker

    init {
        val baseFunctionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
        val functions = data.functions.orEmpty().toLocalFunctions()
        rootLocalComponent = createLocalComponent(
            variableController = DivVariableController(component.variableController),
            functionProvider = baseFunctionProvider + functions,
            triggers = data.variableTriggers.orEmpty(),
            variables = data.variables.orEmpty()
        )
    }

    fun getLocalComponent(
        data: DivBase,
        parentComponent: DivLocalComponent
    ): DivLocalComponent {
        component.localComponentStorage.get(data)?.let {
            return it
        }

        val functions = data.functions.orEmpty().toLocalFunctions()
        val variables = data.variables.orEmpty()
        return createLocalComponent(
            variableController = if (variables.isEmpty()) {
                parentComponent.variableController
            } else {
                DivVariableController(parentComponent.variableController)
            },
            functionProvider = parentComponent.functionProvider + functions,
            triggers = data.variableTriggers.orEmpty(),
            variables = variables
        ).also {
            component.localComponentStorage.put(data, it)
        }
    }

    private fun createLocalComponent(
        variableController: DivVariableController,
        functionProvider: FunctionProviderDecorator,
        triggers: List<DivTrigger>,
        variables: List<DivVariable>,
    ): DivLocalComponent {
        val localComponent = component.localComponent().build(
            functionProvider = functionProvider,
            variableController = variableController
        )

        variables.forEach { variableData ->
            localComponent.variableAdapter.convert(variableData)?.let {
                variableController.declare(it)
            }
        }

        triggers.forEach {
            localComponent.triggerStorage.add(it)
        }

        return localComponent
    }
}

internal val LocalDivViewContext = compositionLocalOf<DivViewContext> {
    throw DivException("DivViewContext not provided")
}
