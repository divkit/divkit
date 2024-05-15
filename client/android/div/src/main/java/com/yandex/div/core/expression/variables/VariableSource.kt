package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.Variable

@Mockable
internal class VariableSource(
    private val variables: Map<String, Variable>,
    private val requestObserver: (variableName: String) -> Unit,
    private val declarationObservers: MutableCollection<(Variable) -> Unit>
) {

    internal fun getMutableVariable(name: String): Variable? {
        requestObserver.invoke(name)
        return variables[name]
    }

    internal fun observeDeclaration(observer: (Variable) -> Unit) {
        declarationObservers.add(observer)
    }

    internal fun observeVariables(observer: (Variable) -> Unit) {
        variables.values.forEach {
            it.addObserver(observer)
        }
    }

    internal fun receiveVariablesUpdates(observer: (Variable) -> Unit) {
        variables.values.forEach {
            observer.invoke(it)
        }
    }

    internal fun removeDeclarationObserver(observer: (Variable) -> Unit) {
        declarationObservers.remove(observer)
    }

    internal fun removeVariablesObserver(observer: (Variable) -> Unit) {
        variables.values.forEach {
            it.removeObserver(observer)
        }
    }
}
