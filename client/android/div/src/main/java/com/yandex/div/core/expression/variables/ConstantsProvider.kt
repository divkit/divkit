package com.yandex.div.core.expression.variables

import com.yandex.div.evaluable.VariableProvider

internal class ConstantsProvider(private val constants: Map<String, Any>) : VariableProvider {
    override fun get(name: String) = constants[name]
}
