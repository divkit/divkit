package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.Variable

@Mockable
internal class VariableController(
    private val variables: Map<String, Variable>,
) {
    private val extraVariablesSources = mutableListOf<VariableSource>()
    private val declarationObservers = mutableMapOf<String, MutableList<(Variable) -> Unit>>()

    internal val declarationNotifier = VariableDeclarationNotifier { name, action ->
        subscribeToDeclaration(name, action)
    }

    private fun subscribeToDeclaration(name: String, action: (Variable) -> Unit): Disposable {
        getMutableVariable(name)?.let { variable ->
            action(variable)
            return Disposable.NULL
        }

        val variableObservers = declarationObservers.getOrPut(name) { mutableListOf() }
        variableObservers.add(action)

        return Disposable {
            variableObservers.remove(action)
        }
    }

    /**
     * aka "adding references to global variables"
     */
    fun addSource(source: VariableSource) {
        source.observeDeclaration { onVariableDeclared(it) }
        extraVariablesSources.add(source)
    }

    private fun onVariableDeclared(variable: Variable) {
        declarationObservers[variable.name]?.let { observers ->
            observers.forEach { it.invoke(variable) }
            observers.clear()
        }
    }

    fun getMutableVariable(name: String): Variable? {
        variables[name]?.let {
            return it
        }

        extraVariablesSources.forEach { source: VariableSource ->
            source.getMutableVariable(name)?.let {
                return it
            }
        }

        return null
    }
}
