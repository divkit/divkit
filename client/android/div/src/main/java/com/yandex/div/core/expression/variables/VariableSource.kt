package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.Variable

@Mockable
internal interface VariableSource {
    fun getMutableVariable(name: String): Variable?

    fun observeDeclaration(observer: DeclarationObserver)

    fun observeVariables(observer: (Variable) -> Unit)

    fun receiveVariablesUpdates(observer: (Variable) -> Unit)

    fun removeDeclarationObserver(observer: DeclarationObserver)

    fun removeVariablesObserver(observer: (Variable) -> Unit)

    companion object {
        operator fun invoke(
            variables: Map<String, Variable>,
            requestObserver: (variableName: String) -> Unit,
            declarationObservers: MutableCollection<DeclarationObserver>
        ): VariableSource {
            return SingleVariableSource(
                variables,
                requestObserver,
                declarationObservers
            )
        }
    }
}
