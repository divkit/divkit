package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.expression.DivExpressionParser.readTypedExpression
import com.yandex.div.core.expression.local.asImpl
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.internal.data.PropertyDelegate
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivVariable
import com.yandex.div2.PropertyVariable
import org.json.JSONArray
import org.json.JSONObject

@PublicApi
object DivVariablesParser {
    /**
     * @param variablesArray json-array of variables for parsing.
     */
    @Throws(ParsingException::class)
    fun parse(variablesArray: JSONArray, logger: ParsingErrorLogger) =
        parse(variablesArray, ExpressionResolver.EMPTY, logger)

    /**
     * @param variablesArray json-array of variables for parsing.
     */
    @Throws(ParsingException::class)
    fun parse(variablesArray: JSONArray, resolver: ExpressionResolver, logger: ParsingErrorLogger): List<Variable> {
        val env = DivParsingEnvironment(logger)
        val listValidator: (value: List<DivVariable>) -> Boolean = { true }
        val key = "variables"
        val jsonObject = JSONObject().apply {
            put(key, variablesArray)
        }
        val divVariables: List<DivVariable> = JsonParser.readList(
            jsonObject, key, DivVariable.CREATOR, listValidator, logger, env)
        val executor = resolver.asImpl?.let { PropertyVariableExecutorImpl(it) } ?: PropertyVariableExecutor.STUB
        return divVariables.mapNotNull { it.toVariable(resolver, executor, logger) }
    }
}

internal fun DivVariable.toVariable(
    resolver: ExpressionResolver,
    propertyVariableExecutor: PropertyVariableExecutor,
    logger: ParsingErrorLogger,
): Variable? {
    return when (this) {
        is DivVariable.Bool -> {
            Variable.BooleanVariable(
                this.value.name, this.value.value.evaluate(resolver))
        }
        is DivVariable.Integer -> {
            Variable.IntegerVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Number -> {
            Variable.DoubleVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Str -> {
            Variable.StringVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Color -> {
            Variable.ColorVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Url -> {
            Variable.UrlVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Dict -> {
            Variable.DictVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Array -> {
            Variable.ArrayVariable(
                this.value.name, this.value.value.evaluate(resolver)
            )
        }
        is DivVariable.Property ->
            this.value.toVariable(resolver, propertyVariableExecutor, logger)
    }
}

internal fun PropertyVariable.toVariable(
    resolver: ExpressionResolver,
    propertyVariableExecutor: PropertyVariableExecutor,
    logger: ParsingErrorLogger
): Variable.PropertyVariable? {
    val getExpression = parseGet(resolver, logger) ?: return null
    val delegate = PropertyDelegate(name, valueType, getExpression, set, newValueVariableName, propertyVariableExecutor)
    return Variable.PropertyVariable(name, valueType, delegate)
}

internal const val PROPERTY_VARIABLE_GET_KEY = "get"

internal fun PropertyVariable.parseGet(resolver: ExpressionResolver, logger: ParsingErrorLogger) =
    parseGet(get.rawValue as String, name, valueType, resolver, logger)

internal fun parseGet(
    raw: String,
    variableName: String,
    valueType: DivEvaluableType,
    resolver: ExpressionResolver,
    logger: ParsingErrorLogger,
): Expression<*>? {
    val expression = try {
        readTypedExpression(raw, PROPERTY_VARIABLE_GET_KEY, valueType, logger)
    } catch (e: ParsingException) {
        logger.logError(e)
        return null
    }

    if (expression.hasCycle(resolver, variableName)) {
        logger.logError(IllegalArgumentException(
            "Property variable '$variableName' has cycle in '$PROPERTY_VARIABLE_GET_KEY' expression."
        ))
        return null
    }
    return expression
}

private fun Expression<*>.hasCycle(resolver: ExpressionResolver, propertyName: String): Boolean {
    val mutableExpression = this as? Expression.MutableExpression<*, *> ?: return false

    val variableNames = mutableExpression.getVariablesName(resolver)
    if (variableNames.contains(propertyName)) return true

    val variableController = resolver.asImpl?.variableController ?: return false
    variableNames.forEach {
        val variable = variableController.getMutableVariable(it) as? Variable.PropertyVariable ?: return@forEach
        if (variable.getExpression.hasCycle(resolver, propertyName)) return true
    }
    return false
}
