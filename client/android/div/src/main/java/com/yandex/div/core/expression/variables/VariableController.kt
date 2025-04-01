package com.yandex.div.core.expression.variables

import android.net.Uri
import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.types.Url
import com.yandex.div.json.expressions.ExpressionResolver

internal interface VariableController : VariableProvider {

    fun subscribeToVariablesChange(
        names: List<String>,
        invokeOnSubscription: Boolean = false,
        observer: (Variable) -> Unit
    ): Disposable

    fun subscribeToVariablesUndeclared(
        names: List<String>,
        observer: (Variable) -> Unit
    ): Disposable

    fun subscribeToVariableChange(
        name: String,
        errorCollector: ErrorCollector? = null,
        invokeOnSubscription: Boolean = false,
        observer: (Variable) -> Unit
    ): Disposable

    fun addSource(source: VariableSource)

    fun getMutableVariable(name: String): Variable?

    fun declare(variable: Variable)

    fun setOnAnyVariableChangeCallback(owner: ExpressionResolver, callback: (Variable) -> Unit)

    fun cleanupSubscriptions()

    fun restoreSubscriptions()

    fun captureAll(): List<Variable> = emptyList()
}

internal fun Any?.wrapVariableValue() = when(this) {
    is Uri -> Url(this.toString())
    else -> this
}
