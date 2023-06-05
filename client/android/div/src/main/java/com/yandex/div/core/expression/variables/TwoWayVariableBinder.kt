package com.yandex.div.core.expression.variables

import androidx.annotation.MainThread
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionsRuntimeProvider
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import javax.inject.Inject

@DivScope
@Mockable
internal class TwoWayStringVariableBinder @Inject constructor(
    errorCollectors: ErrorCollectors,
    expressionsRuntimeProvider: ExpressionsRuntimeProvider
) : TwoWayVariableBinder<String>(errorCollectors, expressionsRuntimeProvider) {

    interface Callbacks : TwoWayVariableBinder.Callbacks<String>

    override fun String.toStringValue() = this
}

@DivScope
@Mockable
internal class TwoWayIntegerVariableBinder @Inject constructor(
    errorCollectors: ErrorCollectors,
    expressionsRuntimeProvider: ExpressionsRuntimeProvider
) : TwoWayVariableBinder<Long>(errorCollectors, expressionsRuntimeProvider) {

    interface Callbacks : TwoWayVariableBinder.Callbacks<Long>

    override fun Long.toStringValue() = toString()
}

@Mockable
internal abstract class TwoWayVariableBinder<T>(
    private val errorCollectors: ErrorCollectors,
    private val expressionsRuntimeProvider: ExpressionsRuntimeProvider
) {

    @Mockable
    interface Callbacks<T> {
        @MainThread
        fun onVariableChanged(value: T?)
        fun setViewStateChangeListener(valueUpdater: (T) -> Unit)
    }

    fun bindVariable(divView: Div2View, variableName: String, callbacks: Callbacks<T>): Disposable {
        val data = divView.divData ?: return Disposable.NULL

        var pendingValue: T? = null
        val tag = divView.dataTag
        var variable: Variable? = null
        val variableController =
            expressionsRuntimeProvider.getOrCreate(tag, data).variableController
        callbacks.setViewStateChangeListener { value ->
            if (pendingValue == value) return@setViewStateChangeListener
            pendingValue = value
            (variable
                ?: variableController.getMutableVariable(variableName)
                    .also { variable = it })
                ?.set(value.toStringValue())
        }

        return variableController.subscribeToVariableChange(
            variableName,
            errorCollectors.getOrCreate(tag, data),
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
