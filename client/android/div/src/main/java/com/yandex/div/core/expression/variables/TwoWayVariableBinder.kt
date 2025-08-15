package com.yandex.div.core.expression.variables

import androidx.annotation.MainThread
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.variableController
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
) : TwoWayVariableBinder<String>(errorCollectors) {

    interface Callbacks : TwoWayVariableBinder.Callbacks<String>

    override fun String.toStringValue() = this
}

@DivScope
@Mockable
internal class TwoWayIntegerVariableBinder @Inject constructor(
    errorCollectors: ErrorCollectors,
) : TwoWayVariableBinder<Long>(errorCollectors) {

    interface Callbacks : TwoWayVariableBinder.Callbacks<Long>

    override fun Long.toStringValue() = toString()
}

@DivScope
@Mockable
internal class TwoWayBooleanVariableBinder @Inject constructor(
    errorCollectors: ErrorCollectors,
) : TwoWayVariableBinder<Boolean>(errorCollectors) {

    interface Callbacks : TwoWayVariableBinder.Callbacks<Boolean>

    override fun Boolean.toStringValue(): String = toString()
}

@Mockable
internal abstract class TwoWayVariableBinder<T>(private val errorCollectors: ErrorCollectors) {

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
        val variableController = bindingContext.expressionResolver.variableController ?: return Disposable.NULL

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
