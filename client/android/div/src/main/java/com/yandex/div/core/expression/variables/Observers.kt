package com.yandex.div.core.expression.variables

import com.yandex.div.data.Variable

internal typealias VariableRequestObserver = (String) -> Unit
internal interface DeclarationObserver {
    fun onDeclared(variable: Variable)
    fun onUndeclared(variable: Variable)
}
