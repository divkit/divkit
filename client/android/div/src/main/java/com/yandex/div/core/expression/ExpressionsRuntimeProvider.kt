package com.yandex.div.core.expression

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.storedvalues.StoredValuesController
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.expression.variables.toVariable
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable
import java.util.Collections
import java.util.WeakHashMap
import javax.inject.Inject

/**
 * Holds state of variables for each div view.
 */
@DivScope
@Mockable
internal class ExpressionsRuntimeProvider @Inject constructor(
    private val divVariableController: DivVariableController,
    private val divActionBinder: DivActionBinder,
    private val errorCollectors: ErrorCollectors,
    private val logger: Div2Logger,
    private val storedValuesController: StoredValuesController,
) {
    private val runtimes = Collections.synchronizedMap(mutableMapOf<String, ExpressionsRuntime>())
    private val divDataTags = WeakHashMap<Div2View, MutableSet<String>>()

    internal fun getOrCreate(tag: DivDataTag, data: DivData, div2View: Div2View): ExpressionsRuntime {
        val result = runtimes.getOrPut(tag.id) { createRuntimeFor(data, tag) }
        val errorCollector = errorCollectors.getOrCreate(tag, data)
        divDataTags.getOrPut(div2View, ::mutableSetOf).add(tag.id)
        ensureVariablesSynced(result.variableController, result.expressionResolver, data, errorCollector)
        result.triggersController?.ensureTriggersSynced(data.variableTriggers ?: emptyList())
        return result
    }

    fun reset(tags: List<DivDataTag>) {
        if (tags.isEmpty()) {
            runtimes.clear()
        } else {
            tags.forEach { tag ->
                runtimes.remove(tag.id)
            }
        }
    }

    internal fun cleanupRuntime(view: Div2View) {
        divDataTags[view]?.forEach { tag ->
            runtimes[tag]?.runtimeStore?.cleanup()
        }
        divDataTags.remove(view)
    }

    private fun ensureVariablesSynced(
        v: VariableController,
        resolver: ExpressionResolver,
        data: DivData,
        errorCollector: ErrorCollector
    ) {
        data.variables?.forEach {
            val existingVariable = v.getMutableVariable(it.name) ?: run {
                try {
                    v.declare(it.toVariable(resolver))
                } catch (e: VariableDeclarationException) {
                    errorCollector.logError(e)
                }
                return@forEach
            }
            val consistent = when (it) {
                is DivVariable.Bool -> existingVariable is Variable.BooleanVariable
                is DivVariable.Integer -> existingVariable is Variable.IntegerVariable
                is DivVariable.Number -> existingVariable is Variable.DoubleVariable
                is DivVariable.Str -> existingVariable is Variable.StringVariable
                is DivVariable.Color -> existingVariable is Variable.ColorVariable
                is DivVariable.Url -> existingVariable is Variable.UrlVariable
                is DivVariable.Dict -> existingVariable is Variable.DictVariable
                is DivVariable.Array -> existingVariable is Variable.ArrayVariable
            }.apply { /*exhaustive*/ }

            // This usually happens when you're using same DivDataTag for DivData
            // with different set of variables!
            if (!consistent) {
                errorCollector.logError(
                    IllegalArgumentException(
                        """
                           Variable inconsistency detected!
                           at DivData: ${it.name} ($it)
                           at VariableController: ${v.getMutableVariable(it.name)}
                        """.trimIndent()
                    )
                )
            }
        }
    }

    private fun createRuntimeFor(data: DivData, tag: DivDataTag): ExpressionsRuntime {
        val errorCollector = errorCollectors.getOrCreate(tag, data)
        val variableController = VariableControllerImpl()
        variableController.addSource(divVariableController.variableSource)

        val functionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
        val evaluationContext = EvaluationContext(
            variableProvider = variableController,
            storedValueProvider = { storedValueName ->
                storedValuesController.getStoredValue(storedValueName, errorCollector)?.getValue()
            },
            functionProvider = functionProvider,
            warningSender = { expressionContext, message ->
                val rawExpr = expressionContext.evaluable.rawExpr
                val warning = "Warning occurred while evaluating '$rawExpr': $message"

                errorCollector.logWarning(Throwable(warning))
            }
        )

        val evaluator = Evaluator(evaluationContext)

        val runtimeStore = RuntimeStore(evaluator, errorCollector, logger, divActionBinder)
        val callback = ExpressionResolverImpl.OnCreateCallback { resolver, variableController, functionProvider ->
            runtimeStore.putRuntime(
                runtime = ExpressionsRuntime(resolver, variableController, null, functionProvider, runtimeStore)
            )
        }

        val expressionResolver = ExpressionResolverImpl(
            path = "dataTag: '${tag.id}'",
            runtimeStore = runtimeStore,
            variableController = variableController,
            evaluator = evaluator,
            errorCollector = errorCollector,
            onCreateCallback = callback,
        )

        data.variables?.forEach { divVariable: DivVariable ->
            try {
                variableController.declare(divVariable.toVariable(expressionResolver))
            } catch (e: VariableDeclarationException) {
                errorCollector.logError(e)
            }
        }

        val triggersController = TriggersController(
            variableController,
            expressionResolver,
            evaluator,
            errorCollector,
            logger,
            divActionBinder
        )

        return ExpressionsRuntime(
            expressionResolver,
            variableController,
            triggersController,
            functionProvider,
            runtimeStore,
        ).also { runtimeStore.rootRuntime = it }
    }
}

internal val DivVariable.name: String
    get() {
        return when (this) {
            is DivVariable.Bool -> this.value.name
            is DivVariable.Integer -> this.value.name
            is DivVariable.Number -> this.value.name
            is DivVariable.Str -> this.value.name
            is DivVariable.Color -> this.value.name
            is DivVariable.Url -> this.value.name
            is DivVariable.Dict -> this.value.name
            is DivVariable.Array -> this.value.name
        }
    }
