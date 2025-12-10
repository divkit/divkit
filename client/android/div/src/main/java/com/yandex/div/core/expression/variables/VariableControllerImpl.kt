package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.runBindingAction
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.missingVariable
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Provider

@Mockable
internal class VariableControllerImpl(
    private val viewProvider: Provider<Div2View?>,
    private val delegate: VariableController? = null,
): VariableController {
    private val variables = ConcurrentHashMap<String, Variable>()
    private val variableSources = Collections.synchronizedSet(mutableSetOf<VariableSource>())
    private val activeVariableSources = Collections.synchronizedSet(mutableSetOf<VariableSource>())
    private val onChangeObservers = ConcurrentHashMap<String, ObserverList<(Variable) -> Unit>>()
    private val onRemoveObservers = ConcurrentHashMap<String, ObserverList<(Variable) -> Unit>>()

    private val onAnyVariableChangeObservers = ConcurrentHashMap<ExpressionResolver, (Variable) -> Unit>()
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

    override fun get(name: String) = getMutableVariable(name)?.getValue().wrapVariableValue() ?: delegate?.get(name)

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
            doOnMainThread {
                // Any on variable changed notify should be executed on main thread.
                observer.invoke(variable)
            }
        }
        addObserver(name, observer)
        return
    }

    private fun removeChangeObserver(name: String, observer: (Variable) -> Unit) {
        onChangeObservers[name]?.removeObserver(observer)
    }

    private fun notifyVariableChanged(v: Variable) {
        doOnMainThread {
            onAnyVariableChangeObservers.values.toList().forEach { callback -> callback.invoke(v) }
            onChangeObservers[v.name]?.forEach {
                it.invoke(v)
            }
        }
    }

    private inline fun doOnMainThread(crossinline block: () -> Unit) {
        val divView = viewProvider.get()
        if (divView == null) {
            UiThreadHandler.executeOnMainThread(block)
        } else {
            divView.runBindingAction(block)
        }
    }

    /**
     * aka "adding references to global variables"
     */
    override fun addSource(source: VariableSource) {
        if (activeVariableSources.add(source)) {
            source.observeVariables(notifyVariableChangedCallback)
            source.observeDeclaration(declarationObserver)
            variableSources.add(source)
        }
    }

    private fun onVariableDeclared(variable: Variable) {
        variable.addObserver(notifyVariableChangedCallback)
        notifyVariableChanged(variable)
    }

    private fun onVariableRemoved(variable: Variable) {
        variable.removeObserver(notifyVariableChangedCallback)
        onRemoveObservers[variable.name]?.forEach { it.invoke(variable) }
        onAnyVariableChangeObservers.values.toList().forEach { callback ->
            callback.invoke(variable)
            variable.removeObserver(callback)
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

        variableSources.forEach { source: VariableSource ->
            source.getMutableVariable(name)?.let {
                return it
            }
        }

        return null
    }

    override fun cleanupSubscriptions() {
        variableSources.forEach { variableSource ->
            variableSource.removeVariablesObserver(notifyVariableChangedCallback)
            variableSource.removeDeclarationObserver(declarationObserver)
            activeVariableSources.remove(variableSource)
        }
        onAnyVariableChangeObservers.clear()
    }

    override fun restoreSubscriptions() {
        variableSources.forEach { variableSource ->
            variableSource.receiveVariablesUpdates(notifyVariableChangedCallback)

            if (activeVariableSources.add(variableSource)) {
                variableSource.observeVariables(notifyVariableChangedCallback)
                variableSource.observeDeclaration(declarationObserver)
            }
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

    override fun setOnAnyVariableChangeCallback(owner: ExpressionResolver, callback: (Variable) -> Unit) {
        onAnyVariableChangeObservers[owner] = callback
        delegate?.setOnAnyVariableChangeCallback(owner) {
            if (variables[it.name] == null) callback.invoke(it)
        }
    }

    override fun captureAll(): List<Variable> {
        return variables.values.toList()
    }
}
