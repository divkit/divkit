package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable

internal class VariableAndConstantController(
    private val delegate: VariableController,
    private val constants: ConstantsProvider,
) : VariableController {

    override fun get(name: String) = constants.get(name).wrapVariableValue() ?: delegate.get(name)

    override fun subscribeToVariablesChange(
        names: List<String>,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ) = delegate.subscribeToVariablesChange(names, invokeOnSubscription, observer)

    override fun subscribeToVariablesUndeclared(
        names: List<String>,
        observer: (Variable) -> Unit
    ): Disposable {
        return delegate.subscribeToVariablesUndeclared(names, observer)
    }

    override fun subscribeToVariableChange(
        name: String,
        errorCollector: ErrorCollector?,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ) = delegate.subscribeToVariableChange(name, errorCollector, invokeOnSubscription, observer)

    override fun addSource(source: VariableSource) = delegate.addSource(source)

    override fun getMutableVariable(name: String) = delegate.getMutableVariable(name)

    override fun declare(variable: Variable) = delegate.declare(variable)

    override fun setOnAnyVariableChangeCallback(callback: (Variable) -> Unit) =
        delegate.setOnAnyVariableChangeCallback(callback)

    override fun cleanupSubscriptions() = delegate.cleanupSubscriptions()

    override fun restoreSubscriptions() = delegate.restoreSubscriptions()
}
