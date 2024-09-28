package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.internal.Assert
import com.yandex.div.json.missingVariable

@Mockable
internal class VariableControllerImpl(
    private val delegate: VariableController? = null,
): VariableController {
    private val variables = mutableMapOf<String, Variable>()
    private val extraVariablesSources = mutableListOf<VariableSource>()
    private val onChangeObservers = mutableMapOf<String, ObserverList<(Variable) -> Unit>>()
    private val onRemoveObservers = mutableMapOf<String, ObserverList<(Variable) -> Unit>>()

    private val onAnyVariableChangeObservers = ObserverList<(Variable) -> Unit>()
    private val notifyVariableChangedCallback = { v : Variable -> notifyVariableChanged(v) }
    private val declarationObserver = object : DeclarationObserver {
        override fun onDeclared(variable: Variable) = onVariableDeclared(variable)
        override fun onUndeclared(variable: Variable) = onVariableRemoved(variable)
    }

    private fun addObserver(name: String, observer: (Variable) -> Unit) {
        val observers = onChangeObservers.getOrPut(name) {
            ObserverList()
        }
        observers.addObserver(observer)
    }

    override fun subscribeToVariablesChange(
        names: List<String>,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ): Disposable {
        val disposables = mutableListOf<Disposable>()
        names.forEach { name ->
            if (!variables.containsKey(name) && delegate?.getMutableVariable(name) != null) {
                // if required variable stored in delegate we should subscribe on it
                delegate.subscribeToVariableChange(
                    name = name,
                    errorCollector = null,
                    invokeOnSubscription = invokeOnSubscription,
                    observer = observer,
                ).also { disposables.add(it) }
            } else {
                subscribeToVariableChangeImpl(name, null, invokeOnSubscription, observer)
            }
        }

        return Disposable {
            names.forEach { removeChangeObserver(it, observer) }
            disposables.forEach { it.close() }
        }
    }

    override fun subscribeToVariableChange(
        name: String,
        errorCollector: ErrorCollector?,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ): Disposable {
        return if (!variables.containsKey(name) && delegate?.getMutableVariable(name) != null) {
            // if required variable stored in delegate we should subscribe on it
            delegate.subscribeToVariableChange(
                name = name,
                errorCollector = errorCollector,
                invokeOnSubscription = invokeOnSubscription,
                observer = observer,
            )
        } else {
            subscribeToVariableChangeImpl(name, errorCollector, invokeOnSubscription, observer)
            Disposable { removeChangeObserver(name, observer) }
        }
    }

    override fun subscribeToVariablesUndeclared(
        names: List<String>,
        observer: (Variable) -> Unit
    ): Disposable {
        names.forEach {
            onRemoveObservers.getOrPut(it) {
                ObserverList<(Variable) -> Unit>()
            }.addObserver(observer)
        }

        return Disposable {
            names.forEach {
                onRemoveObservers[it]?.removeObserver(observer)
            }
        }
    }

    private fun subscribeToVariableChangeImpl(
        name: String,
        errorCollector: ErrorCollector? = null,
        invokeOnSubscription: Boolean = false,
        observer: (Variable) -> Unit
    ) {
        val variable = getMutableVariable(name) ?: run {
            errorCollector?.logError(missingVariable(name))
            addObserver(name, observer)
            return
        }

        if (invokeOnSubscription) {
            // Any on variable changed notify should be executed on main thread.
            Assert.assertMainThread()
            observer.invoke(variable)
        }
        addObserver(name, observer)
        return
    }

    private fun removeChangeObserver(name: String, observer: (Variable) -> Unit) {
        onChangeObservers[name]?.removeObserver(observer)
    }

    private fun notifyVariableChanged(v: Variable) {
        Assert.assertMainThread()
        onAnyVariableChangeObservers.forEach { it.invoke(v) }
        onChangeObservers[v.name]?.forEach {
            it.invoke(v)
        }
    }

    /**
     * aka "adding references to global variables"
     */
    override fun addSource(source: VariableSource) {
        source.observeVariables(notifyVariableChangedCallback)
        source.observeDeclaration(declarationObserver)
        extraVariablesSources.add(source)
    }

    private fun onVariableDeclared(variable: Variable) {
        variable.addObserver(notifyVariableChangedCallback)
        notifyVariableChanged(variable)
    }

    private fun onVariableRemoved(variable: Variable) {
        variable.removeObserver(notifyVariableChangedCallback)
        onRemoveObservers[variable.name]?.forEach { it.invoke(variable) }
        onAnyVariableChangeObservers.forEach {
            it.invoke(variable)
            variable.removeObserver(it)
        }

        variables.remove(variable.name)
    }

    override fun getMutableVariable(name: String): Variable? {
        variables[name]?.let {
            return it
        }

        delegate?.getMutableVariable(name)?.let {
            return it
        }

        extraVariablesSources.forEach { source: VariableSource ->
            source.getMutableVariable(name)?.let {
                return it
            }
        }

        return null
    }

    override fun cleanupSubscriptions() {
        extraVariablesSources.forEach { variableSource ->
            variableSource.removeVariablesObserver(notifyVariableChangedCallback)
            variableSource.removeDeclarationObserver(declarationObserver)
        }
        onAnyVariableChangeObservers.clear()
    }

    override fun restoreSubscriptions() {
        extraVariablesSources.forEach { variableSource ->
            variableSource.observeVariables(notifyVariableChangedCallback)
            variableSource.receiveVariablesUpdates(notifyVariableChangedCallback)
            variableSource.observeDeclaration(declarationObserver)
        }
    }

    @Throws(VariableDeclarationException::class)
    override fun declare(variable: Variable) {
        val prevVariable = variables.put(variable.name, variable)
        if (prevVariable != null) {
            variables[variable.name] = prevVariable
            throw VariableDeclarationException("Variable '${variable.name}' already declared!")
        }
        onVariableDeclared(variable)
    }

    override fun setOnAnyVariableChangeCallback(callback: (Variable) -> Unit) {
        onAnyVariableChangeObservers.addObserver(callback)
        delegate?.setOnAnyVariableChangeCallback {
            if (variables[it.name] == null) callback.invoke(it)
        }
    }
}
