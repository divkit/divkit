package com.yandex.div.core.expression

import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.expression.local.ExpressionsRuntimeProvider
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.local.RuntimeStoreImpl
import com.yandex.div.core.expression.variables.declare
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.internal.variables.name
import com.yandex.div.internal.variables.parseGet
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable
import javax.inject.Inject
import javax.inject.Named

/**
 * Holds state of variables for each div view.
 */
@DivDataScope
internal class RuntimeStoreProvider @Inject constructor(
    @param:Named(Names.DATA_TAG) private val dataTag: String,
    private val runtimeProvider: ExpressionsRuntimeProvider,
    private val errorCollectors: ErrorCollectors,
) {

    var store: RuntimeStoreImpl? = null
        private set

    internal fun getOrCreate(data: DivData): RuntimeStore {
        val errorCollector = errorCollectors.getOrCreate(data)
        return store?.also {
            ensureVariablesSynced(it.rootRuntime, data, errorCollector)
            it.rootRuntime.triggersController?.ensureTriggersSynced(data.variableTriggers ?: emptyList())
        } ?: RuntimeStoreImpl(data, dataTag, runtimeProvider, errorCollector).also {
            store = it
        }
    }

    fun reset() {
        store = null
    }

    private fun ensureVariablesSynced(
        runtime: ExpressionsRuntime,
        data: DivData,
        errorCollector: ErrorCollector,
    ) {
        val resolver = runtime.expressionResolver
        val variableController = resolver.variableController
        val propertyExecutor = runtime.propertyVariableExecutor ?: PropertyVariableExecutor.STUB

        data.variables?.forEach { divVariable ->
            val existingVariable = variableController.getMutableVariable(divVariable.name) ?: run {
                variableController.declare(divVariable, resolver, propertyExecutor, errorCollector)
                return@forEach
            }

            val consistent = when (divVariable) {
                is DivVariable.Bool -> existingVariable is Variable.BooleanVariable
                is DivVariable.Integer -> existingVariable is Variable.IntegerVariable
                is DivVariable.Number -> existingVariable is Variable.DoubleVariable
                is DivVariable.Str -> existingVariable is Variable.StringVariable
                is DivVariable.Color -> existingVariable is Variable.ColorVariable
                is DivVariable.Url -> existingVariable is Variable.UrlVariable
                is DivVariable.Dict -> existingVariable is Variable.DictVariable
                is DivVariable.Array -> existingVariable is Variable.ArrayVariable
                is DivVariable.Property -> {
                    existingVariable is Variable.PropertyVariable &&
                        divVariable.value.valueType == existingVariable.valueType
                }
            }.apply { /*exhaustive*/ }

            // This usually happens when you're using same DivDataTag for DivData
            // with different set of variables!
            if (!consistent) {
                errorCollector.logError(
                    IllegalArgumentException(
                        """
                           Variable inconsistency detected!
                           at DivData: ${divVariable.name} ($divVariable)
                           at VariableController: $existingVariable
                        """.trimIndent()
                    )
                )
                return@forEach
            }

            if (divVariable is DivVariable.Property && existingVariable is Variable.PropertyVariable) {
                val newGetExpression = divVariable.value.parseGet(resolver, errorCollector) ?: return@forEach
                val delegate = existingVariable.delegate.copy(
                    newGetExpression,
                    divVariable.value.set,
                    divVariable.value.newValueVariableName
                )
                existingVariable.delegate = delegate
            }
        }
    }
}
