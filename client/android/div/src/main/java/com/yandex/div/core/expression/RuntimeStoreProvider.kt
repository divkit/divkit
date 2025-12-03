package com.yandex.div.core.expression

import com.yandex.div.DivDataTag
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.ExpressionsRuntimeProvider
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.local.RuntimeStoreImpl
import com.yandex.div.core.expression.variables.declare
import com.yandex.div.core.expression.variables.parseGet
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import com.yandex.div.internal.data.PropertyVariableExecutor
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
internal class RuntimeStoreProvider @Inject constructor(
    private val runtimeProvider: ExpressionsRuntimeProvider,
    private val errorCollectors: ErrorCollectors,
) {

    private val runtimeStores = Collections.synchronizedMap(mutableMapOf<String, RuntimeStoreImpl>())
    private val divDataTags = WeakHashMap<Div2View, MutableSet<String>>()

    internal fun getOrCreate(tag: DivDataTag, data: DivData, div2View: Div2View): RuntimeStore {
        divDataTags.getOrPut(div2View, ::mutableSetOf).add(tag.id)

        val errorCollector = errorCollectors.getOrCreate(tag, data)
        runtimeStores[tag.id]?.let {
            it.attachView(div2View)
            ensureVariablesSynced(it.rootRuntime, data, errorCollector)
            it.rootRuntime.triggersController?.ensureTriggersSynced(data.variableTriggers ?: emptyList())
            return it
        }

        return RuntimeStoreImpl(data, runtimeProvider, errorCollector).also {
            runtimeStores[tag.id] = it
            it.attachView(div2View)
        }
    }

    fun reset(tags: List<DivDataTag>) {
        if (tags.isEmpty()) {
            runtimeStores.clear()
        } else {
            tags.forEach { tag ->
                runtimeStores.remove(tag.id)
            }
        }
    }

    internal fun cleanupRuntime(view: Div2View) {
        divDataTags[view]?.forEach { tag ->
            runtimeStores[tag]?.cleanupRuntimes(view)
        }
        divDataTags.remove(view)
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
            is DivVariable.Property -> this.value.name
        }
    }
