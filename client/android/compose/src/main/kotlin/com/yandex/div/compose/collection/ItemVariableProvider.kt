package com.yandex.div.compose.collection

import com.yandex.div.compose.variables.VariableAndConstantProvider
import com.yandex.div.core.ObserverList
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.internal.variables.variableValueToEvaluableValue

/**

 * Updates item data and index together, without allowing actions to write these values.

 */
internal class ItemVariableProvider(
    private val parent: VariableAndConstantProvider,
    private var values: Map<String, Any>,
) : VariableAndConstantProvider {
    private val observers = ObserverList<() -> Unit>()
    private var hasPendingChanges = false

    override val controller: DivVariableController
        get() = parent.controller

    override fun isConstant(name: String): Boolean = name in values || parent.isConstant(name)

    override fun get(name: String): Any? =
        values[name].variableValueToEvaluableValue() ?: parent.get(name)

    fun update(values: Map<String, Any>) {
        if (this.values === values) return
        this.values = values
        hasPendingChanges = true
    }

    /**

     * Dispatch only after composition applies, because observers can invoke host actions.

     */
    fun notifyChanges() {
        if (!hasPendingChanges) return
        hasPendingChanges = false
        observers.forEach { it() }
    }

    override fun subscribe(variableNames: List<String>, callback: () -> Unit): AutoCloseable? {
        val ownNames = variableNames.filter { it in values }
        var observedValues = values
        val observer: () -> Unit = {
            val previousValues = observedValues
            observedValues = values
            if (ownNames.any { previousValues[it] != observedValues[it] }) callback()
        }
        if (ownNames.isNotEmpty()) observers.addObserver(observer)
        val inherited = parent.subscribe(variableNames.filterNot { it in values }, callback)
        if (ownNames.isEmpty()) return inherited
        return AutoCloseable {
            observers.removeObserver(observer)
            inherited?.close()
        }
    }
}
