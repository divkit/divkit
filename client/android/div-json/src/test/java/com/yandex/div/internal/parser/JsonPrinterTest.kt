package com.yandex.div.internal.parser

import com.yandex.div.internal.util.JsonPrinter
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonPrinterTest {

    @Test
    fun `print minified json`() {
        val json = JSONObject("""{"key": "value"}""")

        val printer = JsonPrinter(indentSpaces = 0, nestingLimit = Int.MAX_VALUE)

        assertEquals("""{"key":"value"}""", printer.print(json))
    }

    @Test
    fun `print json with 2 space indentation`() {
        val json = JSONObject("""{"key": "value"}""")

        val printer = JsonPrinter(indentSpaces = 2, nestingLimit = Int.MAX_VALUE)

        assertEquals(
            """
            |{
            |  "key": "value"
            |}""".trimMargin(),
            printer.print(json)
        )
    }

    @Test
    fun `print json with 4 space indentation`() {
        val json = JSONArray("""["value"]""")

        val printer = JsonPrinter(indentSpaces = 4, nestingLimit = Int.MAX_VALUE)

        assertEquals(
            """
            |[
            |    "value"
            |]""".trimMargin(),
            printer.print(json)
        )
    }

    @Test
    fun `nested object replaced with ellipsis`() {
        val json = JSONObject("""{"object": {"key": "value"}}""")

        val printer = JsonPrinter(indentSpaces = 0, nestingLimit = 0)

        assertEquals("""{"object":"..."}""", printer.print(json))
    }

    @Test
    fun `nested array replaced with ellipsis`() {
        val json = JSONArray("""[["item 1", "item 2", "item 3", "item 4"]]""")

        val printer = JsonPrinter(indentSpaces = 0, nestingLimit = 0)

        assertEquals("""["..."]""", printer.print(json))
    }

    @Test
    fun `objects replaced with ellipsis on second level of nesting`() {
        val json = JSONObject("""{"array": [{"key": "value"}, {"key": "value"}]}""")

        val printer = JsonPrinter(indentSpaces = 0, nestingLimit = 1)

        assertEquals("""{"array":["...","..."]}""", printer.print(json))
    }

    @Test
    fun `array replaced with ellipsis on second level of nesting`() {
        val json = JSONObject("""{"object": {"array": ["item 1", "item 2"]}}""")

        val printer = JsonPrinter(indentSpaces = 0, nestingLimit = 1)

        assertEquals("""{"object":{"array":"..."}}""", printer.print(json))
    }
}
