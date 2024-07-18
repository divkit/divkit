package com.yandex.div.core.expression.variables

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.internal.Assert
import com.yandex.div.json.missingVariable

@Mockable
internal class VariableControllerImpl : VariableController {
    private val variables = mutableMapOf<String, Variable>()
    private val extraVariablesSources = mutableListOf<VariableSource>()
    private val onChangeObservers = mutableMapOf<String, ObserverList<(Variable) -> Unit>>()

    private val onAnyVariableChangeObservers = ObserverList<(Variable) -> Unit>()
    private val notifyVariableChangedCallback = { v : Variable -> notifyVariableChanged(v) }
    private val declarationObserver = { v : Variable -> onVariableDeclared(v) }

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
        names.forEach { name ->
            subscribeToVariableChangeImpl(name, null, invokeOnSubscription, observer)
        }
        return Disposable {
            names.forEach { name ->
                removeChangeObserver(name, observer)
            }
        }
    }

    override fun subscribeToVariableChange(
        name: String,
        errorCollector: ErrorCollector?,
        invokeOnSubscription: Boolean,
        observer: (Variable) -> Unit
    ): Disposable {
        subscribeToVariableChangeImpl(name, errorCollector, invokeOnSubscription, observer)
        return Disposable {
            removeChangeObserver(name, observer)
        }
    }

    private fun subscribeToVariableChangeImpl(
        name: String,
        errorCollector: ErrorCollector? = null,
        invokeOnSubscription: Boolean = false,
        observer: (Variable) -> Unit
    ) {
        val variable = getMutableVariable(name)
            ?: run {
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
        onChangeObservers[v.name]?.forEach { it.invoke(v) }
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

    override fun getMutableVariable(name: String): Variable? {
        variables[name]?.let {
            return it
        }

        extraVariablesSources.forEach { source: VariableSource ->
            source.getMutableVariable(name)?.let {
                return it
            }
        }

        return null
    }

    internal fun cleanupSubscriptions() {
        extraVariablesSources.forEach { variableSource ->
            variableSource.removeVariablesObserver(notifyVariableChangedCallback)
            variableSource.removeDeclarationObserver(declarationObserver)
        }
        onAnyVariableChangeObservers.clear()
    }

    internal fun restoreSubscriptions() {
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
    }
}
