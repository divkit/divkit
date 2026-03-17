package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.expression.asImpl
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.internal.variables.toVariable
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
        val executor = resolver.asImpl?.let {
            PropertyVariableExecutorImpl(it) { null }
        } ?: PropertyVariableExecutor.STUB
        return divVariables.mapNotNull { it.toVariable(resolver, executor, logger) }
    }
}
