package com.yandex.div.compose.expressions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.internal.variables.variableValueToEvaluableValue
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.invalidValue
import com.yandex.div.json.missingVariable
import com.yandex.div.json.resolveFailed
import com.yandex.div.json.typeMismatch
import javax.inject.Inject

@DivLocalScope
internal class DivComposeExpressionResolver @Inject constructor(
    private val reporter: DivReporter,
    private val variableController: DivVariableController
) : ExpressionResolver {

    private val evaluator: Evaluator

    private val evaluationsCache = mutableMapOf<String, Any>()
    private val varToExpressions = mutableMapOf<String, MutableSet<String>>()
    private val expressionObservers = mutableMapOf<String, ObserverList<() -> Unit>>()

    init {
        val evaluationContext = EvaluationContext(
            variableProvider = { name ->
                variableController.get(name)?.getValue().variableValueToEvaluableValue()
            },
            storedValueProvider = { _ -> null },
            functionProvider = GeneratedBuiltinFunctionProvider,
            warningSender = EvaluatorWarningSender(reporter)
        )
        evaluator = Evaluator(evaluationContext)
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
        } catch (e: Exception) {
            reporter.reportError("Failed to resolve expression", e)
            throw e
        }
    }

    override fun subscribeToExpression(
        rawExpression: String,
        variableNames: List<String>,
        callback: () -> Unit
    ): Disposable {
        variableNames.forEach { variableName ->
            varToExpressions
                .getOrPut(variableName) { mutableSetOf() }
                .add(rawExpression)

            variableController.get(variableName)?.let { variable ->
                variable.removeObserver(::onVariableChanged)
                variable.addObserver(::onVariableChanged)
            }
        }

        expressionObservers
            .getOrPut(rawExpression) { ObserverList() }
            .addObserver(callback)

        return Disposable { expressionObservers[rawExpression]?.removeObserver(callback) }
    }

    override fun getVariable(name: String): Variable? {
        return variableController.get(name)
    }

    private fun onVariableChanged(variable: Variable) {
        varToExpressions[variable.name]?.forEach { expression ->
            evaluationsCache.remove(expression)
            expressionObservers[expression]?.forEach { observer ->
                observer()
            }
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
                throw missingVariable(rawExpression, variableName, e)
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
        val cachedValue = evaluationsCache[rawExpression]
        if (cachedValue != null) {
            return cachedValue as R
        }

        val value = evaluator.eval<R>(evaluable)
        if (evaluable.checkIsCacheable()) {
            evaluable.variables.forEach {
                val expressions = varToExpressions.getOrPut(it) { mutableSetOf() }
                expressions.add(rawExpression)
            }
            evaluationsCache[rawExpression] = value
        }
        return value
    }

    private fun tryGetMissingVariableName(e: EvaluableException): String? {
        return if (e is MissingVariableException) e.variableName else null
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
}
