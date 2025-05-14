package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVariable
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
        return divVariables.map { it.toVariable(resolver) }
    }
}

internal fun DivVariable.toVariable(resolver: ExpressionResolver): Variable {
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
    }
}
