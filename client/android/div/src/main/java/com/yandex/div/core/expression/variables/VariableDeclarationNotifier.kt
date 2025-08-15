package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.data.Variable

internal fun interface VariableDeclarationNotifier {
    fun doOnVariableDeclared(name: String, action: (Variable) -> Unit): Disposable
}
