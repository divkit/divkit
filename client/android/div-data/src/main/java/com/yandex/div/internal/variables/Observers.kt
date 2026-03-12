package com.yandex.div.internal.variables

import com.yandex.div.data.Variable

typealias VariableRequestObserver = (String) -> Unit

interface DeclarationObserver {
    fun onDeclared(variable: Variable)
    fun onUndeclared(variable: Variable)
}
