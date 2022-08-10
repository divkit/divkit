package com.yandex.div.json.expressions

import com.yandex.div.core.CompositeDisposable
import com.yandex.div.core.Disposable
import com.yandex.div.json.ListValidator
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.invalidValue

/**
 * A container for collection of [Expression]s with validation of evaluated values.
 */
interface ExpressionsList<T : Any> {
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

class ConstantExpressionsList<T : Any>(
    private val valuesList: List<T>,
) : ExpressionsList<T> {

    override fun evaluate(resolver: ExpressionResolver): List<T> {
        return valuesList
    }

    override fun observe(resolver: ExpressionResolver, callback: (List<T>) -> Unit): Disposable {
        return Disposable.NULL
    }

    override fun observeAndGet(
        resolver: ExpressionResolver,
        callback: (List<T>) -> Unit
    ): Disposable {
        callback(valuesList)
        return Disposable.NULL
    }

    override fun equals(other: Any?): Boolean {
        return other is ConstantExpressionsList<*> && valuesList == other.valuesList
    }
}

internal class MutableExpressionsList<T : Any>(
    private val key: String,
    internal val expressionsList: List<Expression<T>>,
    private val listValidator: ListValidator<T>,
    private val logger: ParsingErrorLogger,
) : ExpressionsList<T> {

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
        val valuesList = expressionsList.map { it.evaluate(resolver) }
        if (!listValidator.isValid(valuesList)) {
            throw invalidValue(key, valuesList)
        }
        return valuesList
    }

    override fun observe(resolver: ExpressionResolver, callback: (List<T>) -> Unit): Disposable {
        val itemCallback = { _: T -> callback(evaluate(resolver)) }

        if (expressionsList.size == 1) {
            return expressionsList.first().observe(resolver, itemCallback)
        }

        val disposable = CompositeDisposable()
        expressionsList.forEach {
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

    override fun equals(other: Any?): Boolean {
        return other is MutableExpressionsList<*> && expressionsList == other.expressionsList
    }
}
