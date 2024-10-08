package com.yandex.div.core.expression.variables

import androidx.annotation.MainThread
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.ExpressionsRuntimeProvider
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
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

    fun bindVariable(
        bindingContext: BindingContext,
        variableName: String,
        callbacks: Callbacks<T>,
        path: DivStatePath,
    ): Disposable {
        val divView = bindingContext.divView
        val data = divView.divData ?: return Disposable.NULL

        var pendingValue: T? = null
        val tag = divView.dataTag
        val variableController = bindingContext.runtimeStore?.let { runtimeStore ->
            runtimeStore.getRuntimeWithOrNull(bindingContext.expressionResolver)?.variableController
        } ?: expressionsRuntimeProvider.getOrCreate(tag, data, divView).variableController

        callbacks.setViewStateChangeListener { value ->
            if (pendingValue == value) return@setViewStateChangeListener
            pendingValue = value
            VariableMutationHandler.setVariable(
                divView,
                variableName,
                value.toStringValue(),
                bindingContext.expressionResolver
            )
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
