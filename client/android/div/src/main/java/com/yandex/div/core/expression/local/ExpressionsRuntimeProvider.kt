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
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.expression.variables.declare
import com.yandex.div.core.util.toLocalFunctions
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.StoredValueProvider
import com.yandex.div.evaluable.WarningSender
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivTrigger
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
        val functionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
        val warningSender = WarningSender { expressionContext, message ->
            val rawExpr = expressionContext.evaluable.rawExpr
            val warning = "Warning occurred while evaluating '$rawExpr': $message"
            errorCollector.logWarning(Throwable(warning))
        }
        val evaluationContext =
            EvaluationContext(variableController, storedValueProvider, functionProvider, warningSender)
        val evaluator = Evaluator(evaluationContext)

        val resolver = ExpressionResolverImpl(
            path = "",
            runtimeStore,
            variableController,
            evaluator,
            errorCollector,
        )

        data.variables?.forEach {
            variableController.declare(it, resolver, errorCollector)
        }

        val triggersController = data.variableTriggers
            .toTriggersController(variableController, resolver, evaluator, errorCollector)

        return ExpressionsRuntime(resolver, triggersController)
    }

    fun createChildRuntime(
        path: String,
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

        val evaluationContext = EvaluationContext(
            localVariableController,
            parentResolver.evaluator.evaluationContext.storedValueProvider,
            functionProvider,
            parentResolver.evaluator.evaluationContext.warningSender
        )
        val evaluator = Evaluator(evaluationContext)

        val childResolver = ExpressionResolverImpl(
            path = path + "/" + parentResolver.path,
            parentResolver.runtimeStore,
            localVariableController,
            evaluator,
            errorCollector,
        )

        div.variables?.forEach {
            localVariableController.declare(it, childResolver, errorCollector)
        }

        val triggerController =
            div.variableTriggers.toTriggersController(localVariableController, childResolver, evaluator, errorCollector)

        return ExpressionsRuntime(childResolver, triggerController)
    }

    private fun List<DivTrigger>?.toTriggersController(
        variableController: VariableController,
        resolver: ExpressionResolver,
        evaluator: Evaluator,
        errorCollector: ErrorCollector,
    ): TriggersController? {
        if (isNullOrEmpty()) return null
        val controller =
            TriggersController(variableController, resolver, evaluator, errorCollector, logger, divActionBinder)
        controller.ensureTriggersSynced(this)
        return controller
    }
}
