package com.yandex.div.core.expression.variables

import com.yandex.div.data.Variable

internal class SingleVariableSource(
    private val variables: Map<String, Variable>,
    private val requestObserver: (variableName: String) -> Unit,
    private val declarationObservers: MutableCollection<DeclarationObserver>,
) : VariableSource {

    override fun getMutableVariable(name: String): Variable? {
        requestObserver.invoke(name)
        return variables[name]
    }

    override fun observeDeclaration(observer: DeclarationObserver) {
        declarationObservers.add(observer)
    }

    override fun observeVariables(observer: (Variable) -> Unit) {
        variables.values.forEach {
            it.addObserver(observer)
        }
    }

    override fun receiveVariablesUpdates(observer: (Variable) -> Unit) {
        variables.values.forEach {
            observer.invoke(it)
        }
    }

    override fun removeDeclarationObserver(observer: DeclarationObserver) {
        declarationObservers.remove(observer)
    }

    override fun removeVariablesObserver(observer: (Variable) -> Unit) {
        variables.values.forEach {
            it.removeObserver(observer)
        }
    }
}
