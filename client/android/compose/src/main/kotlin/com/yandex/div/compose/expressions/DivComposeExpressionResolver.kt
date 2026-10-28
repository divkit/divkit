package com.yandex.div.compose.expressions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.compose.variables.VariableAndConstantProvider
import com.yandex.div.core.Disposable
import com.yandex.div.core.ObserverList
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.internal.variables.DeclarationObserver
import com.yandex.div.internal.variables.VariableSource
import com.yandex.div.internal.variables.evaluableValueToFieldValue
import com.yandex.div.internal.variables.variableValueToEvaluableValue
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.invalidValue
import com.yandex.div.json.missingVariable
import com.yandex.div.json.resolveFailed
import com.yandex.div.json.typeMismatch
import javax.inject.Inject

@DivLocalScope
internal class DivComposeExpressionResolver private constructor(
    private val evaluationContext: EvaluationContext,
    private val reporter: DivReporter,
    private val variableProvider: VariableAndConstantProvider,
) : ExpressionResolver {

    @Inject
    constructor(
        functionProvider: FunctionProviderDecorator,
        reporter: DivReporter,
        storedValueProvider: ScopedStoredValueProvider,
        variableProvider: VariableAndConstantProvider,
    ) : this(
        evaluationContext = EvaluationContext(
            variableProvider = variableProvider,
            storedValueProvider = storedValueProvider,
            functionProvider = functionProvider,
            warningSender = EvaluatorWarningSender(reporter)
        ),
        reporter = reporter,
        variableProvider = variableProvider,
    )

    private val evaluator = Evaluator(evaluationContext)

    private val varToExpressions = mutableMapOf<String, MutableSet<String>>()
    private val expressionObservers = mutableMapOf<String, ObserverList<() -> Unit>>()
    private val variableObserver: (Variable) -> Unit = ::onVariableChanged
    private val declarationObserver = object : DeclarationObserver {
        override fun onDeclared(variable: Variable) {
            variable.addObserver(variableObserver)
            onVariableChanged(variable)
        }

        override fun onUndeclared(variable: Variable) {
            variable.removeObserver(variableObserver)
            onVariableChanged(variable)
        }
    }
    private var isObservingVariables = false
    private var providerSubscriptions: MutableSet<AutoCloseable>? = null
    private val variableSource: VariableSource
        get() = variableProvider.controller.variableSource

    /**
     * Creates a resolver exposing the assigned property value as `new_value` (or a custom name).
     * This value overrides the named variable during setter expression evaluation while keeping
     * the property's declaration scope.
     */
    fun withPropertyNewValueVariable(variableName: String, value: Any): DivComposeExpressionResolver {
        return DivComposeExpressionResolver(
            evaluationContext = evaluationContext.copy(
                variableProvider = { name ->
                    if (name == variableName) {
                        value.variableValueToEvaluableValue()
                    } else {
                        evaluationContext.variableProvider.get(name)
                    }
                },
            ),
            reporter = reporter,
            variableProvider = variableProvider,
        )
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
            reporter.reportError(e)
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
        }

        if (!isObservingVariables && variableNames.any { !variableProvider.isConstant(it) }) {
            isObservingVariables = true
            variableSource.observeVariables(variableObserver)
            variableSource.observeDeclaration(declarationObserver)
        }

        expressionObservers
            .getOrPut(rawExpression) { ObserverList() }
            .addObserver(callback)

        val providerSubscription = variableProvider.subscribe(variableNames, callback)
        if (providerSubscription != null) {
            if (providerSubscriptions == null) providerSubscriptions = mutableSetOf()
            providerSubscriptions?.add(providerSubscription)
        }
        return Disposable {
            expressionObservers[rawExpression]?.removeObserver(callback)
            if (providerSubscription != null) {
                providerSubscription.close()
                providerSubscriptions?.remove(providerSubscription)
            }
        }
    }

    override fun getVariable(name: String): Variable? = variableProvider.getVariable(name)

    fun clearObservers() {
        providerSubscriptions?.forEach { it.close() }
        providerSubscriptions?.clear()
        if (isObservingVariables) {
            variableSource.removeVariablesObserver(variableObserver)
            variableSource.removeDeclarationObserver(declarationObserver)
            isObservingVariables = false
        }
        expressionObservers.clear()
        varToExpressions.clear()
    }

    private fun onVariableChanged(variable: Variable) {
        if (variableProvider.isConstant(variable.name)) return
        varToExpressions[variable.name]?.forEach { expression ->
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
            getEvaluationResult<Any>(rawExpression, evaluable).evaluableValueToFieldValue(fieldType) as R
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
        val value = evaluator.eval<R>(evaluable)
        if (evaluable.checkIsCacheable()) {
            evaluable.variables.forEach {
                val expressions = varToExpressions.getOrPut(it) { mutableSetOf() }
                expressions.add(rawExpression)
            }
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
