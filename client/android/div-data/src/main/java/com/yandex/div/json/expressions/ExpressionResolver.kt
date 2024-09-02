package com.yandex.div.json.expressions

import com.yandex.div.core.Disposable
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException

interface ExpressionResolver {

    /**
     * Expects string inside expression brackets ('sum(var1, 1)' for expression '@{sum(var1, 1)}')
     * and returns real value of expression according to inner state of actual resolver.
     */
    fun <R, T : Any> get(
        expressionKey: String,
        rawExpression: String,
        evaluable: Evaluable,
        converter: Converter<R, T?>?,
        validator: ValueValidator<T>,
        fieldType: TypeHelper<T>,
        logger: ParsingErrorLogger,
    ): T?

    /**
     * Create subscription on expression value change.
     * @param variableNames list of strings, each defined within brackets '@{...}'. For example for '@{var1}' it should be 'var1'.
     * @param callback an action performed when any of variables changes its value.
     * @return subscription to any of [variableNames] change or [Disposable.NULL] if one isn't found.
     */
    fun subscribeToExpression(
        rawExpression: String,
        variableNames: List<String>,
        callback: () -> Unit
    ): Disposable

    fun notifyResolveFailed(e: ParsingException) = Unit

    companion object {
        /**
         * PLEASE AVOID USING EMPTY EXPRESSION RESOLVER!
         * It will throw exception on trying to resolve first non-constant expression
         * (for example with variables).
         */
        @JvmField
        val EMPTY = object : ExpressionResolver {

            override fun <R, T : Any> get(
                expressionKey: String,
                rawExpression: String,
                evaluable: Evaluable,
                converter: Converter<R, T?>?,
                validator: ValueValidator<T>,
                fieldType: TypeHelper<T>,
                logger: ParsingErrorLogger
            ): T? {
                return null
            }

            override fun subscribeToExpression(
                rawExpression: String, variableNames: List<String>,
                callback: () -> Unit
            ): Disposable {
                return Disposable.NULL
            }
        }
    }
}
