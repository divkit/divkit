package com.yandex.div.core.type

import com.yandex.div.core.json.asJsonArray
import org.json.JSONArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class DataArrayTest {

    @Test
    fun `get returns nested JSON array as is`() {
        val nested = JSONArray().asJsonArray()

        val array = DataArray.of(listOf(nested))

        assertSame(nested, array.get(0))
    }

    @Test
    fun `get returns nested data object as is`() {
        val nested = DataObject.of(emptyMap())

        val array = DataArray.of(listOf(nested))

        assertSame(nested, array.get(0))
    }

    @Test
    fun `get returns int when long fits int`() {
        val array = DataArray.of(listOf(65536L))

        assertEquals(65536, array.get(0))
    }

    @Test
    fun `get returns long when long does not fit int`() {
        val array = DataArray.of(listOf(1099511627776L))

        assertEquals(1099511627776L, array.get(0))
    }

    @Test
    fun `get returns double with decimal value of float`() {
        val array = DataArray.of(listOf(0.1f))

        assertEquals(0.1, array.get(0))
    }

    @Test
    fun `of throws when value has unsupported type`() {
        assertFailsWith<IllegalArgumentException> {
            DataArray.of(listOf(mapOf("key" to "value")))
        }
    }

    @Test
    fun `of throws when number is not finite`() {
        assertFailsWith<IllegalArgumentException> {
            DataArray.of(listOf(Float.POSITIVE_INFINITY))
        }
    }

    @Test
    fun `size includes null values`() {
        val array = DataArray.of(listOf(null))

        assertEquals(1, array.size)
    }

    @Test
    fun `get throws when value is null`() {
        val array = DataArray.of(listOf(null))

        assertFailsWith<NoSuchElementException> { array.get(0) }
    }

    @Test
    fun `get throws when index is out of bounds`() {
        val array = DataArray.of(listOf("value"))

        assertFailsWith<NoSuchElementException> { array.get(1) }
    }

    @Test
    fun `opt returns null when index is out of bounds`() {
        val array = DataArray.of(listOf("value"))

        assertNull(array.opt(1))
    }

    @Test
    fun `opt returns null when index is negative`() {
        val array = DataArray.of(listOf("value"))

        assertNull(array.opt(-1))
    }

    @Test
    fun `indexOf returns index of value`() {
        val array = DataArray.of(listOf("first", "second"))

        assertEquals(1, array.indexOf("second"))
    }

    @Test
    fun `indexOf returns -1 when value is missing`() {
        val array = DataArray.of(listOf("value"))

        assertEquals(-1, array.indexOf("missing"))
    }

    @Test
    fun `get returns original value when source list changes`() {
        val values = mutableListOf<Any?>("value")
        val array = DataArray.of(values)

        values[0] = "changed"

        assertEquals("value", array.get(0))
    }

    @Test
    fun `toString prints JSON`() {
        val array = DataArray.of(
            listOf(
                "quote \"",
                1.5,
                null,
                JSONArray("""[1]""").asJsonArray(),
                DataObject.of(mapOf("key" to true)),
            )
        )

        assertEquals("""["quote \"",1.5,null,[1],{"key":true}]""", array.toString())
    }

    @Test
    fun `does not equal array with the same values`() {
        val values = listOf("value")

        assertNotEquals(DataArray.of(values), DataArray.of(values))
    }
}
