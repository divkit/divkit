package com.yandex.div.core.expression.variables

import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.internal.Assert
import com.yandex.div.json.missingVariable

internal fun <T> subscribeToVariable(
    variableName: String,
    errorCollector: ErrorCollector,
    variableController: VariableController,
    invokeChangeOnSubscription: Boolean = false,
    onChangeCallback: (T?) -> Unit
): Disposable {
    val variable = variableController.getMutableVariable(variableName)
        ?: run {
            errorCollector.logError(missingVariable(variableName))
            var changeDisposable: Disposable? = null
            val declareDisposable =
                variableController.declarationNotifier.doOnVariableDeclared(variableName) {
                    changeDisposable = subscribeToVariable<T>(
                        variableName,
                        errorCollector,
                        variableController,
                        invokeChangeOnSubscription = true,
                        onChangeCallback
                    )
                }

            return Disposable {
                declareDisposable.close()
                changeDisposable?.close()
            }
        }
    val onVariableChanged = { changed: Variable ->
        @Suppress("UNCHECKED_CAST")
        onChangeCallback.invoke(changed.getValue() as? T)
    }
    variable.addObserver(onVariableChanged)

    if (invokeChangeOnSubscription) {
        // Any on variable changed notify should be executed on main thread.
        Assert.assertMainThread()
        onVariableChanged.invoke(variable)
    }

    return Disposable {
        variable.removeObserver(onVariableChanged)
    }
}
