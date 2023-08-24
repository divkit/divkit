package com.yandex.div.core.expression

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.storedvalues.StoredValuesController
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.GlobalVariableController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.expression.variables.toVariable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable
import java.util.Collections
import javax.inject.Inject

/**
 * Holds state of variables for each div view.
 */
@DivScope
@Mockable
internal class ExpressionsRuntimeProvider @Inject constructor(
    private val globalVariableController: GlobalVariableController,
    private val divActionHandler: DivActionHandler,
    private val errorCollectors: ErrorCollectors,
    private val logger: Div2Logger,
    private val storedValuesController: StoredValuesController,
) {
    private val runtimes = Collections.synchronizedMap(mutableMapOf<Any, ExpressionsRuntime>())

    internal fun getOrCreate(tag: DivDataTag, data: DivData): ExpressionsRuntime {
        val result = runtimes.getOrPut(tag.id) { createRuntimeFor(data, tag) }
        val errorCollector = errorCollectors.getOrCreate(tag, data)
        ensureVariablesSynced(result.variableController, data, errorCollector)
        result.triggersController.ensureTriggersSynced(data.variableTriggers ?: emptyList())
        return result
    }

    private fun ensureVariablesSynced(
        v: VariableController,
        data: DivData,
        errorCollector: ErrorCollector
    ) {
        data.variables?.forEach {
            val existingVariable = v.getMutableVariable(it.name) ?: run {
                try {
                    v.declare(it.toVariable())
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
        val variableController = VariableController().apply {
            data.variables?.forEach { divVariable: DivVariable ->
                try {
                    declare(divVariable.toVariable())
                } catch (e: VariableDeclarationException) {
                    errorCollector.logError(e)
                }
            }

            addSource(globalVariableController.variableSource)
        }

        val evaluatorFactory = ExpressionEvaluatorFactory(BuiltinFunctionProvider(
            variableProvider = { variableName ->
                variableController.getMutableVariable(variableName)?.getValue()
            },
            storedValueProvider = { storedValueName ->
                storedValuesController.getStoredValue(storedValueName)?.getValue()
            },
        ))
        val expressionResolver = ExpressionResolverImpl(
            variableController,
            evaluatorFactory,
            errorCollector,
        )

        val triggersController = TriggersController(
            variableController,
            expressionResolver,
            divActionHandler,
            evaluatorFactory.create{ name ->
                variableController.getMutableVariable(name)?.getValue()
                    ?: throw EvaluableException("Unknown variable $name")
            },
            errorCollector,
            logger
        )

        return ExpressionsRuntime(
            expressionResolver,
            variableController,
            triggersController,
        )
    }
}

private val DivVariable.name: String
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
