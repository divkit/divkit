package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.data.Variable
import com.yandex.div.util.SynchronizedList

@Mockable
class VariableSource(
    private val variables: Map<String, Variable>,
    private val requestObserver: (variableName: String) -> Unit,
    private val declarationObservers: SynchronizedList<(Variable) -> Unit>
) {

    internal fun getMutableVariable(name: String): Variable? {
        requestObserver.invoke(name)
        return variables[name]
    }

    internal fun observeDeclaration(observer: (Variable) -> Unit) {
        declarationObservers.add(observer)
    }
}
