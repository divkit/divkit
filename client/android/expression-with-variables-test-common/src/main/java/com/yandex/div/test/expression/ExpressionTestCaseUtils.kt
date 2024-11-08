package com.yandex.div.test.expression

import android.net.Uri
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.util.map
import com.yandex.div.test.expression.MultiplatformTestUtils.parsePlatform
import com.yandex.div.test.expression.MultiplatformTestUtils.toListOfJSONObject
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
    private const val VALUE_TYPE_DICT = "dict"
    private const val VALUE_TYPE_ARRAY = "array"
    private const val VALUE_TYPE_UNIT = "unit"
    private const val VALUE_TYPE_ERROR = "error"

    private const val CASES_FIELD = "cases"
    private const val CASE_NAME_FIELD = "name"
    private const val CASE_VARIABLES_FIELD = "variables"
    private const val CASE_VARIABLE_NAME_FIELD = "name"
    private const val CASE_EXPECTED_VALUE_FIELD = "expected"
    private const val CASE_EXPECTED_WARNINGS_FIELD = "expected_warnings"
    private const val CASE_EXPRESSION_VALUE_FIELD = "expression"
    private const val TYPE_FIELD = "type"
    private const val VALUE_FIELD = "value"

    fun parseTestCases(json: JSONObject, fileName: String): List<TestCaseOrError<ExpressionTestCase>> {
        return json.optJSONArray(CASES_FIELD)
            .toListOfJSONObject()
            .map { parseTestCase(it, fileName) }
    }

    private fun parseTestCase(json: JSONObject, fileName: String): TestCaseOrError<ExpressionTestCase> {
        val name = json.optString(CASE_NAME_FIELD)
        try {
            val testCase = ExpressionTestCase(
                fileName,
                name,
                json.getString(CASE_EXPRESSION_VALUE_FIELD),
                json.optJSONArray(CASE_VARIABLES_FIELD).toListOfJSONObject(),
                parsePlatform(json),
                json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).getString(TYPE_FIELD),
                json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).getValue(),
                json.optJSONArray(CASE_EXPECTED_WARNINGS_FIELD)?.map { it as String } ?: emptyList(),
            )

            return TestCaseOrError(testCase)
        } catch (e: JSONException) {
            return TestCaseOrError(TestCaseParsingError(name, fileName, json, e))
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
        val value: Any = when (val type = getString(TYPE_FIELD)) {
            VALUE_TYPE_STRING -> getString(VALUE_FIELD)
            VALUE_TYPE_URL -> Url.from(getString(VALUE_FIELD))
            VALUE_TYPE_COLOR -> Color.parse(getString(VALUE_FIELD))
            VALUE_TYPE_INTEGER -> getLong(VALUE_FIELD)
            VALUE_TYPE_DECIMAL -> getDouble(VALUE_FIELD)
            VALUE_TYPE_DICT -> getJSONObject(VALUE_FIELD)
            VALUE_TYPE_ARRAY -> getJSONArray(VALUE_FIELD)
            VALUE_TYPE_BOOLEAN, VALUE_TYPE_BOOL_INT -> {
                val value = get(VALUE_FIELD)
                ANY_TO_BOOLEAN(value) ?: throw IllegalAccessException("Unknown variable value: $value")
            }
            VALUE_TYPE_DATE_TIME -> parseAsUTC(getString(VALUE_FIELD))
            VALUE_TYPE_UNIT -> Unit
            VALUE_TYPE_ERROR -> EvaluableException(optString(VALUE_FIELD))
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
        return value
    }

    fun JSONObject.toVariable(): Variable {
        val name = getString(CASE_VARIABLE_NAME_FIELD)
        val type = getString(TYPE_FIELD)
        val value = when (type) {
            VALUE_TYPE_STRING -> getString(VALUE_FIELD)
            VALUE_TYPE_INTEGER -> getLong(VALUE_FIELD)
            VALUE_TYPE_DECIMAL ->getDouble(VALUE_FIELD)
            VALUE_TYPE_BOOLEAN -> get(VALUE_FIELD)
            VALUE_TYPE_COLOR -> getString(VALUE_FIELD)
            VALUE_TYPE_URL -> getString(VALUE_FIELD)
            VALUE_TYPE_DICT -> getJSONObject(VALUE_FIELD)
            VALUE_TYPE_ARRAY -> getJSONArray(VALUE_FIELD)
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
        return createVariable(type, name, value)
    }

    private fun createVariable(type: String, name: String, value: Any?): Variable {
        return when (type) {
            VALUE_TYPE_STRING -> Variable.StringVariable(name, value as String? ?: "")
            VALUE_TYPE_INTEGER -> Variable.IntegerVariable(name, value as Long? ?: 0)
            VALUE_TYPE_DECIMAL -> Variable.DoubleVariable(name, value as Double? ?: 0.0)
            VALUE_TYPE_BOOLEAN -> {
                ANY_TO_BOOLEAN(value ?: false)?.let { Variable.BooleanVariable(name, it) }
                    ?: throw IllegalAccessException("Unknown variable value: $value")
            }
            VALUE_TYPE_COLOR -> Variable.ColorVariable(name, Color.parse(value as String? ?: "#000000").value)
            VALUE_TYPE_URL -> Variable.UrlVariable(name, Uri.parse(value as String?))
            VALUE_TYPE_DICT -> Variable.DictVariable(name, value as JSONObject? ?: JSONObject())
            VALUE_TYPE_ARRAY -> Variable.ArrayVariable(name, value as JSONArray? ?: JSONArray())
            else -> throw IllegalAccessException("Unknown variable type: $type")
        }
    }
}
