package com.yandex.div.core.expression.variables

import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable

internal class LocalVariableController(
    private val delegate: VariableController,
    private val localVariables: VariableSource
) : VariableController {

    override fun subscribeToVariablesChange(
        names: List<String>,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ) = delegate.subscribeToVariablesChange(names, invokeOnSubscription, observer)

    override fun subscribeToVariableChange(
        name: String,
        errorCollector: ErrorCollector?,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ) = delegate.subscribeToVariableChange(name, errorCollector, invokeOnSubscription, observer)

    override fun addSource(source: VariableSource) = delegate.addSource(source)

    override fun getMutableVariable(name: String) =
        localVariables.getMutableVariable(name) ?: delegate.getMutableVariable(name)

    override fun declare(variable: Variable) = delegate.declare(variable)

    override fun setOnAnyVariableChangeCallback(callback: (Variable) -> Unit) =
        delegate.setOnAnyVariableChangeCallback(callback)

    override fun cleanupSubscriptions() = delegate.cleanupSubscriptions()

    override fun restoreSubscriptions() = delegate.restoreSubscriptions()
}
