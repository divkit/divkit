package com.yandex.div.core.expression

import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.ParsingExceptionReason
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.invalidValue
import com.yandex.div.json.missingVariable
import com.yandex.div.json.resolveFailed
import com.yandex.div.json.typeMismatch

internal class ExpressionResolverImpl(
    variableController: VariableController,
    private val evaluator: Evaluator,
    private val errorCollector: ErrorCollector,
) : ExpressionResolver {
    private val evaluationsCache = mutableMapOf<String, Any>()
    private val varToExpressions = mutableMapOf<String, MutableSet<String>>()

    private val expressionObservers = mutableMapOf<String, ObserverList<() -> Unit>>()

    init {
        variableController.setOnAnyVariableChangeCallback { v ->
            varToExpressions[v.name]?.forEach { expr ->
                evaluationsCache.remove(expr)
                expressionObservers[expr]?.forEach {
                    it.invoke()
                }
            }
        }
    }

    override fun <R, T : Any> get(
        expressionKey: String,
        rawExpression: String,
        evaluable: Evaluable,
        converter: Converter<R, T?>?,
        validator: ValueValidator<T>,
        fieldType: TypeHelper<T>,
        logger: ParsingErrorLogger
    ): T {
        return try {
            tryResolve(expressionKey, rawExpression, evaluable, converter, validator, fieldType)
        } catch (e: ParsingException) {
            if (e.reason == ParsingExceptionReason.MISSING_VARIABLE) {
                throw e
            }
            logger.logError(e)
            errorCollector.logError(e)
            tryResolve(expressionKey, rawExpression, evaluable, converter, validator, fieldType)
        }
    }

    private fun <R : Any, T : Any> tryResolve(
        expressionKey: String,
        rawExpression: String,
        evaluable: Evaluable,
        converter: Converter<R, T?>?,
        validator: ValueValidator<T>,
        fieldType: TypeHelper<T>,
    ): T {
        val result = try {
            getEvaluationResult<R>(rawExpression, evaluable)
        } catch (e: EvaluableException) {
            val variableName = tryGetMissingVariableName(e)

            if (variableName != null) {
                throw missingVariable(expressionKey, rawExpression, variableName, e)
            }
            throw resolveFailed(expressionKey, rawExpression, e)
        }

        val convertedValue = if (fieldType.isTypeValid(result)) {
            result as T
        } else {
            safeConvert(expressionKey, rawExpression, converter, result, fieldType)
                ?: throw invalidValue(expressionKey, rawExpression, result)
        }

        safeValidate(expressionKey, rawExpression, validator, convertedValue)

        return convertedValue
    }

    private fun <R : Any> getEvaluationResult(
        rawExpression: String,
        evaluable: Evaluable
    ): R {
        return evaluationsCache[rawExpression]?.let {
            it as R
        } ?: run {
            val evalResult = evaluator.eval<R>(evaluable)
            if (evaluable.checkIsCacheable()) {
                evaluable.variables.forEach { varName ->
                    val expressions = varToExpressions.getOrPut(varName) { mutableSetOf() }
                    expressions.add(rawExpression)
                }
                evaluationsCache[rawExpression] = evalResult
            }
            evalResult
        }
    }

    private fun tryGetMissingVariableName(e: EvaluableException): String? {
        if (e is MissingVariableException) {
            return e.variableName
        }

        return null
    }

    private fun <R, T> safeConvert(
        expressionKey: String,
        rawExpression: String,
        converter: Converter<R, T?>?,
        rawValue: R,
        fieldType: TypeHelper<T>
    ): T? {
        val convertedValue = if (converter == null) {
            // cast is unsafe but we hope that safeValidate() will check this type later
            rawValue as? T
        } else {
            try {
                // We need to guard with try..catch because at this point we're
                // not sure that resolved expression type is same type that converter accepts.
                converter.invoke(rawValue)
            } catch (e: ClassCastException) {
                throw typeMismatch(
                    expressionKey = expressionKey,
                    rawExpression = rawExpression,
                    wrongTypeValue = rawValue,
                    cause = e,
                )
            } catch (e: Exception) {
                // okay to catch exceptions here cause convertors may
                // throw different types of errors from common ClassCastException
                // up to specific NumberFormatException.
                throw invalidValue(
                    expressionKey = expressionKey,
                    rawExpression = rawExpression,
                    wrongValue = rawValue,
                    cause = e,
                )
            }
        }

        fun fieldAwaitsStringButValueNotConverted(value: T?): Boolean {
            if (value == null) {
                return false
            }
            return fieldType.typeDefault is String && !fieldType.isTypeValid(value)
        }

        return if (fieldAwaitsStringButValueNotConverted(convertedValue)) {
            convertedValue.toString() as T
        } else {
            convertedValue
        }
    }

    private fun <T> safeValidate(
        expressionKey: String,
        rawExpression: String,
        validator: ValueValidator<T>,
        convertedValue: T
    ) {
        try {
            // We need to guard with try..catch because at this point we're
            // not sure that resolved expression type is same type that validator checks.
            if (!validator.isValid(convertedValue)) {
                throw invalidValue(rawExpression, convertedValue)
            }
        } catch (e: ClassCastException) {
            throw typeMismatch(
                expressionKey = expressionKey,
                rawExpression = rawExpression,
                wrongTypeValue = convertedValue,
                cause = e,
            )
        }
    }

    override fun notifyResolveFailed(e: ParsingException) {
        errorCollector.logError(e)
    }

    override fun subscribeToExpression(
        rawExpression: String,
        variableNames: List<String>,
        callback: () -> Unit
    ): Disposable {
        variableNames.forEach {
            val expressions = varToExpressions.getOrPut(it) { mutableSetOf() }
            expressions.add(rawExpression)
        }
        val observers = expressionObservers.getOrPut(rawExpression) { ObserverList() }
        observers.addObserver(callback)
        return Disposable { expressionObservers[rawExpression]?.removeObserver(callback) }
    }
}
