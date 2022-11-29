package com.yandex.div.internal.parser

import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.TemplateProvider
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonParserTest {
    private val env = object : ParsingEnvironment {
        override val templates: TemplateProvider<JsonTemplate<*>>
            get() = TemplateProvider.empty()
        override val logger: ParsingErrorLogger = mock()
    }

    @Test
    fun `test read and write mutable expressions list`() {
        val jsonObject = JSONObject(MUTABLE_EXPRESSIONS_JSON)
        val listValueValidator = ListValidator<Int> { true }
        val expressionsList = JsonParser.readExpressionList(
            jsonObject, "expressions", STRING_TO_COLOR_INT, listValueValidator, env.logger, env, TYPE_HELPER_COLOR
        )
        jsonObject.writeExpressionList("result", expressionsList, COLOR_INT_TO_STRING)
        assertEqualJSONs(jsonObject.get("expressions").toString(), jsonObject.get("result").toString())
    }

    @Test
    fun `test read and write constant expressions list`() {
        val jsonObject = JSONObject(CONSTANT_EXPRESSIONS_JSON)
        val listValueValidator = ListValidator<Int> { true }
        val expressionsList = JsonParser.readExpressionList(
            jsonObject, "expressions", STRING_TO_COLOR_INT, listValueValidator, env.logger, env, TYPE_HELPER_COLOR
        )
        jsonObject.writeExpressionList("result", expressionsList, COLOR_INT_TO_STRING)
        assertEqualJSONs(jsonObject.get("expressions").toString(), jsonObject.get("result").toString())
    }

    private fun assertEqualJSONs(expected: String, actual: String) {
        assertEquals(
            expected.lowercase().replace("\\s".toRegex(), ""),
            actual.lowercase().replace("\\s".toRegex(), "")
        )
    }

    companion object {
        private const val MUTABLE_EXPRESSIONS_JSON =
            """
                {
                    "expressions": ["#ffff00ff", "@{is_incognito ? '#00000000' : top_part_background_color}"]
                }
            """

        private const val CONSTANT_EXPRESSIONS_JSON =
            """
                {
                    "expressions": ["#ff0000ff", "#ffff00aa"]
                }
            """

        private val COLOR_INT_TO_STRING: Converter<Int, String> = { value -> Color(value).toString() }
        private val STRING_TO_COLOR_INT: Converter<Any?, Int?> = { value ->
            when (value) {
                is String -> Color.parse(value).value
                else -> throw ClassCastException("Received value of wrong type")
            }
        }
        private val TYPE_HELPER_COLOR = object : TypeHelper<Int> {
            override val typeDefault = android.graphics.Color.BLACK
            override fun isTypeValid(value: Any) = value is Int
        }
    }
}
