package com.yandex.div.internal.variables

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.data.PropertyDelegate
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.internal.expressions.DivExpressionParser.readTypedExpression
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVariable
import com.yandex.div2.PropertyVariable

@InternalApi
fun DivVariable.toVariable(
    resolver: ExpressionResolver,
    propertyVariableExecutor: PropertyVariableExecutor,
    logger: ParsingErrorLogger,
): Variable? {
    return when (this) {
        is DivVariable.Bool ->
            Variable.BooleanVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Integer ->
            Variable.IntegerVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Number ->
            Variable.DoubleVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Str ->
            Variable.StringVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Color ->
            Variable.ColorVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Url ->
            Variable.UrlVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Dict ->
            Variable.DictVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Array ->
            Variable.ArrayVariable(value.name, value.value.evaluate(resolver))

        is DivVariable.Property ->
            value.toVariable(resolver, propertyVariableExecutor, logger)
    }
}

@InternalApi
fun PropertyVariable.parseGet(
    resolver: ExpressionResolver,
    logger: ParsingErrorLogger
): Expression<*>? {
    val expression = try {
        readTypedExpression(get.rawValue as String, PROPERTY_VARIABLE_GET_KEY, valueType, logger)
    } catch (e: ParsingException) {
        logger.logError(e)
        return null
    }

    if (expression.hasCycle(resolver, name)) {
        logger.logError(
            IllegalArgumentException(
                "Property variable '$name' has cycle in '$PROPERTY_VARIABLE_GET_KEY' expression."
            )
        )
        return null
    }
    return expression
}

@InternalApi
val DivVariable.name: String
    get() {
        return when (this) {
            is DivVariable.Bool -> this.value.name
            is DivVariable.Integer -> this.value.name
            is DivVariable.Number -> this.value.name
            is DivVariable.Str -> this.value.name
            is DivVariable.Color -> this.value.name
            is DivVariable.Url -> this.value.name
            is DivVariable.Dict -> this.value.name
            is DivVariable.Array -> this.value.name
            is DivVariable.Property -> this.value.name
        }
    }

/**
 * Converts [DivVariable] value to the value that can be used in
 * [com.yandex.div.evaluable.VariableProvider].
 */
@InternalApi
fun Any?.variableValueToEvaluableValue(): Any? {
    return when(this) {
        is Uri -> Url(toString())
        else -> this
    }
}

private fun PropertyVariable.toVariable(
    resolver: ExpressionResolver,
    propertyVariableExecutor: PropertyVariableExecutor,
    logger: ParsingErrorLogger
): Variable.PropertyVariable? {
    val getExpression = parseGet(resolver, logger) ?: return null
    val delegate = PropertyDelegate(
        name,
        valueType,
        getExpression,
        set,
        newValueVariableName,
        propertyVariableExecutor
    )
    return Variable.PropertyVariable(name, valueType, delegate)
}

private fun Expression<*>.hasCycle(resolver: ExpressionResolver, propertyName: String): Boolean {
    val mutableExpression = this as? Expression.MutableExpression<*, *> ?: return false

    val variableNames = mutableExpression.getVariablesName(resolver)
    if (variableNames.contains(propertyName)) return true

    variableNames.forEach {
        val variable = resolver.getVariable(it) as? Variable.PropertyVariable ?: return@forEach
        if (variable.getExpression.hasCycle(resolver, propertyName)) return true
    }
    return false
}

private const val PROPERTY_VARIABLE_GET_KEY = "get"
