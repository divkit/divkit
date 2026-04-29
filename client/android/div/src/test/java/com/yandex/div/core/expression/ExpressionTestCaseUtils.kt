package com.yandex.div.core.expression

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.util.map
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.isForAndroid
import com.yandex.div.test.crossplatform.parseDateTime
import com.yandex.div.test.crossplatform.toObjectList
import com.yandex.div2.DivData
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivFunction
import com.yandex.div2.DivVariable
import org.json.JSONException
import org.json.JSONObject

object ExpressionTestCaseUtils {

    private const val VALUE_TYPE_STRING = "string"
    private const val VALUE_TYPE_INTEGER = "integer"
    private const val VALUE_TYPE_DECIMAL = "number"
    private const val VALUE_TYPE_BOOLEAN = "boolean"
    private const val VALUE_TYPE_BOOL_INT = "bool_int"
    private const val VALUE_TYPE_DATE_TIME = "datetime"
    private const val VALUE_TYPE_URL = "url"
    private const val VALUE_TYPE_COLOR = "color"
    private const val VALUE_TYPE_DICT = "dict"
    private const val VALUE_TYPE_ARRAY = "array"
    const val VALUE_TYPE_UNORDERED_ARRAY = "unordered_array"
    private const val VALUE_TYPE_UNIT = "unit"
    private const val VALUE_TYPE_ERROR = "error"

    private const val CASES_FIELD = "cases"
    private const val CASE_VARIABLES_FIELD = "variables"
    private const val CASE_FUNCTIONS_FIELD = "functions"
    private const val CASE_EXPECTED_VALUE_FIELD = "expected"
    private const val CASE_EXPECTED_WARNINGS_FIELD = "expected_warnings"
    private const val CASE_EXPRESSION_VALUE_FIELD = "expression"
    private const val TYPE_FIELD = "type"
    private const val VALUE_FIELD = "value"

    fun parseTestCases(
        json: JSONObject,
        fileName: String
    ): List<ParsingResult<ExpressionTestCase>> {
        return json.optJSONArray(CASES_FIELD)
            .toObjectList()
            .filter { it.isForAndroid }
            .map { parseTestCase(it, fileName) }
    }

    private fun parseTestCase(
        json: JSONObject,
        fileName: String
    ): ParsingResult<ExpressionTestCase> {
        try {
            val testCase = ExpressionTestCase(
                fileName,
                json.getString(CASE_EXPRESSION_VALUE_FIELD),
                json.optJSONArray(CASE_VARIABLES_FIELD).toObjectList(),
                json.optJSONArray(CASE_FUNCTIONS_FIELD).toObjectList(),
                json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).type,
                json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).getValue(),
                json.optJSONArray(CASE_EXPECTED_WARNINGS_FIELD)?.map { it as String } ?: emptyList(),
            )
            return ParsingResult.Success(testCase)
        } catch (e: JSONException) {
            return ParsingResult.Error(fileName, json, e)
        }
    }

    fun checkDuplicates(cases: Sequence<ParsingResult<ExpressionTestCase>>) {
        val duplicate = cases
            .filterIsInstance<ParsingResult.Success<ExpressionTestCase>>()
            .map { it.value }
            .groupingBy { it.fileName to it.description }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        assert(duplicate.isEmpty()) {
            duplicate.joinToString("\n", prefix = "Duplicate test case names:\n") {
                it.toList().joinToString("::")
            }
        }
    }

    private fun JSONObject.getValue(): Any {
        val value: Any = when (type) {
            VALUE_TYPE_STRING -> getString(VALUE_FIELD)
            VALUE_TYPE_URL -> Url(getString(VALUE_FIELD))
            VALUE_TYPE_COLOR -> Color.parse(getString(VALUE_FIELD))
            VALUE_TYPE_INTEGER -> getLong(VALUE_FIELD)
            VALUE_TYPE_DECIMAL -> getDouble(VALUE_FIELD)
            VALUE_TYPE_DICT -> getJSONObject(VALUE_FIELD)
            VALUE_TYPE_ARRAY -> getJSONArray(VALUE_FIELD)
            VALUE_TYPE_UNORDERED_ARRAY -> getJSONArray(VALUE_FIELD)
            VALUE_TYPE_BOOLEAN, VALUE_TYPE_BOOL_INT -> ANY_TO_BOOLEAN(get(VALUE_FIELD))
            VALUE_TYPE_DATE_TIME -> parseDateTime(getString(VALUE_FIELD))
            VALUE_TYPE_UNIT -> Unit
            VALUE_TYPE_ERROR -> EvaluableException(optString(VALUE_FIELD))
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
        return value
    }

    private val JSONObject.type: String get() = getString(TYPE_FIELD)

    fun createDivDataFromTestVars(
        vars: List<JSONObject>,
        functions: List<JSONObject>,
        logger: ParsingErrorLogger
    ): DivData {
        val env = DivParsingEnvironment(logger)
        val divVars: List<DivVariable> = vars.map { json -> DivVariable(env, json) }
        val divFunctions = functions.map { DivFunction(env, it) }
        return DivData(
            logId = "testLogId",
            states = emptyList(),
            variables = divVars,
            functions = divFunctions,
        )
    }

    fun String.toEvaluableType() : DivEvaluableType {
        return DivEvaluableType.fromString(this) ?: when(this) {
            VALUE_TYPE_BOOL_INT -> DivEvaluableType.BOOLEAN
            VALUE_TYPE_UNORDERED_ARRAY -> DivEvaluableType.ARRAY
            VALUE_TYPE_ERROR -> DivEvaluableType.STRING
            else -> throw JSONException("Unknown expected result type: $this")
        }
    }
}
