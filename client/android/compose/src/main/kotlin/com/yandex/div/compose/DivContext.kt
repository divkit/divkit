package com.yandex.div.compose

import android.content.ContextWrapper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import com.yandex.div.compose.context.DivLocalContext
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.dagger.DivContextComponent
import com.yandex.div.compose.internal.DivDebugFeatures
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.expressions.toLocalFunctions
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import javax.inject.Inject

/**
 * An implementation of [android.content.Context] that must be used for composing [DivView]s.
 *
 * Example usage:
 *
 *    val configuration = DivComposeConfiguration()
 *    val divContext = configuration.createContext(baseContext = activity)
 */
@PublicApi
class DivContext @Inject @MainThread internal constructor(
    internal val component: DivContextComponent
) : ContextWrapper(component.baseContext) {

    @InternalApi
    @VisibleForTesting
    val debugFeatures: DivDebugFeatures
        get() = component.debugFeatures

    /**
     * Removes [DivView] context associated with the given [com.yandex.div2.DivData].
     */
    fun clearViewContext(data: DivData) {
        component.viewContextStorage.remove(data)
    }

    internal fun getViewContext(data: DivData): DivViewContext {
        component.viewContextStorage.get(data)?.let {
            return it
        }

        val baseFunctionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
        val functions = data.functions.orEmpty().toLocalFunctions()
        return DivViewContext(
            rootLocalContext = createLocalContext(
                variableController = DivVariableController(component.variableController),
                functionProvider = baseFunctionProvider + functions,
                triggers = data.variableTriggers.orEmpty(),
                variables = data.variables.orEmpty()
            )
        ).also {
            component.viewContextStorage.put(data, it)
        }
    }

    internal fun getLocalContext(
        data: DivBase,
        viewContext: DivViewContext,
        parentContext: DivLocalContext
    ): DivLocalContext {
        viewContext.localContextStorage.get(data)?.let {
            return it
        }

        val functions = data.functions.orEmpty().toLocalFunctions()
        val variables = data.variables.orEmpty()
        return createLocalContext(
            variableController = if (variables.isEmpty()) {
                parentContext.variableController
            } else {
                DivVariableController(parentContext.variableController)
            },
            functionProvider = parentContext.functionProvider + functions,
            triggers = data.variableTriggers.orEmpty(),
            variables = variables
        ).also {
            viewContext.localContextStorage.put(data, it)
        }
    }

    private fun createLocalContext(
        variableController: DivVariableController,
        functionProvider: FunctionProviderDecorator,
        triggers: List<DivTrigger>,
        variables: List<DivVariable>,
    ): DivLocalContext {
        val localComponent = component.localComponent()
            .functionProvider(functionProvider)
            .variableController(variableController)
            .build()

        variables.forEach { variableData ->
            localComponent.variableAdapter.convert(variableData)?.let {
                variableController.declare(it)
            }
        }

        triggers.forEach {
            localComponent.triggerStorage.add(it)
        }

        return localComponent.context
    }
}
