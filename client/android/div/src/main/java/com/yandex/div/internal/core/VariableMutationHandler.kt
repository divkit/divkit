package com.yandex.div.internal.core

import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VariableMutationHandler @Inject constructor() {

    /**
     * This method finds and mutates variable in scope of provided resolver.
     * @return exception if setting variable failed, null otherwise.
     */
    fun setVariable(
        name: String,
        value: String,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ): VariableMutationException? {
        val mutableVariable = resolver.getVariable(name)
            ?: return createAndReportError(null, errorCollector, "Variable '$name' not defined!")

        runCatching {
            mutableVariable.set(value)
        }.getOrElse {
            return createAndReportError(it, errorCollector, "Variable '$name' mutation failed!")
        }

        return null
    }

    /**
     * This method finds and mutates variable in scope of provided resolver.
     * @return exception if setting variable failed, null otherwise.
     * @param valueMutation - gets variable as argument for modification opportunities
     */
    fun <T : Variable> setVariable(
        name: String,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
        valueMutation: (T) -> T,
    ): VariableMutationException? {
        val mutableVariable = resolver.getVariable(name)
            ?: return createAndReportError(null, errorCollector, "Variable '$name' not defined!")

        runCatching {
            val newValue = valueMutation.invoke(mutableVariable as T)
            mutableVariable.setValue(newValue)
        }.getOrElse {
            return createAndReportError(it, errorCollector, "Variable '$name' mutation failed!")
        }

        return null
    }

    private fun createAndReportError(e: Throwable?, errorCollector: ErrorCollector, message: String) =
        VariableMutationException(message, e).also { errorCollector.logError(it) }
}
