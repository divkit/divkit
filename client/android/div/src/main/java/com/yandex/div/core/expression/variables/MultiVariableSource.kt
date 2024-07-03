package com.yandex.div.core.expression.variables

import com.yandex.div.data.Variable

internal class MultiVariableSource(
    private val variableController: DivVariableController,
    private val variableRequestObserver: (String) -> Unit,
) : VariableSource {
    override fun getMutableVariable(name: String): Variable? {
        variableRequestObserver.invoke(name)
        return variableController.get(name)
    }

    override fun observeDeclaration(observer: (Variable) -> Unit) {
        variableController.addDeclarationObserver(observer)
    }

    override fun removeDeclarationObserver(observer: (Variable) -> Unit) {
        variableController.removeDeclarationObserver(observer)
    }

    override fun observeVariables(observer: (Variable) -> Unit) {
        variableController.addVariableObserver(observer)
    }

    override fun removeVariablesObserver(observer: (Variable) -> Unit) {
        variableController.removeVariablesObserver(observer)
    }

    override fun receiveVariablesUpdates(observer: (Variable) -> Unit) {
        variableController.receiveVariablesUpdates(observer)
    }
}
