package com.yandex.div.json.expressions

import com.yandex.div.core.CompositeDisposable
import com.yandex.div.core.Disposable
import com.yandex.div.internal.parser.ListValidator
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.invalidValue

/**
 * A container for collection of [Expression]s with validation of evaluated values.
 */
sealed interface ExpressionList<T : Any> {
    /**
     * Allows to get list of values from list of expression which are based on provided resolver.
     * WARNING! Resolvers like [ExpressionResolver.EMPTY] will generate errors.
     * So choose you resolver with care.
     */
    @Throws(ParsingException::class)
    fun evaluate(resolver: ExpressionResolver): List<T>

    /**
     * Subscribes to expression value changes.
     * WARNING! Resolvers like [ExpressionResolver.EMPTY] will generate errors.
     * So choose you resolver with care.
     * @param resolver a context for expression evaluation.
     */
    fun observe(resolver: ExpressionResolver, callback: (List<T>) -> Unit): Disposable

    /**
     * Subscribes to expression value changes and invokes callback immediately for current value.
     * WARNING! Resolvers like [ExpressionResolver.EMPTY] will generate errors.
     * So choose you resolver with care.
     * @param resolver a context for expression evaluation.
     */
    fun observeAndGet(resolver: ExpressionResolver, callback: (List<T>) -> Unit): Disposable
}

class ConstantExpressionList<T : Any>(
    internal val values: List<T>,
) : ExpressionList<T> {

    override fun evaluate(resolver: ExpressionResolver): List<T> {
        return values
    }

    override fun observe(resolver: ExpressionResolver, callback: (List<T>) -> Unit): Disposable {
        return Disposable.NULL
    }

    override fun observeAndGet(
        resolver: ExpressionResolver,
        callback: (List<T>) -> Unit
    ): Disposable {
        callback(values)
        return Disposable.NULL
    }

    override fun hashCode(): Int {
        return values.hashCode() * 16
    }

    override fun equals(other: Any?): Boolean {
        return other is ConstantExpressionList<*> && values == other.values
    }
}

internal class MutableExpressionList<T : Any>(
    private val key: String,
    internal val expressions: List<Expression<T>>,
    private val listValidator: ListValidator<T>,
    private val logger: ParsingErrorLogger,
) : ExpressionList<T> {

    private var lastValidValuesList: List<T>? = null

    override fun evaluate(resolver: ExpressionResolver): List<T> {
        try {
            val value = tryResolve(resolver)
            lastValidValuesList = value
            return value
        } catch (e: ParsingException) {
            logger.logError(e)
            lastValidValuesList?.let {
                return it
            }

            throw e
        }
    }

    private fun tryResolve(resolver: ExpressionResolver): List<T> {
        val values = expressions.map { it.evaluate(resolver) }
        if (!listValidator.isValid(values)) {
            throw invalidValue(key, values)
        }
        return values
    }

    override fun observe(resolver: ExpressionResolver, callback: (List<T>) -> Unit): Disposable {
        val itemCallback = { _: T -> callback(evaluate(resolver)) }

        if (expressions.size == 1) {
            return expressions.first().observe(resolver, itemCallback)
        }

        val disposable = CompositeDisposable()
        expressions.forEach {
            disposable.add(it.observe(resolver, itemCallback))
        }

        return disposable
    }

    override fun observeAndGet(
        resolver: ExpressionResolver,
        callback: (List<T>) -> Unit
    ): Disposable {
        val disposable = observe(resolver, callback)
        val value = try {
            evaluate(resolver)
        } catch (e: ParsingException) {
            logger.logError(e)
            null
        }

        value?.let { callback(it) }
        return disposable
    }

    override fun hashCode(): Int {
        return expressions.hashCode() * 16
    }

    override fun equals(other: Any?): Boolean {
        return other is MutableExpressionList<*> && expressions == other.expressions
    }
}
