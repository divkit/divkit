package com.yandex.div.core.expression.variables

import androidx.annotation.MainThread
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject

@DivScope
internal class TwoWayStringVariableBinder @Inject constructor() : TwoWayVariableBinder<String>() {

    interface Callbacks : TwoWayVariableBinder.Callbacks<String>

    override fun String.toStringValue() = this
}

@DivScope
internal class TwoWayIntegerVariableBinder @Inject constructor() : TwoWayVariableBinder<Long>() {

    interface Callbacks : TwoWayVariableBinder.Callbacks<Long>

    override fun Long.toStringValue() = toString()
}

@DivScope
internal class TwoWayBooleanVariableBinder @Inject constructor() : TwoWayVariableBinder<Boolean>() {

    interface Callbacks : TwoWayVariableBinder.Callbacks<Boolean>

    override fun Boolean.toStringValue(): String = toString()
}

internal abstract class TwoWayVariableBinder<T> {

    interface Callbacks<T> {
        @MainThread
        fun onVariableChanged(value: T?)
        fun setViewStateChangeListener(valueUpdater: (T) -> Unit)
    }

    fun bindVariable(
        variableName: String,
        resolver: ExpressionResolver,
        divView: Div2View,
        callbacks: Callbacks<T>,
    ): Disposable {
        val resolver = resolver.asImpl ?: return Disposable.NULL

        var pendingValue: T? = null

        callbacks.setViewStateChangeListener { value ->
            if (pendingValue == value) return@setViewStateChangeListener
            pendingValue = value
            VariableMutationHandler.setVariable(divView, variableName, value.toStringValue(), resolver)
        }

        return resolver.variableController.subscribeToVariableChange(
            variableName,
            divView.errorCollector,
            invokeOnSubscription = true
        ) { changed: Variable ->
            @Suppress("UNCHECKED_CAST")
            val value = changed.getValue() as? T
            if (pendingValue == value) return@subscribeToVariableChange
            pendingValue = value
            callbacks.onVariableChanged(value)
        }
    }

    abstract fun T.toStringValue(): String
}
