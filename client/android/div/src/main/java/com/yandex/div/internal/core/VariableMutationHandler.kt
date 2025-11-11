package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.local.variableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.json.expressions.ExpressionResolver

@InternalApi
class VariableMutationHandler {
    companion object {
        @JvmStatic
        /**
         * This method finds and mutates variable in scope of provided resolver.
         * @return exception if setting variable failed, null otherwise.
         */
        fun setVariable(
            div2View: Div2View,
            name: String,
            value: String,
            resolver: ExpressionResolver
        ): VariableMutationException? {
            val mutableVariable = findVariable(name, resolver)
                ?: return createAndReportError(null, div2View, "Variable '$name' not defined!")

            runCatching {
                mutableVariable.set(value)
            }.getOrElse {
                return createAndReportError(it, div2View, "Variable '$name' mutation failed!")
            }

            return null
        }

        @JvmStatic
        /**
         * This method finds and mutates variable in scope of provided resolver.
         * @return exception if setting variable failed, null otherwise.
         * @param valueMutation - gets variable as argument for modification opportunities
         */
        fun <T : Variable> setVariable(
            div2View: Div2View,
            name: String,
            resolver: ExpressionResolver,
            valueMutation: (T) -> T,
        ): VariableMutationException? {
            val mutableVariable = findVariable(name, resolver)
                ?: return createAndReportError(null, div2View, "Variable '$name' not defined!")

            runCatching {
                val newValue = valueMutation.invoke(mutableVariable as T)
                mutableVariable.setValue(newValue)
            }.getOrElse {
                return createAndReportError(it, div2View, "Variable '$name' mutation failed!")
            }

            return null
        }

        private fun findVariable(name: String, resolver: ExpressionResolver) =
            resolver.variableController?.getMutableVariable(name)

        private fun createAndReportError(
            e: Throwable?,
            div2View: Div2View,
            message: String
        ): VariableMutationException {
            return VariableMutationException(message, e).also { div2View.logError(it) }
        }
    }
}
