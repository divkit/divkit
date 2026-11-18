package com.yandex.div.compose.variables

import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.variables.variableValueToEvaluableValue

internal class ScopedVariableProvider(
    override val controller: DivVariableController,
    private val parent: VariableAndConstantProvider?,
) : VariableAndConstantProvider {
    private val declaredNames = mutableSetOf<String>()

    /**

     * A declaration shadows its parent only after its initial value has been evaluated.

     */
    fun declare(variable: Variable) {
        controller.declare(variable)
        declaredNames.add(variable.name)
    }

    override fun get(name: String): Any? {
        return if (parent == null || name in declaredNames) {
            controller.get(name)?.getValue().variableValueToEvaluableValue()
        } else {
            parent.get(name)
        }
    }

    override fun isConstant(name: String): Boolean =
        name !in declaredNames && parent?.isConstant(name) == true

    override fun subscribe(variableNames: List<String>, callback: () -> Unit): AutoCloseable? =
        parent?.subscribe(variableNames.filterNot { it in declaredNames }, callback)
}
