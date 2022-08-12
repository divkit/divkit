package com.yandex.div.core.expression

import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.expression.variables.subscribeToVariable
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.json.Converter
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.ParsingExceptionReason
import com.yandex.div.json.TypeHelper
import com.yandex.div.json.ValueValidator
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.invalidValue
import com.yandex.div.json.missingVariable
import com.yandex.div.json.resolveFailed
import com.yandex.div.json.typeMismatch

internal class ExpressionResolverImpl(
    private val variableController: VariableController,
    evaluatorFactory: ExpressionEvaluatorFactory,
    private val errorCollector: ErrorCollector,
) : ExpressionResolver {

    private val evaluator = evaluatorFactory.create { variableName ->
        variableController.getMutableVariable(variableName)?.getValue()
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
            evaluator.eval<R>(evaluable)
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
            safeConvert(expressionKey, rawExpression, converter, result)
                    ?: throw invalidValue(expressionKey, rawExpression, result)
        }

        safeValidate(expressionKey, rawExpression, validator, convertedValue)
        return convertedValue
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
        rawValue: R
    ): T? {
        // cast is unsafe but we hope that safeValidate() will check this type later
        val c = converter ?: return rawValue as? T
        try {
            // We need to guard with try..catch because at this point we're
            // not sure that resolved expression type is same type that converter accepts.
            return c.invoke(rawValue)
        } catch (e: ClassCastException) {
            throw typeMismatch(
                expressionKey = expressionKey,
                rawExpression = rawExpression,
                wrongTypeValue = rawValue,
                cause = e,
            )
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


    override fun <T> onChange(variableName: String, callback: (T?) -> Unit) =
        subscribeToVariable<T>(
            variableName,
            errorCollector,
            variableController,
            invokeChangeOnSubscription = false,
            callback
        )

    override fun notifyResolveFailed(e: ParsingException) {
        errorCollector.logError(e)
    }
}
