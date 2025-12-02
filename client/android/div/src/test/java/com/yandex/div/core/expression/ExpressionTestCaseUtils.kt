package com.yandex.div.core.expression

import android.net.Uri
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.interactive.IntegrationTestCase
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.util.map
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.test.expression.MultiplatformTestUtils.isForAndroidPlatform
import com.yandex.div.test.expression.MultiplatformTestUtils.parsePlatform
import com.yandex.div.test.expression.MultiplatformTestUtils.toListOfJSONObject
import com.yandex.div.test.expression.TestCaseOrError
import com.yandex.div.test.expression.TestCaseParsingError
import com.yandex.div.test.expression.parseAsUTC
import com.yandex.div2.DivData
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivFunction
import com.yandex.div2.DivVariable
import org.json.JSONArray
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
    const val VALUE_TYPE_DICT = "dict"
    const val VALUE_TYPE_ARRAY = "array"
    const val VALUE_TYPE_UNORDERED_ARRAY = "unordered_array"
    private const val VALUE_TYPE_UNIT = "unit"
    private const val VALUE_TYPE_ERROR = "error"
    private const val VALUE_TYPE_VARIABLE = "variable"

    private const val CASES_FIELD = "cases"
    private const val CASE_VARIABLES_FIELD = "variables"
    private const val CASE_FUNCTIONS_FIELD = "functions"
    private const val CASE_VARIABLE_NAME_FIELD = "variable_name"
    private const val CASE_EXPECTED_VALUE_FIELD = "expected"
    private const val CASE_EXPECTED_WARNINGS_FIELD = "expected_warnings"
    private const val CASE_EXPRESSION_VALUE_FIELD = "expression"
    private const val TYPE_FIELD = "type"
    private const val VALUE_FIELD = "value"

    fun parseTestCases(json: JSONObject, fileName: String): List<TestCaseOrError<ExpressionTestCase>> {
        return json.optJSONArray(CASES_FIELD)
            .toListOfJSONObject()
            .filter { isForAndroidPlatform(parsePlatform(it)) }
            .map { parseTestCase(it, fileName) }
    }

    private fun parseTestCase(json: JSONObject, fileName: String): TestCaseOrError<ExpressionTestCase> {
        try {
            val testCase = ExpressionTestCase(
                fileName,
                json.getString(CASE_EXPRESSION_VALUE_FIELD),
                json.optJSONArray(CASE_VARIABLES_FIELD).toListOfJSONObject(),
                json.optJSONArray(CASE_FUNCTIONS_FIELD).toListOfJSONObject(),
                parsePlatform(json),
                json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).type,
                json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).getValue(),
                json.optJSONArray(CASE_EXPECTED_WARNINGS_FIELD)?.map { it as String } ?: emptyList(),
            )

            return TestCaseOrError(testCase)
        } catch (e: JSONException) {
            return TestCaseOrError(TestCaseParsingError(fileName, json, e))
        }
    }

    fun checkDuplicates(cases: Sequence<TestCaseOrError<ExpressionTestCase>>) {
        val duplicate = cases.mapNotNull { it.testCase }
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
            VALUE_TYPE_DATE_TIME -> parseAsUTC(getString(VALUE_FIELD))
            VALUE_TYPE_UNIT -> Unit
            VALUE_TYPE_ERROR -> EvaluableException(optString(VALUE_FIELD))
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
        return value
    }

    val JSONObject.type: String get() = getString(TYPE_FIELD)

    fun JSONObject.getVariableValue(type: String): Any {
        return when (type) {
            VALUE_TYPE_STRING -> getString(VALUE_FIELD)
            VALUE_TYPE_INTEGER -> getLong(VALUE_FIELD)
            VALUE_TYPE_DECIMAL ->getDouble(VALUE_FIELD)
            VALUE_TYPE_BOOLEAN -> get(VALUE_FIELD)
            VALUE_TYPE_COLOR -> Color.parse(getString(VALUE_FIELD))
            VALUE_TYPE_URL -> Uri.parse(getString(VALUE_FIELD))
            VALUE_TYPE_DICT -> getJSONObject(VALUE_FIELD)
            VALUE_TYPE_ARRAY -> getJSONArray(VALUE_FIELD)
            VALUE_TYPE_DATE_TIME -> parseAsUTC(getString(VALUE_FIELD))
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
    }

    fun createVariable(type: String, name: String, value: Any?): Variable {
        return when (type) {
            VALUE_TYPE_STRING -> Variable.StringVariable(name, value as String? ?: "")
            VALUE_TYPE_INTEGER -> Variable.IntegerVariable(name, value as Long? ?: 0)
            VALUE_TYPE_DECIMAL -> Variable.DoubleVariable(name, value as Double? ?: 0.0)
            VALUE_TYPE_BOOLEAN -> Variable.BooleanVariable(name, ANY_TO_BOOLEAN(value ?: false))
            VALUE_TYPE_COLOR -> Variable.ColorVariable(name, (value as Color?)?.value ?: 0)
            VALUE_TYPE_URL -> Variable.UrlVariable(name, value as Uri? ?: Uri.EMPTY)
            VALUE_TYPE_DICT -> Variable.DictVariable(name, value as JSONObject? ?: JSONObject())
            VALUE_TYPE_ARRAY -> Variable.ArrayVariable(name, value as JSONArray? ?: JSONArray())
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
    }

    fun parseIntegrationTestCase(fileName: String, jsonString: String): List<TestCaseOrError<IntegrationTestCase>> {
        val json = JSONObject(jsonString)
        return json.getJSONArray(CASES_FIELD).toListOfJSONObject().mapIndexedNotNull { index, jsonObject ->
            try {
                val data = JSONObject(jsonString).getJSONObject("div_data")
                jsonObject.parseStep(fileName, data, index)?.let { TestCaseOrError(it) }
            } catch (e: JSONException) {
                TestCaseOrError(TestCaseParsingError(fileName, json, e))
            }
        }
    }

    private fun JSONObject.parseStep(
        fileName: String,
        data: JSONObject,
        index: Int,
    ): IntegrationTestCase? {
        if (!isForAndroidPlatform(parsePlatform(this))) return null

        val actions = optJSONArray("div_actions")?.toListOfJSONObject()

        var name = "$fileName Case $index"
        actions?.forEach {
            name += ", ${it.getString("log_id")}"
        }

        val expected = getJSONArray(CASE_EXPECTED_VALUE_FIELD).toListOfJSONObject().map {
            when (val type = it.type) {
                VALUE_TYPE_VARIABLE -> {
                    IntegrationTestCase.ExpectedResult.Variable(
                        it.getString(CASE_VARIABLE_NAME_FIELD),
                        it.getJSONObject(VALUE_FIELD)
                    )
                }
                VALUE_TYPE_ERROR -> IntegrationTestCase.ExpectedResult.Error(it.getString(VALUE_FIELD))
                else -> throw JSONException("Unknown expected result type: $type")
            }
        }

        return IntegrationTestCase(name, data, actions, expected)
    }

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
