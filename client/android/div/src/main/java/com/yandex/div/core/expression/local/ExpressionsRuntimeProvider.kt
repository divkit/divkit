package com.yandex.div.core.expression.local

import com.yandex.div.core.Div2Logger
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.FunctionProviderDecorator
import com.yandex.div.core.expression.storedvalues.StoredValuesController
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.expression.variables.PropertyVariableExecutorImpl
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.expression.variables.declare
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.toLocalFunctions
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.FunctionProvider
import com.yandex.div.evaluable.StoredValueProvider
import com.yandex.div.evaluable.WarningSender
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import javax.inject.Inject

@DivScope
@Mockable
internal class ExpressionsRuntimeProvider @Inject constructor(
    private val divVariableController: DivVariableController,
    private val divActionBinder: DivActionBinder,
    private val logger: Div2Logger,
    private val storedValuesController: StoredValuesController,
) {

    fun createRootRuntime(
        data: DivData,
        errorCollector: ErrorCollector,
        runtimeStore: RuntimeStore,
    ): ExpressionsRuntime {
        val variableController = VariableControllerImpl()
        variableController.addSource(divVariableController.variableSource)

        val storedValueProvider = StoredValueProvider { storedValueName ->
            storedValuesController.getStoredValue(storedValueName, errorCollector)?.getValue()
        }
        var functionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
        val functions = data.functions
        if (!functions.isNullOrEmpty()) {
            functionProvider += functions.toLocalFunctions()
        }

        val warningSender = WarningSender { expressionContext, message ->
            val rawExpr = expressionContext.evaluable.rawExpr
            val warning = "Warning occurred while evaluating '$rawExpr':"
            errorCollector.logWarning(Throwable(warning, Throwable(message)))
        }

        return createRuntime(
            data.variables,
            data.variableTriggers,
            variableController,
            storedValueProvider,
            functionProvider,
            warningSender,
            path = "",
            runtimeStore,
            errorCollector,
        )
    }

    fun createChildRuntime(
        path: DivStatePath,
        div: DivBase,
        parentResolver: ExpressionResolverImpl,
        errorCollector: ErrorCollector,
    ): ExpressionsRuntime {
        val localVariableController = VariableControllerImpl(parentResolver.variableController)

        val functions = div.functions
        var functionProvider = parentResolver.evaluator.evaluationContext.functionProvider as FunctionProviderDecorator
        if (!functions.isNullOrEmpty()) {
            functionProvider += functions.toLocalFunctions()
        }

        return createRuntime(
            div.variables,
            div.variableTriggers,
            localVariableController,
            parentResolver.evaluator.evaluationContext.storedValueProvider,
            functionProvider,
            parentResolver.evaluator.evaluationContext.warningSender,
            path = parentResolver.path + "/" + path.lastDivId,
            parentResolver.runtimeStore,
            errorCollector,
        )
    }

    private fun createRuntime(
        variables: List<DivVariable>?,
        variableTriggers: List<DivTrigger>?,
        variableController: VariableController,
        storedValueProvider: StoredValueProvider,
        functionProvider: FunctionProvider,
        warningSender: WarningSender,
        path: String,
        runtimeStore: RuntimeStore,
        errorCollector: ErrorCollector,
    ): ExpressionsRuntime {
        val evaluationContext =
            EvaluationContext(variableController, storedValueProvider, functionProvider, warningSender)
        val evaluator = Evaluator(evaluationContext)
        val resolver = ExpressionResolverImpl(path, runtimeStore, variableController, evaluator, errorCollector)

        var propertyExecutor: PropertyVariableExecutorImpl? = null

        variables?.forEach { variable ->
            if (variable is DivVariable.Property && propertyExecutor == null) {
                propertyExecutor = PropertyVariableExecutorImpl(resolver, runtimeStore.viewProvider)
            }
            variableController.declare(
                variable,
                resolver,
                propertyExecutor ?: PropertyVariableExecutor.STUB,
                errorCollector,
            )
        }

        val triggerController = variableTriggers.toTriggersController(resolver, errorCollector)

        return ExpressionsRuntime(resolver, propertyExecutor, triggerController)
    }

    private fun List<DivTrigger>?.toTriggersController(
        resolver: ExpressionResolverImpl,
        errorCollector: ErrorCollector,
    ): TriggersController? {
        if (isNullOrEmpty()) return null
        val controller = TriggersController(resolver, errorCollector, logger, divActionBinder)
        controller.ensureTriggersSynced(this)
        return controller
    }
}
