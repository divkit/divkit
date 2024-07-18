package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.Variable

@Mockable
internal interface VariableSource {
    fun getMutableVariable(name: String): Variable?

    fun observeDeclaration(observer: (Variable) -> Unit)

    fun observeVariables(observer: (Variable) -> Unit)

    fun receiveVariablesUpdates(observer: (Variable) -> Unit)

    fun removeDeclarationObserver(observer: (Variable) -> Unit)

    fun removeVariablesObserver(observer: (Variable) -> Unit)

    companion object {
        operator fun invoke(
            variables: Map<String, Variable>,
            requestObserver: (variableName: String) -> Unit,
            declarationObservers: MutableCollection<(Variable) -> Unit>
        ): VariableSource {
            return SingleVariableSource(variables, requestObserver, declarationObservers)
        }
    }
}
