package com.yandex.div.internal.expression

import com.yandex.div.core.CompositeDisposable
import com.yandex.div.core.Disposable
import com.yandex.div.core.plusAssign
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.resolveFailed
import com.yandex.div.json.typeMismatch
import java.util.concurrent.ConcurrentHashMap

private const val EXPR_SYMBOL = '@'
private const val EXPR_OPEN_BRACKET = '{'
private const val EXPR_OPEN_SYMBOLS = "$EXPR_SYMBOL$EXPR_OPEN_BRACKET"

/**
 * Expression represents a string with special placeholders that may reference to
 * variables or operations above set of variables. For example:
 * "Profile: @{name} (@{email})"
 * can be [evaluate]d into "Profile: John (john1@email.net)"
 */
abstract class Expression<T : Any> {
    /**
     * Allows to get the value of expression which is based on provided resolver.
     * WARNING! Resolvers like [ExpressionResolver.EMPTY] will generate errors.
     * So choose you resolver with care.
     */
    abstract fun evaluate(resolver: ExpressionResolver): T

    /**
     * Subscribes to expression value changes.
     * WARNING! Resolvers like [ExpressionResolver.EMPTY] will generate errors.
     * So choose you resolver with care.
     * @param resolver a context for expression evaluation.
     */
    abstract fun observe(resolver: ExpressionResolver, callback: (T) -> Unit): Disposable

    /**
     * Subscribes to expression value changes and invokes callback immediately for current value.
     * WARNING! Resolvers like [ExpressionResolver.EMPTY] will generate errors.
     * So choose you resolver with care.
     * @param resolver a context for expression evaluation.
     */
    open fun observeAndGet(resolver: ExpressionResolver, callback: (T) -> Unit): Disposable {
        val value: T? = try {
            evaluate(resolver)
        } catch (e: ParsingException) {
            // Ok to ignore, tryResolveOrUseLast will log about exception.
            null
        }
        value?.let {
            callback.invoke(it)
        }
        return observe(resolver, callback)
    }

    /**
     * Value of expression is mutable so we cannot use it in equals/hashcode
     * instead we'll use its raw value.
     */
    // TODO: set internal after TriggersController fix
    abstract val rawValue: Any

    override fun equals(other: Any?): Boolean {
        if (other !is Expression<*>) {
            return false
        }

        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        return rawValue.hashCode() * 16
    }

    class ConstantExpression<T : Any>(private val value: T) : Expression<T>() {
        override val rawValue
            get() = value as Any

        override fun evaluate(resolver: ExpressionResolver): T = value

        override fun observe(resolver: ExpressionResolver, callback: (T) -> Unit): Disposable {
            return Disposable.NULL
        }

        override fun observeAndGet(
            resolver: ExpressionResolver,
            callback: (T) -> Unit
        ): Disposable {
            callback(value)
            return Disposable.NULL
        }
    }

    /**
     * @param fieldDefaultValue default value of field where this expression is used. Used for fallbacks.
     */
    class MutableExpression<R, T : Any>(
        private val expressionKey: String,
        private val rawExpression: String,
        private val converter: Converter<R, T?>?,
        private val validator: ValueValidator<T>,
        private val logger: ParsingErrorLogger,
        private val typeHelper: TypeHelper<T>,
        private val fieldDefaultValue: Expression<T>? = null,
    ) : Expression<T>() {
        override val rawValue = rawExpression
        private var evaluable: Evaluable? = null

        override fun evaluate(resolver: ExpressionResolver): T = tryResolveOrUseLast(resolver)

        override fun observe(resolver: ExpressionResolver, callback: (T) -> Unit): Disposable {
            val variablesName = try {
                getEvaluable().variables
            } catch (e: Exception) {
                logError(resolveFailed(expressionKey, rawExpression, e), resolver)
                return Disposable.NULL
            }
            return variablesName
                .ifEmpty { return Disposable.NULL }
                .fold(CompositeDisposable()) { subscription, variable ->
                    subscription += resolver.onChange<T>(variable) {
                        callback(evaluate(resolver))
                    }
                    subscription
                }
        }

        private fun tryResolveOrUseLast(resolver: ExpressionResolver): T {
            try {
                val newValue = tryResolve(resolver)
                lastValidValue = newValue
                return newValue
            } catch (e: ParsingException) {
                logError(e, resolver)
                lastValidValue?.let {
                    return it
                }

                try {
                    fieldDefaultValue?.evaluate(resolver)?.let {
                        lastValidValue = it
                        return it
                    }
                } catch (e: ParsingException) {
                    logError(e, resolver)
                    throw e
                }

                return typeHelper.typeDefault
            }
        }

        private fun logError(e: ParsingException, resolver: ExpressionResolver) {
            logger.logError(e)
            resolver.notifyResolveFailed(e)
        }

        private var lastValidValue: T? = null

        private fun tryResolve(resolver: ExpressionResolver): T {
            val value = resolver.get(
                expressionKey,
                rawExpression,
                getEvaluable(),
                converter,
                validator,
                typeHelper,
                logger
            ) ?: throw resolveFailed(expressionKey, rawExpression)

            if (!typeHelper.isTypeValid(value)) {
                throw typeMismatch(
                    expressionKey = expressionKey,
                    rawExpression = rawExpression,
                    wrongTypeValue = value,
                )
            }

            return value
        }

        private fun getEvaluable(): Evaluable {
            return evaluable ?: try {
                Evaluable.lazy(rawExpression).also {
                    evaluable = it
                }
            } catch (e: EvaluableException) {
                throw resolveFailed(expressionKey, rawExpression, e)
            }
        }
    }

    companion object {
        private val pool = ConcurrentHashMap<Any, Expression<*>>(1000)

        @JvmStatic
        fun <T : Any> constant(value: T): Expression<T> {
            val unTypedExpression = pool.getOrPut(value) { ConstantExpression(value) }
            return unTypedExpression as Expression<T>
        }

        @JvmStatic
        fun mayBeExpression(value: Any?): Boolean = value is String &&
                value.contains(EXPR_OPEN_SYMBOLS)
    }

}
