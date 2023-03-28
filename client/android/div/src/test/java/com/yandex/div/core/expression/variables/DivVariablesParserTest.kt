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
                    "name": "logic_int_var",
                    "type": "boolean",
                    "value": 1
                },
                {
                    "name": "logic_var",
                    "type": "boolean",
                    "value": true
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

        Assert.assertEquals(5, variables.size)
    }

    @Test
    fun `logic int variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val intLogicVariable = variables[0] as Variable.BooleanVariable
        Assert.assertTrue(intLogicVariable.getValue() as Boolean)
    }

    @Test
    fun `logic variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val logicVariable = variables[1] as Variable.BooleanVariable
        Assert.assertTrue(logicVariable.getValue() as Boolean)
    }

    @Test
    fun `integer variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val intVariable = variables[2] as Variable.IntegerVariable
        Assert.assertEquals(100, intVariable.getValue() as Long)
    }

    @Test
    fun `color variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val colorVariable = variables[3] as Variable.ColorVariable
        Assert.assertEquals(Color.rgb(0, 0, 0), colorVariable.getValue())
    }

    @Test
    fun `string variable variable parsed`() {
        val variables = DivVariablesParser.parse(JSONArray(json), logger)

        val stringVariable = variables[4] as Variable.StringVariable
        Assert.assertEquals("???", stringVariable.getValue())
    }
}
