package com.yandex.div.core.expression.variables

import com.yandex.div.data.Variable
import com.yandex.div.evaluable.types.Color
import org.json.JSONArray
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivVariablesParserTest {
    private val json = """
            [
                {
                    "name": "logic_var",
                    "type": "boolean",
                    "value": 1
                },
                {
                    "name": "int_var",
                    "type": "integer",
                    "value": 100
                },
                {
                    "name": "black",
                    "type": "color",
                    "value": "#f000"
                },
                {
                    "name": "string_var",
                    "type": "string",
                    "value": "???"
                }
            ]
            """.trimIndent()
    private val logger: (e: Exception) -> Unit = { e -> throw e }

    @Test
    fun `all types of variables parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        Assert.assertEquals(4, variables.size)
    }

    @Test
    fun `logic variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val colorVariable = variables[0] as Variable.BooleanVariable
        Assert.assertTrue(colorVariable.getValue() as Boolean)
    }

    @Test
    fun `integer variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val variable = variables[1] as Variable.IntegerVariable
        Assert.assertEquals(100, variable.getValue() as Int)
    }

    @Test
    fun `color variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val variable = variables[2] as Variable.ColorVariable
        Assert.assertEquals(Color.rgb(0, 0, 0), variable.getValue())
    }

    @Test
    fun `string variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val stringVariable = variables[3] as Variable.StringVariable
        Assert.assertEquals("???", stringVariable.getValue())
    }
}
