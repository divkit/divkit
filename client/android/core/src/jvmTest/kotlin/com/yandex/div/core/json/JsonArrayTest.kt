package com.yandex.div.core.json

import kotlinx.serialization.json.jsonArray
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlinx.serialization.json.Json as KtxJson

@RunWith(Parameterized::class)
class JsonArrayTest(private val backend: JsonBackend) {

    private val jsonArray: JsonArray = createJsonArray()

    @Test
    fun `array get reads short integer number value`() {
        assertEquals(65536, jsonArray.get(0))
    }

    @Test
    fun `array opt reads short integer number value`() {
        assertEquals(65536, jsonArray.opt(0))
    }

    @Test
    fun `array get reads long integer number value`() {
        assertEquals(1099511627776L, jsonArray.get(1))
    }

    @Test
    fun `array opt reads long integer number value`() {
        assertEquals(1099511627776L, jsonArray.opt(1))
    }

    @Test
    fun `array get reads decimal number value`() {
        assertEquals(3.141592653589793, jsonArray.get(2))
    }

    @Test
    fun `array opt reads decimal number value`() {
        assertEquals(3.141592653589793, jsonArray.opt(2))
    }

    @Test
    fun `array get reads negative decimal number value`() {
        assertEquals(-3.141592653589793, jsonArray.get(3))
    }

    @Test
    fun `array opt reads negative decimal number value`() {
        assertEquals(-3.141592653589793, jsonArray.opt(3))
    }

    @Test
    fun `array get reads boolean value`() {
        assertEquals(true, jsonArray.get(4))
    }

    @Test
    fun `array opt reads boolean value`() {
        assertEquals(true, jsonArray.opt(4))
    }

    @Test
    fun `array get reads string value`() {
        assertEquals("Lorem ipsum", jsonArray.get(5))
    }

    @Test
    fun `array opt reads string value`() {
        assertEquals("Lorem ipsum", jsonArray.opt(5))
    }

    @Test
    fun `array get reads short integer string value`() {
        assertEquals("65536", jsonArray.get(6))
    }

    @Test
    fun `array opt reads short integer string value`() {
        assertEquals("65536", jsonArray.opt(6))
    }

    @Test
    fun `array get reads long integer string value`() {
        assertEquals("1099511627776", jsonArray.get(7))
    }

    @Test
    fun `array opt reads long integer string value`() {
        assertEquals("1099511627776", jsonArray.opt(7))
    }

    @Test
    fun `array get reads decimal string value`() {
        assertEquals("3.141592653589793", jsonArray.get(8))
    }

    @Test
    fun `array opt reads decimal string value`() {
        assertEquals("3.141592653589793", jsonArray.opt(8))
    }

    @Test
    fun `array get reads negative decimal string value`() {
        assertEquals("-3.141592653589793", jsonArray.get(9))
    }

    @Test
    fun `array opt reads negative decimal string value`() {
        assertEquals("-3.141592653589793", jsonArray.opt(9))
    }

    @Test
    fun `array get reads exponent decimal string value`() {
        assertEquals("6.5536e4", jsonArray.get(10))
    }

    @Test
    fun `array opt reads exponent decimal string value`() {
        assertEquals("6.5536e4", jsonArray.opt(10))
    }

    @Test
    fun `array get reads true string value`() {
        assertEquals("true", jsonArray.get(11))
    }

    @Test
    fun `array opt reads true string value`() {
        assertEquals("true", jsonArray.opt(11))
    }

    @Test
    fun `array get reads false string value`() {
        assertEquals("false", jsonArray.get(12))
    }

    @Test
    fun `array opt reads false string value`() {
        assertEquals("false", jsonArray.opt(12))
    }

    @Test
    fun `array get reads uppercase boolean string value`() {
        assertEquals("TRUE", jsonArray.get(13))
    }

    @Test
    fun `array opt reads uppercase boolean string value`() {
        assertEquals("TRUE", jsonArray.opt(13))
    }

    @Test
    fun `array get reads mixed case boolean string value`() {
        assertEquals("False", jsonArray.get(14))
    }

    @Test
    fun `array opt reads mixed case boolean string value`() {
        assertEquals("False", jsonArray.opt(14))
    }

    @Test
    fun `array get reads object value`() {
        assertEquals(mapOf("nested" to "value").toList(), (jsonArray.get(16) as JsonObject).toList())
    }

    @Test
    fun `array opt reads object value`() {
        assertEquals(mapOf("nested" to "value").toList(), (jsonArray.opt(16) as JsonObject).toList())
    }

    @Test
    fun `array get reads array value`() {
        assertEquals(listOf(65536), (jsonArray.get(17) as JsonArray).toList())
    }

    @Test
    fun `array opt reads array value`() {
        assertEquals(listOf(65536), (jsonArray.opt(17) as JsonArray).toList())
    }

    @Test
    fun `array get throws for json null`() {
        val exception = assertThrows(NoSuchElementException::class.java) {
            jsonArray.get(15)
        }
        assertEquals("Value at index 15 is missing or null", exception.message)
    }

    @Test
    fun `array opt returns null for json null`() {
        assertNull(jsonArray.opt(15))
    }

    @Test
    fun `array get throws for negative index`() {
        val exception = assertThrows(NoSuchElementException::class.java) {
            jsonArray.get(-1)
        }
        assertEquals("Value at index -1 is missing or null", exception.message)
    }

    @Test
    fun `array opt returns null for negative index`() {
        assertNull(jsonArray.opt(-1))
    }

    @Test
    fun `array get throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.get(jsonArray.size)
        }
    }

    @Test
    fun `array opt returns null for index equal to size`() {
        assertNull(jsonArray.opt(jsonArray.size))
    }

    @Test
    fun `array get throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.get(jsonArray.size + 1)
        }
    }

    @Test
    fun `array opt returns null for index beyond size`() {
        assertNull(jsonArray.opt(jsonArray.size + 1))
    }

    @Test
    fun `array getInt reads short integer number value`() {
        assertEquals(65536, jsonArray.getInt(0))
    }

    @Test
    fun `array getInt with fallback reads short integer number value`() {
        assertEquals(65536, jsonArray.getInt(0, -1))
    }

    @Test
    fun `array optInt reads short integer number value`() {
        assertEquals(65536, jsonArray.optInt(0))
    }

    @Test
    fun `array optInt with fallback reads short integer number value`() {
        assertEquals(65536, jsonArray.optInt(0, -1))
    }

    @Test
    fun `array getInt reads long integer number value`() {
        assertEquals(0, jsonArray.getInt(1))
    }

    @Test
    fun `array getInt with fallback reads long integer number value`() {
        assertEquals(0, jsonArray.getInt(1, -1))
    }

    @Test
    fun `array optInt reads long integer number value`() {
        assertEquals(0, jsonArray.optInt(1))
    }

    @Test
    fun `array optInt with fallback reads long integer number value`() {
        assertEquals(0, jsonArray.optInt(1, -1))
    }

    @Test
    fun `array getInt reads short integer string value`() {
        assertEquals(65536, jsonArray.getInt(6))
    }

    @Test
    fun `array getInt with fallback reads short integer string value`() {
        assertEquals(65536, jsonArray.getInt(6, -1))
    }

    @Test
    fun `array optInt reads short integer string value`() {
        assertEquals(65536, jsonArray.optInt(6))
    }

    @Test
    fun `array optInt with fallback reads short integer string value`() {
        assertEquals(65536, jsonArray.optInt(6, -1))
    }

    @Test
    fun `array getInt reads decimal number value`() {
        assertEquals(3, jsonArray.getInt(2))
    }

    @Test
    fun `array getInt with fallback reads decimal number value`() {
        assertEquals(3, jsonArray.getInt(2, -1))
    }

    @Test
    fun `array optInt reads decimal number value`() {
        assertEquals(3, jsonArray.optInt(2))
    }

    @Test
    fun `array optInt with fallback reads decimal number value`() {
        assertEquals(3, jsonArray.optInt(2, -1))
    }

    @Test
    fun `array getInt reads negative decimal number value`() {
        assertEquals(-3, jsonArray.getInt(3))
    }

    @Test
    fun `array getInt with fallback reads negative decimal number value`() {
        assertEquals(-3, jsonArray.getInt(3, -1))
    }

    @Test
    fun `array optInt reads negative decimal number value`() {
        assertEquals(-3, jsonArray.optInt(3))
    }

    @Test
    fun `array optInt with fallback reads negative decimal number value`() {
        assertEquals(-3, jsonArray.optInt(3, -1))
    }

    @Test
    fun `array getInt reads decimal string value`() {
        assertEquals(3, jsonArray.getInt(8))
    }

    @Test
    fun `array getInt with fallback reads decimal string value`() {
        assertEquals(3, jsonArray.getInt(8, -1))
    }

    @Test
    fun `array optInt reads decimal string value`() {
        assertEquals(3, jsonArray.optInt(8))
    }

    @Test
    fun `array optInt with fallback reads decimal string value`() {
        assertEquals(3, jsonArray.optInt(8, -1))
    }

    @Test
    fun `array getInt reads negative decimal string value`() {
        assertEquals(-3, jsonArray.getInt(9))
    }

    @Test
    fun `array getInt with fallback reads negative decimal string value`() {
        assertEquals(-3, jsonArray.getInt(9, -1))
    }

    @Test
    fun `array optInt reads negative decimal string value`() {
        assertEquals(-3, jsonArray.optInt(9))
    }

    @Test
    fun `array optInt with fallback reads negative decimal string value`() {
        assertEquals(-3, jsonArray.optInt(9, -1))
    }

    @Test
    fun `array getInt reads exponent decimal string value`() {
        assertEquals(65536, jsonArray.getInt(10))
    }

    @Test
    fun `array getInt with fallback reads exponent decimal string value`() {
        assertEquals(65536, jsonArray.getInt(10, -1))
    }

    @Test
    fun `array optInt reads exponent decimal string value`() {
        assertEquals(65536, jsonArray.optInt(10))
    }

    @Test
    fun `array optInt with fallback reads exponent decimal string value`() {
        assertEquals(65536, jsonArray.optInt(10, -1))
    }

    @Test
    fun `array getLong reads short integer number value`() {
        assertEquals(65536L, jsonArray.getLong(0))
    }

    @Test
    fun `array getLong with fallback reads short integer number value`() {
        assertEquals(65536L, jsonArray.getLong(0, -1L))
    }

    @Test
    fun `array optLong reads short integer number value`() {
        assertEquals(65536L, jsonArray.optLong(0))
    }

    @Test
    fun `array optLong with fallback reads short integer number value`() {
        assertEquals(65536L, jsonArray.optLong(0, -1L))
    }

    @Test
    fun `array getLong reads long integer number value`() {
        assertEquals(1099511627776L, jsonArray.getLong(1))
    }

    @Test
    fun `array getLong with fallback reads long integer number value`() {
        assertEquals(1099511627776L, jsonArray.getLong(1, -1L))
    }

    @Test
    fun `array optLong reads long integer number value`() {
        assertEquals(1099511627776L, jsonArray.optLong(1))
    }

    @Test
    fun `array optLong with fallback reads long integer number value`() {
        assertEquals(1099511627776L, jsonArray.optLong(1, -1L))
    }

    @Test
    fun `array getLong reads decimal number value`() {
        assertEquals(3L, jsonArray.getLong(2))
    }

    @Test
    fun `array getLong with fallback reads decimal number value`() {
        assertEquals(3L, jsonArray.getLong(2, -1L))
    }

    @Test
    fun `array optLong reads decimal number value`() {
        assertEquals(3L, jsonArray.optLong(2))
    }

    @Test
    fun `array optLong with fallback reads decimal number value`() {
        assertEquals(3L, jsonArray.optLong(2, -1L))
    }

    @Test
    fun `array getLong reads negative decimal number value`() {
        assertEquals(-3L, jsonArray.getLong(3))
    }

    @Test
    fun `array getLong with fallback reads negative decimal number value`() {
        assertEquals(-3L, jsonArray.getLong(3, -1L))
    }

    @Test
    fun `array optLong reads negative decimal number value`() {
        assertEquals(-3L, jsonArray.optLong(3))
    }

    @Test
    fun `array optLong with fallback reads negative decimal number value`() {
        assertEquals(-3L, jsonArray.optLong(3, -1L))
    }

    @Test
    fun `array getLong reads decimal string value`() {
        assertEquals(3L, jsonArray.getLong(8))
    }

    @Test
    fun `array getLong with fallback reads decimal string value`() {
        assertEquals(3L, jsonArray.getLong(8, -1L))
    }

    @Test
    fun `array optLong reads decimal string value`() {
        assertEquals(3L, jsonArray.optLong(8))
    }

    @Test
    fun `array optLong with fallback reads decimal string value`() {
        assertEquals(3L, jsonArray.optLong(8, -1L))
    }

    @Test
    fun `array getLong reads negative decimal string value`() {
        assertEquals(-3L, jsonArray.getLong(9))
    }

    @Test
    fun `array getLong with fallback reads negative decimal string value`() {
        assertEquals(-3L, jsonArray.getLong(9, -1L))
    }

    @Test
    fun `array optLong reads negative decimal string value`() {
        assertEquals(-3L, jsonArray.optLong(9))
    }

    @Test
    fun `array optLong with fallback reads negative decimal string value`() {
        assertEquals(-3L, jsonArray.optLong(9, -1L))
    }

    @Test
    fun `array getLong reads exponent decimal string value`() {
        assertEquals(65536L, jsonArray.getLong(10))
    }

    @Test
    fun `array getLong with fallback reads exponent decimal string value`() {
        assertEquals(65536L, jsonArray.getLong(10, -1L))
    }

    @Test
    fun `array optLong reads exponent decimal string value`() {
        assertEquals(65536L, jsonArray.optLong(10))
    }

    @Test
    fun `array optLong with fallback reads exponent decimal string value`() {
        assertEquals(65536L, jsonArray.optLong(10, -1L))
    }

    @Test
    fun `array getLong reads short integer string value`() {
        assertEquals(65536L, jsonArray.getLong(6))
    }

    @Test
    fun `array getLong with fallback reads short integer string value`() {
        assertEquals(65536L, jsonArray.getLong(6, -1L))
    }

    @Test
    fun `array optLong reads short integer string value`() {
        assertEquals(65536L, jsonArray.optLong(6))
    }

    @Test
    fun `array optLong with fallback reads short integer string value`() {
        assertEquals(65536L, jsonArray.optLong(6, -1L))
    }

    @Test
    fun `array getLong reads long integer string value`() {
        assertEquals(1099511627776L, jsonArray.getLong(7))
    }

    @Test
    fun `array getLong with fallback reads long integer string value`() {
        assertEquals(1099511627776L, jsonArray.getLong(7, -1L))
    }

    @Test
    fun `array optLong reads long integer string value`() {
        assertEquals(1099511627776L, jsonArray.optLong(7))
    }

    @Test
    fun `array optLong with fallback reads long integer string value`() {
        assertEquals(1099511627776L, jsonArray.optLong(7, -1L))
    }

    @Test
    fun `array getFloat reads decimal number value`() {
        assertEquals(3.1415927f, jsonArray.getFloat(2), 0f)
    }

    @Test
    fun `array getFloat with fallback reads decimal number value`() {
        assertEquals(3.1415927f, jsonArray.getFloat(2, -1f), 0f)
    }

    @Test
    fun `array optFloat reads decimal number value`() {
        assertEquals(3.1415927f, jsonArray.optFloat(2)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads decimal number value`() {
        assertEquals(3.1415927f, jsonArray.optFloat(2, -1f), 0f)
    }

    @Test
    fun `array getFloat reads short integer number value`() {
        assertEquals(65536f, jsonArray.getFloat(0), 0f)
    }

    @Test
    fun `array getFloat with fallback reads short integer number value`() {
        assertEquals(65536f, jsonArray.getFloat(0, -1f), 0f)
    }

    @Test
    fun `array optFloat reads short integer number value`() {
        assertEquals(65536f, jsonArray.optFloat(0)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads short integer number value`() {
        assertEquals(65536f, jsonArray.optFloat(0, -1f), 0f)
    }

    @Test
    fun `array getFloat reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonArray.getFloat(1), 0f)
    }

    @Test
    fun `array getFloat with fallback reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonArray.getFloat(1, -1f), 0f)
    }

    @Test
    fun `array optFloat reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonArray.optFloat(1)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonArray.optFloat(1, -1f), 0f)
    }

    @Test
    fun `array getFloat reads short integer string value`() {
        assertEquals(65536f, jsonArray.getFloat(6), 0f)
    }

    @Test
    fun `array getFloat with fallback reads short integer string value`() {
        assertEquals(65536f, jsonArray.getFloat(6, -1f), 0f)
    }

    @Test
    fun `array optFloat reads short integer string value`() {
        assertEquals(65536f, jsonArray.optFloat(6)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads short integer string value`() {
        assertEquals(65536f, jsonArray.optFloat(6, -1f), 0f)
    }

    @Test
    fun `array getFloat reads decimal string value`() {
        assertEquals(3.1415927f, jsonArray.getFloat(8), 0f)
    }

    @Test
    fun `array getFloat with fallback reads decimal string value`() {
        assertEquals(3.1415927f, jsonArray.getFloat(8, -1f), 0f)
    }

    @Test
    fun `array optFloat reads decimal string value`() {
        assertEquals(3.1415927f, jsonArray.optFloat(8)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads decimal string value`() {
        assertEquals(3.1415927f, jsonArray.optFloat(8, -1f), 0f)
    }

    @Test
    fun `array getFloat reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonArray.getFloat(9), 0f)
    }

    @Test
    fun `array getFloat with fallback reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonArray.getFloat(9, -1f), 0f)
    }

    @Test
    fun `array optFloat reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonArray.optFloat(9)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonArray.optFloat(9, -1f), 0f)
    }

    @Test
    fun `array getFloat reads exponent decimal string value`() {
        assertEquals(65536f, jsonArray.getFloat(10), 0f)
    }

    @Test
    fun `array getFloat with fallback reads exponent decimal string value`() {
        assertEquals(65536f, jsonArray.getFloat(10, -1f), 0f)
    }

    @Test
    fun `array optFloat reads exponent decimal string value`() {
        assertEquals(65536f, jsonArray.optFloat(10)!!, 0f)
    }

    @Test
    fun `array optFloat with fallback reads exponent decimal string value`() {
        assertEquals(65536f, jsonArray.optFloat(10, -1f), 0f)
    }

    @Test
    fun `array getDouble reads decimal number value`() {
        assertEquals(3.141592653589793, jsonArray.getDouble(2), PRECISION_DELTA)
    }

    @Test
    fun `array getDouble with fallback reads decimal number value`() {
        assertEquals(3.141592653589793, jsonArray.getDouble(2, -1.0), PRECISION_DELTA)
    }

    @Test
    fun `array optDouble reads decimal number value`() {
        assertEquals(3.141592653589793, jsonArray.optDouble(2)!!, PRECISION_DELTA)
    }

    @Test
    fun `array optDouble with fallback reads decimal number value`() {
        assertEquals(3.141592653589793, jsonArray.optDouble(2, -1.0), PRECISION_DELTA)
    }

    @Test
    fun `array getDouble reads short integer number value`() {
        assertEquals(65536.0, jsonArray.getDouble(0), 0.0)
    }

    @Test
    fun `array getDouble with fallback reads short integer number value`() {
        assertEquals(65536.0, jsonArray.getDouble(0, -1.0), 0.0)
    }

    @Test
    fun `array optDouble reads short integer number value`() {
        assertEquals(65536.0, jsonArray.optDouble(0)!!, 0.0)
    }

    @Test
    fun `array optDouble with fallback reads short integer number value`() {
        assertEquals(65536.0, jsonArray.optDouble(0, -1.0), 0.0)
    }

    @Test
    fun `array getDouble reads long integer number value`() {
        assertEquals(1099511627776.0, jsonArray.getDouble(1), 0.0)
    }

    @Test
    fun `array getDouble with fallback reads long integer number value`() {
        assertEquals(1099511627776.0, jsonArray.getDouble(1, -1.0), 0.0)
    }

    @Test
    fun `array optDouble reads long integer number value`() {
        assertEquals(1099511627776.0, jsonArray.optDouble(1)!!, 0.0)
    }

    @Test
    fun `array optDouble with fallback reads long integer number value`() {
        assertEquals(1099511627776.0, jsonArray.optDouble(1, -1.0), 0.0)
    }

    @Test
    fun `array getDouble reads short integer string value`() {
        assertEquals(65536.0, jsonArray.getDouble(6), 0.0)
    }

    @Test
    fun `array getDouble with fallback reads short integer string value`() {
        assertEquals(65536.0, jsonArray.getDouble(6, -1.0), 0.0)
    }

    @Test
    fun `array optDouble reads short integer string value`() {
        assertEquals(65536.0, jsonArray.optDouble(6)!!, 0.0)
    }

    @Test
    fun `array optDouble with fallback reads short integer string value`() {
        assertEquals(65536.0, jsonArray.optDouble(6, -1.0), 0.0)
    }

    @Test
    fun `array getDouble reads decimal string value`() {
        assertEquals(3.141592653589793, jsonArray.getDouble(8), 0.0)
    }

    @Test
    fun `array getDouble with fallback reads decimal string value`() {
        assertEquals(3.141592653589793, jsonArray.getDouble(8, -1.0), 0.0)
    }

    @Test
    fun `array optDouble reads decimal string value`() {
        assertEquals(3.141592653589793, jsonArray.optDouble(8)!!, 0.0)
    }

    @Test
    fun `array optDouble with fallback reads decimal string value`() {
        assertEquals(3.141592653589793, jsonArray.optDouble(8, -1.0), 0.0)
    }

    @Test
    fun `array getDouble reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonArray.getDouble(9), 0.0)
    }

    @Test
    fun `array getDouble with fallback reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonArray.getDouble(9, -1.0), 0.0)
    }

    @Test
    fun `array optDouble reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonArray.optDouble(9)!!, 0.0)
    }

    @Test
    fun `array optDouble with fallback reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonArray.optDouble(9, -1.0), 0.0)
    }

    @Test
    fun `array getDouble reads exponent decimal string value`() {
        assertEquals(65536.0, jsonArray.getDouble(10), 0.0)
    }

    @Test
    fun `array getDouble with fallback reads exponent decimal string value`() {
        assertEquals(65536.0, jsonArray.getDouble(10, -1.0), 0.0)
    }

    @Test
    fun `array optDouble reads exponent decimal string value`() {
        assertEquals(65536.0, jsonArray.optDouble(10)!!, 0.0)
    }

    @Test
    fun `array optDouble with fallback reads exponent decimal string value`() {
        assertEquals(65536.0, jsonArray.optDouble(10, -1.0), 0.0)
    }

    @Test
    fun `array getBoolean reads boolean value`() {
        assertEquals(true, jsonArray.getBoolean(4))
    }

    @Test
    fun `array getBoolean with fallback reads boolean value`() {
        assertEquals(true, jsonArray.getBoolean(4, false))
    }

    @Test
    fun `array optBoolean reads boolean value`() {
        assertEquals(true, jsonArray.optBoolean(4))
    }

    @Test
    fun `array optBoolean with fallback reads boolean value`() {
        assertEquals(true, jsonArray.optBoolean(4, false))
    }

    @Test
    fun `array getBoolean reads true string value`() {
        assertEquals(true, jsonArray.getBoolean(11))
    }

    @Test
    fun `array getBoolean with fallback reads true string value`() {
        assertEquals(true, jsonArray.getBoolean(11, false))
    }

    @Test
    fun `array optBoolean reads true string value`() {
        assertEquals(true, jsonArray.optBoolean(11))
    }

    @Test
    fun `array optBoolean with fallback reads true string value`() {
        assertEquals(true, jsonArray.optBoolean(11, false))
    }

    @Test
    fun `array getBoolean reads false string value`() {
        assertEquals(false, jsonArray.getBoolean(12))
    }

    @Test
    fun `array getBoolean with fallback reads false string value`() {
        assertEquals(false, jsonArray.getBoolean(12, true))
    }

    @Test
    fun `array optBoolean reads false string value`() {
        assertEquals(false, jsonArray.optBoolean(12))
    }

    @Test
    fun `array optBoolean with fallback reads false string value`() {
        assertEquals(false, jsonArray.optBoolean(12, true))
    }

    @Test
    fun `array getString reads short integer number value`() {
        assertEquals("65536", jsonArray.getString(0))
    }

    @Test
    fun `array getString with fallback reads short integer number value`() {
        assertEquals("65536", jsonArray.getString(0, "fallback"))
    }

    @Test
    fun `array optString reads short integer number value`() {
        assertEquals("65536", jsonArray.optString(0))
    }

    @Test
    fun `array optString with fallback reads short integer number value`() {
        assertEquals("65536", jsonArray.optString(0, "fallback"))
    }

    @Test
    fun `array getString reads long integer number value`() {
        assertEquals("1099511627776", jsonArray.getString(1))
    }

    @Test
    fun `array getString with fallback reads long integer number value`() {
        assertEquals("1099511627776", jsonArray.getString(1, "fallback"))
    }

    @Test
    fun `array optString reads long integer number value`() {
        assertEquals("1099511627776", jsonArray.optString(1))
    }

    @Test
    fun `array optString with fallback reads long integer number value`() {
        assertEquals("1099511627776", jsonArray.optString(1, "fallback"))
    }

    @Test
    fun `array getString reads decimal number value`() {
        assertEquals("3.141592653589793", jsonArray.getString(2))
    }

    @Test
    fun `array getString with fallback reads decimal number value`() {
        assertEquals("3.141592653589793", jsonArray.getString(2, "fallback"))
    }

    @Test
    fun `array optString reads decimal number value`() {
        assertEquals("3.141592653589793", jsonArray.optString(2))
    }

    @Test
    fun `array optString with fallback reads decimal number value`() {
        assertEquals("3.141592653589793", jsonArray.optString(2, "fallback"))
    }

    @Test
    fun `array getString reads boolean value`() {
        assertEquals("true", jsonArray.getString(4))
    }

    @Test
    fun `array getString with fallback reads boolean value`() {
        assertEquals("true", jsonArray.getString(4, "fallback"))
    }

    @Test
    fun `array optString reads boolean value`() {
        assertEquals("true", jsonArray.optString(4))
    }

    @Test
    fun `array optString with fallback reads boolean value`() {
        assertEquals("true", jsonArray.optString(4, "fallback"))
    }

    @Test
    fun `array getString reads string value`() {
        assertEquals("Lorem ipsum", jsonArray.getString(5))
    }

    @Test
    fun `array getString with fallback reads string value`() {
        assertEquals("Lorem ipsum", jsonArray.getString(5, "fallback"))
    }

    @Test
    fun `array optString reads string value`() {
        assertEquals("Lorem ipsum", jsonArray.optString(5))
    }

    @Test
    fun `array optString with fallback reads string value`() {
        assertEquals("Lorem ipsum", jsonArray.optString(5, "fallback"))
    }

    @Test
    fun `array getInt throws for negative index`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getInt(-1)
        }
    }

    @Test
    fun `array getInt with fallback returns fallback for negative index`() {
        assertEquals(-1, jsonArray.getInt(-1, -1))
    }

    @Test
    fun `array optInt returns null for negative index`() {
        assertNull(jsonArray.optInt(-1))
    }

    @Test
    fun `array optInt with fallback returns fallback for negative index`() {
        assertEquals(-1, jsonArray.optInt(-1, -1))
    }

    @Test
    fun `array getLong throws for negative index`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getLong(-1)
        }
    }

    @Test
    fun `array getLong with fallback returns fallback for negative index`() {
        assertEquals(-1L, jsonArray.getLong(-1, -1L))
    }

    @Test
    fun `array optLong returns null for negative index`() {
        assertNull(jsonArray.optLong(-1))
    }

    @Test
    fun `array optLong with fallback returns fallback for negative index`() {
        assertEquals(-1L, jsonArray.optLong(-1, -1L))
    }

    @Test
    fun `array getFloat throws for negative index`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getFloat(-1)
        }
    }

    @Test
    fun `array getFloat with fallback returns fallback for negative index`() {
        assertEquals(-1f, jsonArray.getFloat(-1, -1f), 0f)
    }

    @Test
    fun `array optFloat returns null for negative index`() {
        assertNull(jsonArray.optFloat(-1))
    }

    @Test
    fun `array optFloat with fallback returns fallback for negative index`() {
        assertEquals(-1f, jsonArray.optFloat(-1, -1f), 0f)
    }

    @Test
    fun `array getDouble throws for negative index`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getDouble(-1)
        }
    }

    @Test
    fun `array getDouble with fallback returns fallback for negative index`() {
        assertEquals(-1.0, jsonArray.getDouble(-1, -1.0), 0.0)
    }

    @Test
    fun `array optDouble returns null for negative index`() {
        assertNull(jsonArray.optDouble(-1))
    }

    @Test
    fun `array optDouble with fallback returns fallback for negative index`() {
        assertEquals(-1.0, jsonArray.optDouble(-1, -1.0), 0.0)
    }

    @Test
    fun `array getBoolean throws for negative index`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getBoolean(-1)
        }
    }

    @Test
    fun `array getBoolean with fallback returns fallback for negative index`() {
        assertEquals(true, jsonArray.getBoolean(-1, true))
    }

    @Test
    fun `array optBoolean returns null for negative index`() {
        assertNull(jsonArray.optBoolean(-1))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for negative index`() {
        assertEquals(true, jsonArray.optBoolean(-1, true))
    }

    @Test
    fun `array getString throws for negative index`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getString(-1)
        }
    }

    @Test
    fun `array getString with fallback returns fallback for negative index`() {
        assertEquals("fallback", jsonArray.getString(-1, "fallback"))
    }

    @Test
    fun `array optString returns null for negative index`() {
        assertNull(jsonArray.optString(-1))
    }

    @Test
    fun `array optString with fallback returns fallback for negative index`() {
        assertEquals("fallback", jsonArray.optString(-1, "fallback"))
    }

    @Test
    fun `array getInt throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getInt(jsonArray.size)
        }
    }

    @Test
    fun `array getInt with fallback returns fallback for index equal to size`() {
        assertEquals(-1, jsonArray.getInt(jsonArray.size, -1))
    }

    @Test
    fun `array optInt returns null for index equal to size`() {
        assertNull(jsonArray.optInt(jsonArray.size))
    }

    @Test
    fun `array optInt with fallback returns fallback for index equal to size`() {
        assertEquals(-1, jsonArray.optInt(jsonArray.size, -1))
    }

    @Test
    fun `array getLong throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getLong(jsonArray.size)
        }
    }

    @Test
    fun `array getLong with fallback returns fallback for index equal to size`() {
        assertEquals(-1L, jsonArray.getLong(jsonArray.size, -1L))
    }

    @Test
    fun `array optLong returns null for index equal to size`() {
        assertNull(jsonArray.optLong(jsonArray.size))
    }

    @Test
    fun `array optLong with fallback returns fallback for index equal to size`() {
        assertEquals(-1L, jsonArray.optLong(jsonArray.size, -1L))
    }

    @Test
    fun `array getFloat throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getFloat(jsonArray.size)
        }
    }

    @Test
    fun `array getFloat with fallback returns fallback for index equal to size`() {
        assertEquals(-1f, jsonArray.getFloat(jsonArray.size, -1f), 0f)
    }

    @Test
    fun `array optFloat returns null for index equal to size`() {
        assertNull(jsonArray.optFloat(jsonArray.size))
    }

    @Test
    fun `array optFloat with fallback returns fallback for index equal to size`() {
        assertEquals(-1f, jsonArray.optFloat(jsonArray.size, -1f), 0f)
    }

    @Test
    fun `array getDouble throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getDouble(jsonArray.size)
        }
    }

    @Test
    fun `array getDouble with fallback returns fallback for index equal to size`() {
        assertEquals(-1.0, jsonArray.getDouble(jsonArray.size, -1.0), 0.0)
    }

    @Test
    fun `array optDouble returns null for index equal to size`() {
        assertNull(jsonArray.optDouble(jsonArray.size))
    }

    @Test
    fun `array optDouble with fallback returns fallback for index equal to size`() {
        assertEquals(-1.0, jsonArray.optDouble(jsonArray.size, -1.0), 0.0)
    }

    @Test
    fun `array getBoolean throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getBoolean(jsonArray.size)
        }
    }

    @Test
    fun `array getBoolean with fallback returns fallback for index equal to size`() {
        assertEquals(true, jsonArray.getBoolean(jsonArray.size, true))
    }

    @Test
    fun `array optBoolean returns null for index equal to size`() {
        assertNull(jsonArray.optBoolean(jsonArray.size))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for index equal to size`() {
        assertEquals(true, jsonArray.optBoolean(jsonArray.size, true))
    }

    @Test
    fun `array getString throws for index equal to size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getString(jsonArray.size)
        }
    }

    @Test
    fun `array getString with fallback returns fallback for index equal to size`() {
        assertEquals("fallback", jsonArray.getString(jsonArray.size, "fallback"))
    }

    @Test
    fun `array optString returns null for index equal to size`() {
        assertNull(jsonArray.optString(jsonArray.size))
    }

    @Test
    fun `array optString with fallback returns fallback for index equal to size`() {
        assertEquals("fallback", jsonArray.optString(jsonArray.size, "fallback"))
    }

    @Test
    fun `array getInt throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getInt(jsonArray.size + 1)
        }
    }

    @Test
    fun `array getInt with fallback returns fallback for index beyond size`() {
        assertEquals(-1, jsonArray.getInt(jsonArray.size + 1, -1))
    }

    @Test
    fun `array optInt returns null for index beyond size`() {
        assertNull(jsonArray.optInt(jsonArray.size + 1))
    }

    @Test
    fun `array optInt with fallback returns fallback for index beyond size`() {
        assertEquals(-1, jsonArray.optInt(jsonArray.size + 1, -1))
    }

    @Test
    fun `array getLong throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getLong(jsonArray.size + 1)
        }
    }

    @Test
    fun `array getLong with fallback returns fallback for index beyond size`() {
        assertEquals(-1L, jsonArray.getLong(jsonArray.size + 1, -1L))
    }

    @Test
    fun `array optLong returns null for index beyond size`() {
        assertNull(jsonArray.optLong(jsonArray.size + 1))
    }

    @Test
    fun `array optLong with fallback returns fallback for index beyond size`() {
        assertEquals(-1L, jsonArray.optLong(jsonArray.size + 1, -1L))
    }

    @Test
    fun `array getFloat throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getFloat(jsonArray.size + 1)
        }
    }

    @Test
    fun `array getFloat with fallback returns fallback for index beyond size`() {
        assertEquals(-1f, jsonArray.getFloat(jsonArray.size + 1, -1f), 0f)
    }

    @Test
    fun `array optFloat returns null for index beyond size`() {
        assertNull(jsonArray.optFloat(jsonArray.size + 1))
    }

    @Test
    fun `array optFloat with fallback returns fallback for index beyond size`() {
        assertEquals(-1f, jsonArray.optFloat(jsonArray.size + 1, -1f), 0f)
    }

    @Test
    fun `array getDouble throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getDouble(jsonArray.size + 1)
        }
    }

    @Test
    fun `array getDouble with fallback returns fallback for index beyond size`() {
        assertEquals(-1.0, jsonArray.getDouble(jsonArray.size + 1, -1.0), 0.0)
    }

    @Test
    fun `array optDouble returns null for index beyond size`() {
        assertNull(jsonArray.optDouble(jsonArray.size + 1))
    }

    @Test
    fun `array optDouble with fallback returns fallback for index beyond size`() {
        assertEquals(-1.0, jsonArray.optDouble(jsonArray.size + 1, -1.0), 0.0)
    }

    @Test
    fun `array getBoolean throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getBoolean(jsonArray.size + 1)
        }
    }

    @Test
    fun `array getBoolean with fallback returns fallback for index beyond size`() {
        assertEquals(true, jsonArray.getBoolean(jsonArray.size + 1, true))
    }

    @Test
    fun `array optBoolean returns null for index beyond size`() {
        assertNull(jsonArray.optBoolean(jsonArray.size + 1))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for index beyond size`() {
        assertEquals(true, jsonArray.optBoolean(jsonArray.size + 1, true))
    }

    @Test
    fun `array getString throws for index beyond size`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getString(jsonArray.size + 1)
        }
    }

    @Test
    fun `array getString with fallback returns fallback for index beyond size`() {
        assertEquals("fallback", jsonArray.getString(jsonArray.size + 1, "fallback"))
    }

    @Test
    fun `array optString returns null for index beyond size`() {
        assertNull(jsonArray.optString(jsonArray.size + 1))
    }

    @Test
    fun `array optString with fallback returns fallback for index beyond size`() {
        assertEquals("fallback", jsonArray.optString(jsonArray.size + 1, "fallback"))
    }

    @Test
    fun `array getInt throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getInt(15)
        }
    }

    @Test
    fun `array getInt with fallback returns fallback for json null`() {
        assertEquals(-1, jsonArray.getInt(15, -1))
    }

    @Test
    fun `array optInt returns null for json null`() {
        assertNull(jsonArray.optInt(15))
    }

    @Test
    fun `array optInt with fallback returns fallback for json null`() {
        assertEquals(-1, jsonArray.optInt(15, -1))
    }

    @Test
    fun `array getLong throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getLong(15)
        }
    }

    @Test
    fun `array getLong with fallback returns fallback for json null`() {
        assertEquals(-1L, jsonArray.getLong(15, -1L))
    }

    @Test
    fun `array optLong returns null for json null`() {
        assertNull(jsonArray.optLong(15))
    }

    @Test
    fun `array optLong with fallback returns fallback for json null`() {
        assertEquals(-1L, jsonArray.optLong(15, -1L))
    }

    @Test
    fun `array getFloat throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getFloat(15)
        }
    }

    @Test
    fun `array getFloat with fallback returns fallback for json null`() {
        assertEquals(-1f, jsonArray.getFloat(15, -1f), 0f)
    }

    @Test
    fun `array optFloat returns null for json null`() {
        assertNull(jsonArray.optFloat(15))
    }

    @Test
    fun `array optFloat with fallback returns fallback for json null`() {
        assertEquals(-1f, jsonArray.optFloat(15, -1f), 0f)
    }

    @Test
    fun `array getDouble throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getDouble(15)
        }
    }

    @Test
    fun `array getDouble with fallback returns fallback for json null`() {
        assertEquals(-1.0, jsonArray.getDouble(15, -1.0), 0.0)
    }

    @Test
    fun `array optDouble returns null for json null`() {
        assertNull(jsonArray.optDouble(15))
    }

    @Test
    fun `array optDouble with fallback returns fallback for json null`() {
        assertEquals(-1.0, jsonArray.optDouble(15, -1.0), 0.0)
    }

    @Test
    fun `array getBoolean throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getBoolean(15)
        }
    }

    @Test
    fun `array getBoolean with fallback returns fallback for json null`() {
        assertEquals(true, jsonArray.getBoolean(15, true))
    }

    @Test
    fun `array optBoolean returns null for json null`() {
        assertNull(jsonArray.optBoolean(15))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for json null`() {
        assertEquals(true, jsonArray.optBoolean(15, true))
    }

    @Test
    fun `array getString throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonArray.getString(15)
        }
    }

    @Test
    fun `array getString with fallback returns fallback for json null`() {
        assertEquals("fallback", jsonArray.getString(15, "fallback"))
    }

    @Test
    fun `array optString returns null for json null`() {
        assertNull(jsonArray.optString(15))
    }

    @Test
    fun `array optString with fallback returns fallback for json null`() {
        assertEquals("fallback", jsonArray.optString(15, "fallback"))
    }

    @Test
    fun `array getInt throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(5)
        }
        assertEquals("Value at index 5 of type string cannot be read as Int", exception.message)
    }

    @Test
    fun `array getInt with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(5, -1)
        }
        assertEquals("Value at index 5 of type string cannot be read as Int", exception.message)
    }

    @Test
    fun `array optInt returns null for non-numeric string`() {
        assertNull(jsonArray.optInt(5))
    }

    @Test
    fun `array optInt with fallback returns fallback for non-numeric string`() {
        assertEquals(-1, jsonArray.optInt(5, -1))
    }

    @Test
    fun `array getInt throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(4)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Int", exception.message)
    }

    @Test
    fun `array getInt with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(4, -1)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Int", exception.message)
    }

    @Test
    fun `array optInt returns null for boolean value`() {
        assertNull(jsonArray.optInt(4))
    }

    @Test
    fun `array optInt with fallback returns fallback for boolean value`() {
        assertEquals(-1, jsonArray.optInt(4, -1))
    }

    @Test
    fun `array getInt throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(16)
        }
        assertEquals("Value at index 16 of type object cannot be read as Int", exception.message)
    }

    @Test
    fun `array getInt with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(16, -1)
        }
        assertEquals("Value at index 16 of type object cannot be read as Int", exception.message)
    }

    @Test
    fun `array optInt returns null for object value`() {
        assertNull(jsonArray.optInt(16))
    }

    @Test
    fun `array optInt with fallback returns fallback for object value`() {
        assertEquals(-1, jsonArray.optInt(16, -1))
    }

    @Test
    fun `array getInt throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(17)
        }
        assertEquals("Value at index 17 of type array cannot be read as Int", exception.message)
    }

    @Test
    fun `array getInt with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getInt(17, -1)
        }
        assertEquals("Value at index 17 of type array cannot be read as Int", exception.message)
    }

    @Test
    fun `array optInt returns null for array value`() {
        assertNull(jsonArray.optInt(17))
    }

    @Test
    fun `array optInt with fallback returns fallback for array value`() {
        assertEquals(-1, jsonArray.optInt(17, -1))
    }

    @Test
    fun `array getLong throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(5)
        }
        assertEquals("Value at index 5 of type string cannot be read as Long", exception.message)
    }

    @Test
    fun `array getLong with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(5, -1L)
        }
        assertEquals("Value at index 5 of type string cannot be read as Long", exception.message)
    }

    @Test
    fun `array optLong returns null for non-numeric string`() {
        assertNull(jsonArray.optLong(5))
    }

    @Test
    fun `array optLong with fallback returns fallback for non-numeric string`() {
        assertEquals(-1L, jsonArray.optLong(5, -1L))
    }

    @Test
    fun `array getLong throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(4)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Long", exception.message)
    }

    @Test
    fun `array getLong with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(4, -1L)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Long", exception.message)
    }

    @Test
    fun `array optLong returns null for boolean value`() {
        assertNull(jsonArray.optLong(4))
    }

    @Test
    fun `array optLong with fallback returns fallback for boolean value`() {
        assertEquals(-1L, jsonArray.optLong(4, -1L))
    }

    @Test
    fun `array getLong throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(16)
        }
        assertEquals("Value at index 16 of type object cannot be read as Long", exception.message)
    }

    @Test
    fun `array getLong with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(16, -1L)
        }
        assertEquals("Value at index 16 of type object cannot be read as Long", exception.message)
    }

    @Test
    fun `array optLong returns null for object value`() {
        assertNull(jsonArray.optLong(16))
    }

    @Test
    fun `array optLong with fallback returns fallback for object value`() {
        assertEquals(-1L, jsonArray.optLong(16, -1L))
    }

    @Test
    fun `array getLong throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(17)
        }
        assertEquals("Value at index 17 of type array cannot be read as Long", exception.message)
    }

    @Test
    fun `array getLong with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getLong(17, -1L)
        }
        assertEquals("Value at index 17 of type array cannot be read as Long", exception.message)
    }

    @Test
    fun `array optLong returns null for array value`() {
        assertNull(jsonArray.optLong(17))
    }

    @Test
    fun `array optLong with fallback returns fallback for array value`() {
        assertEquals(-1L, jsonArray.optLong(17, -1L))
    }

    @Test
    fun `array getFloat throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(5)
        }
        assertEquals("Value at index 5 of type string cannot be read as Float", exception.message)
    }

    @Test
    fun `array getFloat with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(5, -1f)
        }
        assertEquals("Value at index 5 of type string cannot be read as Float", exception.message)
    }

    @Test
    fun `array optFloat returns null for non-numeric string`() {
        assertNull(jsonArray.optFloat(5))
    }

    @Test
    fun `array optFloat with fallback returns fallback for non-numeric string`() {
        assertEquals(-1f, jsonArray.optFloat(5, -1f), 0f)
    }

    @Test
    fun `array getFloat throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(4)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Float", exception.message)
    }

    @Test
    fun `array getFloat with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(4, -1f)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Float", exception.message)
    }

    @Test
    fun `array optFloat returns null for boolean value`() {
        assertNull(jsonArray.optFloat(4))
    }

    @Test
    fun `array optFloat with fallback returns fallback for boolean value`() {
        assertEquals(-1f, jsonArray.optFloat(4, -1f), 0f)
    }

    @Test
    fun `array getFloat throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(16)
        }
        assertEquals("Value at index 16 of type object cannot be read as Float", exception.message)
    }

    @Test
    fun `array getFloat with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(16, -1f)
        }
        assertEquals("Value at index 16 of type object cannot be read as Float", exception.message)
    }

    @Test
    fun `array optFloat returns null for object value`() {
        assertNull(jsonArray.optFloat(16))
    }

    @Test
    fun `array optFloat with fallback returns fallback for object value`() {
        assertEquals(-1f, jsonArray.optFloat(16, -1f), 0f)
    }

    @Test
    fun `array getFloat throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(17)
        }
        assertEquals("Value at index 17 of type array cannot be read as Float", exception.message)
    }

    @Test
    fun `array getFloat with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getFloat(17, -1f)
        }
        assertEquals("Value at index 17 of type array cannot be read as Float", exception.message)
    }

    @Test
    fun `array optFloat returns null for array value`() {
        assertNull(jsonArray.optFloat(17))
    }

    @Test
    fun `array optFloat with fallback returns fallback for array value`() {
        assertEquals(-1f, jsonArray.optFloat(17, -1f), 0f)
    }

    @Test
    fun `array getDouble throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(5)
        }
        assertEquals("Value at index 5 of type string cannot be read as Double", exception.message)
    }

    @Test
    fun `array getDouble with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(5, -1.0)
        }
        assertEquals("Value at index 5 of type string cannot be read as Double", exception.message)
    }

    @Test
    fun `array optDouble returns null for non-numeric string`() {
        assertNull(jsonArray.optDouble(5))
    }

    @Test
    fun `array optDouble with fallback returns fallback for non-numeric string`() {
        assertEquals(-1.0, jsonArray.optDouble(5, -1.0), 0.0)
    }

    @Test
    fun `array getDouble throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(4)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Double", exception.message)
    }

    @Test
    fun `array getDouble with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(4, -1.0)
        }
        assertEquals("Value at index 4 of type boolean cannot be read as Double", exception.message)
    }

    @Test
    fun `array optDouble returns null for boolean value`() {
        assertNull(jsonArray.optDouble(4))
    }

    @Test
    fun `array optDouble with fallback returns fallback for boolean value`() {
        assertEquals(-1.0, jsonArray.optDouble(4, -1.0), 0.0)
    }

    @Test
    fun `array getDouble throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(16)
        }
        assertEquals("Value at index 16 of type object cannot be read as Double", exception.message)
    }

    @Test
    fun `array getDouble with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(16, -1.0)
        }
        assertEquals("Value at index 16 of type object cannot be read as Double", exception.message)
    }

    @Test
    fun `array optDouble returns null for object value`() {
        assertNull(jsonArray.optDouble(16))
    }

    @Test
    fun `array optDouble with fallback returns fallback for object value`() {
        assertEquals(-1.0, jsonArray.optDouble(16, -1.0), 0.0)
    }

    @Test
    fun `array getDouble throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(17)
        }
        assertEquals("Value at index 17 of type array cannot be read as Double", exception.message)
    }

    @Test
    fun `array getDouble with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getDouble(17, -1.0)
        }
        assertEquals("Value at index 17 of type array cannot be read as Double", exception.message)
    }

    @Test
    fun `array optDouble returns null for array value`() {
        assertNull(jsonArray.optDouble(17))
    }

    @Test
    fun `array optDouble with fallback returns fallback for array value`() {
        assertEquals(-1.0, jsonArray.optDouble(17, -1.0), 0.0)
    }

    @Test
    fun `array getBoolean throws for uppercase boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(13)
        }
        assertEquals("Value at index 13 of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array getBoolean with fallback throws for uppercase boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(13, true)
        }
        assertEquals("Value at index 13 of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array optBoolean returns null for uppercase boolean string`() {
        assertNull(jsonArray.optBoolean(13))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for uppercase boolean string`() {
        assertEquals(true, jsonArray.optBoolean(13, true))
    }

    @Test
    fun `array getBoolean throws for mixed-case boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(14)
        }
        assertEquals("Value at index 14 of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array getBoolean with fallback throws for mixed-case boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(14, true)
        }
        assertEquals("Value at index 14 of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array optBoolean returns null for mixed-case boolean string`() {
        assertNull(jsonArray.optBoolean(14))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for mixed-case boolean string`() {
        assertEquals(true, jsonArray.optBoolean(14, true))
    }

    @Test
    fun `array getBoolean throws for short integer number value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(0)
        }
        assertEquals("Value at index 0 of type number cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array getBoolean with fallback throws for short integer number value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(0, true)
        }
        assertEquals("Value at index 0 of type number cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array optBoolean returns null for short integer number value`() {
        assertNull(jsonArray.optBoolean(0))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for short integer number value`() {
        assertEquals(true, jsonArray.optBoolean(0, true))
    }

    @Test
    fun `array getBoolean throws for non-boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(5)
        }
        assertEquals("Value at index 5 of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array getBoolean with fallback throws for non-boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(5, true)
        }
        assertEquals("Value at index 5 of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array optBoolean returns null for non-boolean string`() {
        assertNull(jsonArray.optBoolean(5))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for non-boolean string`() {
        assertEquals(true, jsonArray.optBoolean(5, true))
    }

    @Test
    fun `array getBoolean throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(16)
        }
        assertEquals("Value at index 16 of type object cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array getBoolean with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(16, true)
        }
        assertEquals("Value at index 16 of type object cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array optBoolean returns null for object value`() {
        assertNull(jsonArray.optBoolean(16))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for object value`() {
        assertEquals(true, jsonArray.optBoolean(16, true))
    }

    @Test
    fun `array getBoolean throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(17)
        }
        assertEquals("Value at index 17 of type array cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array getBoolean with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getBoolean(17, true)
        }
        assertEquals("Value at index 17 of type array cannot be read as Boolean", exception.message)
    }

    @Test
    fun `array optBoolean returns null for array value`() {
        assertNull(jsonArray.optBoolean(17))
    }

    @Test
    fun `array optBoolean with fallback returns fallback for array value`() {
        assertEquals(true, jsonArray.optBoolean(17, true))
    }

    @Test
    fun `array getString throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getString(16)
        }
        assertEquals("Value at index 16 of type object cannot be read as String", exception.message)
    }

    @Test
    fun `array getString with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getString(16, "fallback")
        }
        assertEquals("Value at index 16 of type object cannot be read as String", exception.message)
    }

    @Test
    fun `array optString returns null for object value`() {
        assertNull(jsonArray.optString(16))
    }

    @Test
    fun `array optString with fallback returns fallback for object value`() {
        assertEquals("fallback", jsonArray.optString(16, "fallback"))
    }

    @Test
    fun `array getString throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getString(17)
        }
        assertEquals("Value at index 17 of type array cannot be read as String", exception.message)
    }

    @Test
    fun `array getString with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonArray.getString(17, "fallback")
        }
        assertEquals("Value at index 17 of type array cannot be read as String", exception.message)
    }

    @Test
    fun `array optString returns null for array value`() {
        assertNull(jsonArray.optString(17))
    }

    @Test
    fun `array optString with fallback returns fallback for array value`() {
        assertEquals("fallback", jsonArray.optString(17, "fallback"))
    }

    @Test
    fun `array get returns equal nested objects`() {
        assertEquals(jsonArray.get(16), jsonArray.get(16))
    }

    @Test
    fun `array get returns nested objects with equal hash codes`() {
        assertEquals(jsonArray.get(16).hashCode(), jsonArray.get(16).hashCode())
    }

    @Test
    fun `array get returns equal nested arrays`() {
        assertEquals(jsonArray.get(17), jsonArray.get(17))
    }

    @Test
    fun `array get returns nested arrays with equal hash codes`() {
        assertEquals(jsonArray.get(17).hashCode(), jsonArray.get(17).hashCode())
    }

    @Test
    fun `array does not equal array with the same content`() {
        assertNotEquals(jsonArray, createJsonArray())
    }

    @Test
    fun `array does not equal its modified copy`() {
        assertNotEquals(jsonArray, jsonArray.modify {})
    }

    private fun createJsonArray(): JsonArray = when (backend) {
        JsonBackend.ORG_JSON -> OrgJsonArray(JSONArray(JSON_ARRAY))
        JsonBackend.KTX_JSON -> KtxJsonArray(KtxJson.parseToJsonElement(JSON_ARRAY).jsonArray)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<JsonBackend> = JsonBackend.entries
    }
}
