package com.yandex.div.core.type

import com.yandex.div.core.json.asJsonObject
import org.json.JSONObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DataObjectTest {

    @Test
    fun `get returns nested JSON object as is`() {
        val nested = JSONObject().asJsonObject()

        val obj = DataObject.of(mapOf("nested" to nested))

        assertSame(nested, obj.get("nested"))
    }

    @Test
    fun `get returns nested data array as is`() {
        val nested = DataArray.of(emptyList())

        val obj = DataObject.of(mapOf("nested" to nested))

        assertSame(nested, obj.get("nested"))
    }

    @Test
    fun `get returns int when long fits int`() {
        val obj = DataObject.of(mapOf("number" to 65536L))

        assertEquals(65536, obj.get("number"))
    }

    @Test
    fun `get returns long when long does not fit int`() {
        val obj = DataObject.of(mapOf("number" to 1099511627776L))

        assertEquals(1099511627776L, obj.get("number"))
    }

    @Test
    fun `get returns double with decimal value of float`() {
        val obj = DataObject.of(mapOf("number" to 0.1f))

        assertEquals(0.1, obj.get("number"))
    }

    @Test
    fun `of throws when value has unsupported type`() {
        assertFailsWith<IllegalArgumentException> {
            DataObject.of(mapOf("list" to listOf("value")))
        }
    }

    @Test
    fun `of throws when number is not finite`() {
        assertFailsWith<IllegalArgumentException> {
            DataObject.of(mapOf("number" to Double.NaN))
        }
    }

    @Test
    fun `containsKey returns true when value is null`() {
        val obj = DataObject.of(mapOf("null" to null))

        assertTrue(obj.containsKey("null"))
    }

    @Test
    fun `get throws when value is null`() {
        val obj = DataObject.of(mapOf("null" to null))

        assertFailsWith<NoSuchElementException> { obj.get("null") }
    }

    @Test
    fun `get throws when key is missing`() {
        val obj = DataObject.of(emptyMap())

        assertFailsWith<NoSuchElementException> { obj.get("missing") }
    }

    @Test
    fun `get returns original value when source map changes`() {
        val entries = mutableMapOf<String, Any?>("key" to "value")
        val obj = DataObject.of(entries)

        entries["key"] = "changed"

        assertEquals("value", obj.get("key"))
    }

    @Test
    fun `iterates entries in insertion order`() {
        val obj = DataObject.of(mapOf("b" to 1, "a" to 2))

        assertEquals(listOf("b" to 1, "a" to 2), obj.toList())
    }

    @Test
    fun `toString prints JSON`() {
        val obj = DataObject.of(
            mapOf(
                "string" to "quote \"",
                "number" to 1.5,
                "null" to null,
                "json" to JSONObject("""{"key":"value"}""").asJsonObject(),
                "data" to DataArray.of(listOf(true)),
            )
        )

        assertEquals(
            """{"string":"quote \"","number":1.5,"null":null,"json":{"key":"value"},"data":[true]}""",
            obj.toString()
        )
    }

    @Test
    fun `does not equal object with the same entries`() {
        val entries = mapOf("key" to "value")

        assertNotEquals(DataObject.of(entries), DataObject.of(entries))
    }
}
