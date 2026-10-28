package com.yandex.div.compose.variables

import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.VariableProvider

/**
 * The variables an element resolves expressions against: the mutable variables of [controller]
 * and its parents plus the read-only constants of the enclosing item builder items.
 */
internal interface VariableAndConstantProvider : VariableProvider {

    /**
     * The nearest scope with mutable variables. Writes go through it; declarations go through
     * [ScopedVariableProvider.declare] so that they shadow the inherited names.
     */
    val controller: DivVariableController

    fun getVariable(name: String): Variable? = controller.get(name)

    /**

     * True when [name] resolves to a constant, so controller notifications for it are ignored.

     */
    fun isConstant(name: String): Boolean

    /**

     * Observes the constants among [variableNames]; `null` when none of them is a constant.

     */
    fun subscribe(variableNames: List<String>, callback: () -> Unit): AutoCloseable?
}
