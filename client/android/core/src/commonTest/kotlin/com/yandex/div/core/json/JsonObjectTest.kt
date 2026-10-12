package com.yandex.div.core.json

import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlinx.serialization.json.Json as KtxJson

@RunWith(Parameterized::class)
class JsonObjectTest(backend: JsonBackend) {

    private val jsonObject: JsonObject = when (backend) {
        JsonBackend.ORG_JSON -> OrgJsonObject(JSONObject(JSON_OBJECT))
        JsonBackend.KTX_JSON -> KtxJsonObject(KtxJson.parseToJsonElement(JSON_OBJECT).jsonObject)
    }

    @Test
    fun `object get reads short integer number value`() {
        assertEquals(65536, jsonObject.get("short_integer_number"))
    }

    @Test
    fun `object opt reads short integer number value`() {
        assertEquals(65536, jsonObject.opt("short_integer_number"))
    }

    @Test
    fun `object get reads long integer number value`() {
        assertEquals(1099511627776L, jsonObject.get("long_integer_number"))
    }

    @Test
    fun `object opt reads long integer number value`() {
        assertEquals(1099511627776L, jsonObject.opt("long_integer_number"))
    }

    @Test
    fun `object get reads decimal number value`() {
        assertEquals(3.141592653589793, jsonObject.get("decimal_number"))
    }

    @Test
    fun `object opt reads decimal number value`() {
        assertEquals(3.141592653589793, jsonObject.opt("decimal_number"))
    }

    @Test
    fun `object get reads negative decimal number value`() {
        assertEquals(-3.141592653589793, jsonObject.get("negative_decimal_number"))
    }

    @Test
    fun `object opt reads negative decimal number value`() {
        assertEquals(-3.141592653589793, jsonObject.opt("negative_decimal_number"))
    }

    @Test
    fun `object get reads boolean value`() {
        assertEquals(true, jsonObject.get("boolean"))
    }

    @Test
    fun `object opt reads boolean value`() {
        assertEquals(true, jsonObject.opt("boolean"))
    }

    @Test
    fun `object get reads string value`() {
        assertEquals("Lorem ipsum", jsonObject.get("string"))
    }

    @Test
    fun `object opt reads string value`() {
        assertEquals("Lorem ipsum", jsonObject.opt("string"))
    }

    @Test
    fun `object get reads short integer string value`() {
        assertEquals("65536", jsonObject.get("short_integer_string"))
    }

    @Test
    fun `object opt reads short integer string value`() {
        assertEquals("65536", jsonObject.opt("short_integer_string"))
    }

    @Test
    fun `object get reads long integer string value`() {
        assertEquals("1099511627776", jsonObject.get("long_integer_string"))
    }

    @Test
    fun `object opt reads long integer string value`() {
        assertEquals("1099511627776", jsonObject.opt("long_integer_string"))
    }

    @Test
    fun `object get reads decimal string value`() {
        assertEquals("3.141592653589793", jsonObject.get("decimal_string"))
    }

    @Test
    fun `object opt reads decimal string value`() {
        assertEquals("3.141592653589793", jsonObject.opt("decimal_string"))
    }

    @Test
    fun `object get reads negative decimal string value`() {
        assertEquals("-3.141592653589793", jsonObject.get("negative_decimal_string"))
    }

    @Test
    fun `object opt reads negative decimal string value`() {
        assertEquals("-3.141592653589793", jsonObject.opt("negative_decimal_string"))
    }

    @Test
    fun `object get reads exponent decimal string value`() {
        assertEquals("6.5536e4", jsonObject.get("exponent_decimal_string"))
    }

    @Test
    fun `object opt reads exponent decimal string value`() {
        assertEquals("6.5536e4", jsonObject.opt("exponent_decimal_string"))
    }

    @Test
    fun `object get reads true string value`() {
        assertEquals("true", jsonObject.get("true_string"))
    }

    @Test
    fun `object opt reads true string value`() {
        assertEquals("true", jsonObject.opt("true_string"))
    }

    @Test
    fun `object get reads false string value`() {
        assertEquals("false", jsonObject.get("false_string"))
    }

    @Test
    fun `object opt reads false string value`() {
        assertEquals("false", jsonObject.opt("false_string"))
    }

    @Test
    fun `object get reads uppercase boolean string value`() {
        assertEquals("TRUE", jsonObject.get("uppercase_boolean_string"))
    }

    @Test
    fun `object opt reads uppercase boolean string value`() {
        assertEquals("TRUE", jsonObject.opt("uppercase_boolean_string"))
    }

    @Test
    fun `object get reads mixed case boolean string value`() {
        assertEquals("False", jsonObject.get("mixed_case_boolean_string"))
    }

    @Test
    fun `object opt reads mixed case boolean string value`() {
        assertEquals("False", jsonObject.opt("mixed_case_boolean_string"))
    }

    @Test
    fun `object get reads object value`() {
        assertEquals(mapOf("nested" to "value").toList(), (jsonObject.get("object") as JsonObject).toList())
    }

    @Test
    fun `object opt reads object value`() {
        assertEquals(mapOf("nested" to "value").toList(), (jsonObject.opt("object") as JsonObject).toList())
    }

    @Test
    fun `object get reads array value`() {
        assertEquals(listOf(65536), (jsonObject.get("array") as JsonArray).toList())
    }

    @Test
    fun `object opt reads array value`() {
        assertEquals(listOf(65536), (jsonObject.opt("array") as JsonArray).toList())
    }

    @Test
    fun `object get throws for json null`() {
        val exception = assertThrows(NoSuchElementException::class.java) {
            jsonObject.get("null")
        }
        assertEquals("Value for key 'null' is missing or null", exception.message)
    }

    @Test
    fun `object opt returns null for json null`() {
        assertNull(jsonObject.opt("null"))
    }

    @Test
    fun `object get throws for missing key`() {
        val exception = assertThrows(NoSuchElementException::class.java) {
            jsonObject.get("absent")
        }
        assertEquals("Value for key 'absent' is missing or null", exception.message)
    }

    @Test
    fun `object opt returns null for missing key`() {
        assertNull(jsonObject.opt("absent"))
    }

    @Test
    fun `object getInt reads short integer number value`() {
        assertEquals(65536, jsonObject.getInt("short_integer_number"))
    }

    @Test
    fun `object getInt with fallback reads short integer number value`() {
        assertEquals(65536, jsonObject.getInt("short_integer_number", -1))
    }

    @Test
    fun `object optInt reads short integer number value`() {
        assertEquals(65536, jsonObject.optInt("short_integer_number"))
    }

    @Test
    fun `object optInt with fallback reads short integer number value`() {
        assertEquals(65536, jsonObject.optInt("short_integer_number", -1))
    }

    @Test
    fun `object getInt reads long integer number value`() {
        assertEquals(0, jsonObject.getInt("long_integer_number"))
    }

    @Test
    fun `object getInt with fallback reads long integer number value`() {
        assertEquals(0, jsonObject.getInt("long_integer_number", -1))
    }

    @Test
    fun `object optInt reads long integer number value`() {
        assertEquals(0, jsonObject.optInt("long_integer_number"))
    }

    @Test
    fun `object optInt with fallback reads long integer number value`() {
        assertEquals(0, jsonObject.optInt("long_integer_number", -1))
    }

    @Test
    fun `object getInt reads short integer string value`() {
        assertEquals(65536, jsonObject.getInt("short_integer_string"))
    }

    @Test
    fun `object getInt with fallback reads short integer string value`() {
        assertEquals(65536, jsonObject.getInt("short_integer_string", -1))
    }

    @Test
    fun `object optInt reads short integer string value`() {
        assertEquals(65536, jsonObject.optInt("short_integer_string"))
    }

    @Test
    fun `object optInt with fallback reads short integer string value`() {
        assertEquals(65536, jsonObject.optInt("short_integer_string", -1))
    }

    @Test
    fun `object getInt reads decimal number value`() {
        assertEquals(3, jsonObject.getInt("decimal_number"))
    }

    @Test
    fun `object getInt with fallback reads decimal number value`() {
        assertEquals(3, jsonObject.getInt("decimal_number", -1))
    }

    @Test
    fun `object optInt reads decimal number value`() {
        assertEquals(3, jsonObject.optInt("decimal_number"))
    }

    @Test
    fun `object optInt with fallback reads decimal number value`() {
        assertEquals(3, jsonObject.optInt("decimal_number", -1))
    }

    @Test
    fun `object getInt reads negative decimal number value`() {
        assertEquals(-3, jsonObject.getInt("negative_decimal_number"))
    }

    @Test
    fun `object getInt with fallback reads negative decimal number value`() {
        assertEquals(-3, jsonObject.getInt("negative_decimal_number", -1))
    }

    @Test
    fun `object optInt reads negative decimal number value`() {
        assertEquals(-3, jsonObject.optInt("negative_decimal_number"))
    }

    @Test
    fun `object optInt with fallback reads negative decimal number value`() {
        assertEquals(-3, jsonObject.optInt("negative_decimal_number", -1))
    }

    @Test
    fun `object getInt reads decimal string value`() {
        assertEquals(3, jsonObject.getInt("decimal_string"))
    }

    @Test
    fun `object getInt with fallback reads decimal string value`() {
        assertEquals(3, jsonObject.getInt("decimal_string", -1))
    }

    @Test
    fun `object optInt reads decimal string value`() {
        assertEquals(3, jsonObject.optInt("decimal_string"))
    }

    @Test
    fun `object optInt with fallback reads decimal string value`() {
        assertEquals(3, jsonObject.optInt("decimal_string", -1))
    }

    @Test
    fun `object getInt reads negative decimal string value`() {
        assertEquals(-3, jsonObject.getInt("negative_decimal_string"))
    }

    @Test
    fun `object getInt with fallback reads negative decimal string value`() {
        assertEquals(-3, jsonObject.getInt("negative_decimal_string", -1))
    }

    @Test
    fun `object optInt reads negative decimal string value`() {
        assertEquals(-3, jsonObject.optInt("negative_decimal_string"))
    }

    @Test
    fun `object optInt with fallback reads negative decimal string value`() {
        assertEquals(-3, jsonObject.optInt("negative_decimal_string", -1))
    }

    @Test
    fun `object getInt reads exponent decimal string value`() {
        assertEquals(65536, jsonObject.getInt("exponent_decimal_string"))
    }

    @Test
    fun `object getInt with fallback reads exponent decimal string value`() {
        assertEquals(65536, jsonObject.getInt("exponent_decimal_string", -1))
    }

    @Test
    fun `object optInt reads exponent decimal string value`() {
        assertEquals(65536, jsonObject.optInt("exponent_decimal_string"))
    }

    @Test
    fun `object optInt with fallback reads exponent decimal string value`() {
        assertEquals(65536, jsonObject.optInt("exponent_decimal_string", -1))
    }

    @Test
    fun `object getLong reads short integer number value`() {
        assertEquals(65536L, jsonObject.getLong("short_integer_number"))
    }

    @Test
    fun `object getLong with fallback reads short integer number value`() {
        assertEquals(65536L, jsonObject.getLong("short_integer_number", -1L))
    }

    @Test
    fun `object optLong reads short integer number value`() {
        assertEquals(65536L, jsonObject.optLong("short_integer_number"))
    }

    @Test
    fun `object optLong with fallback reads short integer number value`() {
        assertEquals(65536L, jsonObject.optLong("short_integer_number", -1L))
    }

    @Test
    fun `object getLong reads long integer number value`() {
        assertEquals(1099511627776L, jsonObject.getLong("long_integer_number"))
    }

    @Test
    fun `object getLong with fallback reads long integer number value`() {
        assertEquals(1099511627776L, jsonObject.getLong("long_integer_number", -1L))
    }

    @Test
    fun `object optLong reads long integer number value`() {
        assertEquals(1099511627776L, jsonObject.optLong("long_integer_number"))
    }

    @Test
    fun `object optLong with fallback reads long integer number value`() {
        assertEquals(1099511627776L, jsonObject.optLong("long_integer_number", -1L))
    }

    @Test
    fun `object getLong reads decimal number value`() {
        assertEquals(3L, jsonObject.getLong("decimal_number"))
    }

    @Test
    fun `object getLong with fallback reads decimal number value`() {
        assertEquals(3L, jsonObject.getLong("decimal_number", -1L))
    }

    @Test
    fun `object optLong reads decimal number value`() {
        assertEquals(3L, jsonObject.optLong("decimal_number"))
    }

    @Test
    fun `object optLong with fallback reads decimal number value`() {
        assertEquals(3L, jsonObject.optLong("decimal_number", -1L))
    }

    @Test
    fun `object getLong reads negative decimal number value`() {
        assertEquals(-3L, jsonObject.getLong("negative_decimal_number"))
    }

    @Test
    fun `object getLong with fallback reads negative decimal number value`() {
        assertEquals(-3L, jsonObject.getLong("negative_decimal_number", -1L))
    }

    @Test
    fun `object optLong reads negative decimal number value`() {
        assertEquals(-3L, jsonObject.optLong("negative_decimal_number"))
    }

    @Test
    fun `object optLong with fallback reads negative decimal number value`() {
        assertEquals(-3L, jsonObject.optLong("negative_decimal_number", -1L))
    }

    @Test
    fun `object getLong reads decimal string value`() {
        assertEquals(3L, jsonObject.getLong("decimal_string"))
    }

    @Test
    fun `object getLong with fallback reads decimal string value`() {
        assertEquals(3L, jsonObject.getLong("decimal_string", -1L))
    }

    @Test
    fun `object optLong reads decimal string value`() {
        assertEquals(3L, jsonObject.optLong("decimal_string"))
    }

    @Test
    fun `object optLong with fallback reads decimal string value`() {
        assertEquals(3L, jsonObject.optLong("decimal_string", -1L))
    }

    @Test
    fun `object getLong reads negative decimal string value`() {
        assertEquals(-3L, jsonObject.getLong("negative_decimal_string"))
    }

    @Test
    fun `object getLong with fallback reads negative decimal string value`() {
        assertEquals(-3L, jsonObject.getLong("negative_decimal_string", -1L))
    }

    @Test
    fun `object optLong reads negative decimal string value`() {
        assertEquals(-3L, jsonObject.optLong("negative_decimal_string"))
    }

    @Test
    fun `object optLong with fallback reads negative decimal string value`() {
        assertEquals(-3L, jsonObject.optLong("negative_decimal_string", -1L))
    }

    @Test
    fun `object getLong reads exponent decimal string value`() {
        assertEquals(65536L, jsonObject.getLong("exponent_decimal_string"))
    }

    @Test
    fun `object getLong with fallback reads exponent decimal string value`() {
        assertEquals(65536L, jsonObject.getLong("exponent_decimal_string", -1L))
    }

    @Test
    fun `object optLong reads exponent decimal string value`() {
        assertEquals(65536L, jsonObject.optLong("exponent_decimal_string"))
    }

    @Test
    fun `object optLong with fallback reads exponent decimal string value`() {
        assertEquals(65536L, jsonObject.optLong("exponent_decimal_string", -1L))
    }

    @Test
    fun `object getLong reads short integer string value`() {
        assertEquals(65536L, jsonObject.getLong("short_integer_string"))
    }

    @Test
    fun `object getLong with fallback reads short integer string value`() {
        assertEquals(65536L, jsonObject.getLong("short_integer_string", -1L))
    }

    @Test
    fun `object optLong reads short integer string value`() {
        assertEquals(65536L, jsonObject.optLong("short_integer_string"))
    }

    @Test
    fun `object optLong with fallback reads short integer string value`() {
        assertEquals(65536L, jsonObject.optLong("short_integer_string", -1L))
    }

    @Test
    fun `object getLong reads long integer string value`() {
        assertEquals(1099511627776L, jsonObject.getLong("long_integer_string"))
    }

    @Test
    fun `object getLong with fallback reads long integer string value`() {
        assertEquals(1099511627776L, jsonObject.getLong("long_integer_string", -1L))
    }

    @Test
    fun `object optLong reads long integer string value`() {
        assertEquals(1099511627776L, jsonObject.optLong("long_integer_string"))
    }

    @Test
    fun `object optLong with fallback reads long integer string value`() {
        assertEquals(1099511627776L, jsonObject.optLong("long_integer_string", -1L))
    }

    @Test
    fun `object getFloat reads decimal number value`() {
        assertEquals(3.1415927f, jsonObject.getFloat("decimal_number"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads decimal number value`() {
        assertEquals(3.1415927f, jsonObject.getFloat("decimal_number", -1f), 0f)
    }

    @Test
    fun `object optFloat reads decimal number value`() {
        assertEquals(3.1415927f, jsonObject.optFloat("decimal_number")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads decimal number value`() {
        assertEquals(3.1415927f, jsonObject.optFloat("decimal_number", -1f), 0f)
    }

    @Test
    fun `object getFloat reads short integer number value`() {
        assertEquals(65536f, jsonObject.getFloat("short_integer_number"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads short integer number value`() {
        assertEquals(65536f, jsonObject.getFloat("short_integer_number", -1f), 0f)
    }

    @Test
    fun `object optFloat reads short integer number value`() {
        assertEquals(65536f, jsonObject.optFloat("short_integer_number")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads short integer number value`() {
        assertEquals(65536f, jsonObject.optFloat("short_integer_number", -1f), 0f)
    }

    @Test
    fun `object getFloat reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonObject.getFloat("long_integer_number"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonObject.getFloat("long_integer_number", -1f), 0f)
    }

    @Test
    fun `object optFloat reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonObject.optFloat("long_integer_number")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads long integer number value`() {
        assertEquals(1099511627776L.toFloat(), jsonObject.optFloat("long_integer_number", -1f), 0f)
    }

    @Test
    fun `object getFloat reads short integer string value`() {
        assertEquals(65536f, jsonObject.getFloat("short_integer_string"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads short integer string value`() {
        assertEquals(65536f, jsonObject.getFloat("short_integer_string", -1f), 0f)
    }

    @Test
    fun `object optFloat reads short integer string value`() {
        assertEquals(65536f, jsonObject.optFloat("short_integer_string")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads short integer string value`() {
        assertEquals(65536f, jsonObject.optFloat("short_integer_string", -1f), 0f)
    }

    @Test
    fun `object getFloat reads decimal string value`() {
        assertEquals(3.1415927f, jsonObject.getFloat("decimal_string"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads decimal string value`() {
        assertEquals(3.1415927f, jsonObject.getFloat("decimal_string", -1f), 0f)
    }

    @Test
    fun `object optFloat reads decimal string value`() {
        assertEquals(3.1415927f, jsonObject.optFloat("decimal_string")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads decimal string value`() {
        assertEquals(3.1415927f, jsonObject.optFloat("decimal_string", -1f), 0f)
    }

    @Test
    fun `object getFloat reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonObject.getFloat("negative_decimal_string"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonObject.getFloat("negative_decimal_string", -1f), 0f)
    }

    @Test
    fun `object optFloat reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonObject.optFloat("negative_decimal_string")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads negative decimal string value`() {
        assertEquals(-3.1415927f, jsonObject.optFloat("negative_decimal_string", -1f), 0f)
    }

    @Test
    fun `object getFloat reads exponent decimal string value`() {
        assertEquals(65536f, jsonObject.getFloat("exponent_decimal_string"), 0f)
    }

    @Test
    fun `object getFloat with fallback reads exponent decimal string value`() {
        assertEquals(65536f, jsonObject.getFloat("exponent_decimal_string", -1f), 0f)
    }

    @Test
    fun `object optFloat reads exponent decimal string value`() {
        assertEquals(65536f, jsonObject.optFloat("exponent_decimal_string")!!, 0f)
    }

    @Test
    fun `object optFloat with fallback reads exponent decimal string value`() {
        assertEquals(65536f, jsonObject.optFloat("exponent_decimal_string", -1f), 0f)
    }

    @Test
    fun `object getDouble reads decimal number value`() {
        assertEquals(3.141592653589793, jsonObject.getDouble("decimal_number"), PRECISION_DELTA)
    }

    @Test
    fun `object getDouble with fallback reads decimal number value`() {
        assertEquals(3.141592653589793, jsonObject.getDouble("decimal_number", -1.0), PRECISION_DELTA)
    }

    @Test
    fun `object optDouble reads decimal number value`() {
        assertEquals(3.141592653589793, jsonObject.optDouble("decimal_number")!!, PRECISION_DELTA)
    }

    @Test
    fun `object optDouble with fallback reads decimal number value`() {
        assertEquals(3.141592653589793, jsonObject.optDouble("decimal_number", -1.0), PRECISION_DELTA)
    }

    @Test
    fun `object getDouble reads short integer number value`() {
        assertEquals(65536.0, jsonObject.getDouble("short_integer_number"), 0.0)
    }

    @Test
    fun `object getDouble with fallback reads short integer number value`() {
        assertEquals(65536.0, jsonObject.getDouble("short_integer_number", -1.0), 0.0)
    }

    @Test
    fun `object optDouble reads short integer number value`() {
        assertEquals(65536.0, jsonObject.optDouble("short_integer_number")!!, 0.0)
    }

    @Test
    fun `object optDouble with fallback reads short integer number value`() {
        assertEquals(65536.0, jsonObject.optDouble("short_integer_number", -1.0), 0.0)
    }

    @Test
    fun `object getDouble reads long integer number value`() {
        assertEquals(1099511627776.0, jsonObject.getDouble("long_integer_number"), 0.0)
    }

    @Test
    fun `object getDouble with fallback reads long integer number value`() {
        assertEquals(1099511627776.0, jsonObject.getDouble("long_integer_number", -1.0), 0.0)
    }

    @Test
    fun `object optDouble reads long integer number value`() {
        assertEquals(1099511627776.0, jsonObject.optDouble("long_integer_number")!!, 0.0)
    }

    @Test
    fun `object optDouble with fallback reads long integer number value`() {
        assertEquals(1099511627776.0, jsonObject.optDouble("long_integer_number", -1.0), 0.0)
    }

    @Test
    fun `object getDouble reads short integer string value`() {
        assertEquals(65536.0, jsonObject.getDouble("short_integer_string"), 0.0)
    }

    @Test
    fun `object getDouble with fallback reads short integer string value`() {
        assertEquals(65536.0, jsonObject.getDouble("short_integer_string", -1.0), 0.0)
    }

    @Test
    fun `object optDouble reads short integer string value`() {
        assertEquals(65536.0, jsonObject.optDouble("short_integer_string")!!, 0.0)
    }

    @Test
    fun `object optDouble with fallback reads short integer string value`() {
        assertEquals(65536.0, jsonObject.optDouble("short_integer_string", -1.0), 0.0)
    }

    @Test
    fun `object getDouble reads decimal string value`() {
        assertEquals(3.141592653589793, jsonObject.getDouble("decimal_string"), 0.0)
    }

    @Test
    fun `object getDouble with fallback reads decimal string value`() {
        assertEquals(3.141592653589793, jsonObject.getDouble("decimal_string", -1.0), 0.0)
    }

    @Test
    fun `object optDouble reads decimal string value`() {
        assertEquals(3.141592653589793, jsonObject.optDouble("decimal_string")!!, 0.0)
    }

    @Test
    fun `object optDouble with fallback reads decimal string value`() {
        assertEquals(3.141592653589793, jsonObject.optDouble("decimal_string", -1.0), 0.0)
    }

    @Test
    fun `object getDouble reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonObject.getDouble("negative_decimal_string"), 0.0)
    }

    @Test
    fun `object getDouble with fallback reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonObject.getDouble("negative_decimal_string", -1.0), 0.0)
    }

    @Test
    fun `object optDouble reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonObject.optDouble("negative_decimal_string")!!, 0.0)
    }

    @Test
    fun `object optDouble with fallback reads negative decimal string value`() {
        assertEquals(-3.141592653589793, jsonObject.optDouble("negative_decimal_string", -1.0), 0.0)
    }

    @Test
    fun `object getDouble reads exponent decimal string value`() {
        assertEquals(65536.0, jsonObject.getDouble("exponent_decimal_string"), 0.0)
    }

    @Test
    fun `object getDouble with fallback reads exponent decimal string value`() {
        assertEquals(65536.0, jsonObject.getDouble("exponent_decimal_string", -1.0), 0.0)
    }

    @Test
    fun `object optDouble reads exponent decimal string value`() {
        assertEquals(65536.0, jsonObject.optDouble("exponent_decimal_string")!!, 0.0)
    }

    @Test
    fun `object optDouble with fallback reads exponent decimal string value`() {
        assertEquals(65536.0, jsonObject.optDouble("exponent_decimal_string", -1.0), 0.0)
    }

    @Test
    fun `object getBoolean reads boolean value`() {
        assertEquals(true, jsonObject.getBoolean("boolean"))
    }

    @Test
    fun `object getBoolean with fallback reads boolean value`() {
        assertEquals(true, jsonObject.getBoolean("boolean", false))
    }

    @Test
    fun `object optBoolean reads boolean value`() {
        assertEquals(true, jsonObject.optBoolean("boolean"))
    }

    @Test
    fun `object optBoolean with fallback reads boolean value`() {
        assertEquals(true, jsonObject.optBoolean("boolean", false))
    }

    @Test
    fun `object getBoolean reads true string value`() {
        assertEquals(true, jsonObject.getBoolean("true_string"))
    }

    @Test
    fun `object getBoolean with fallback reads true string value`() {
        assertEquals(true, jsonObject.getBoolean("true_string", false))
    }

    @Test
    fun `object optBoolean reads true string value`() {
        assertEquals(true, jsonObject.optBoolean("true_string"))
    }

    @Test
    fun `object optBoolean with fallback reads true string value`() {
        assertEquals(true, jsonObject.optBoolean("true_string", false))
    }

    @Test
    fun `object getBoolean reads false string value`() {
        assertEquals(false, jsonObject.getBoolean("false_string"))
    }

    @Test
    fun `object getBoolean with fallback reads false string value`() {
        assertEquals(false, jsonObject.getBoolean("false_string", true))
    }

    @Test
    fun `object optBoolean reads false string value`() {
        assertEquals(false, jsonObject.optBoolean("false_string"))
    }

    @Test
    fun `object optBoolean with fallback reads false string value`() {
        assertEquals(false, jsonObject.optBoolean("false_string", true))
    }

    @Test
    fun `object getString reads short integer number value`() {
        assertEquals("65536", jsonObject.getString("short_integer_number"))
    }

    @Test
    fun `object getString with fallback reads short integer number value`() {
        assertEquals("65536", jsonObject.getString("short_integer_number", "fallback"))
    }

    @Test
    fun `object optString reads short integer number value`() {
        assertEquals("65536", jsonObject.optString("short_integer_number"))
    }

    @Test
    fun `object optString with fallback reads short integer number value`() {
        assertEquals("65536", jsonObject.optString("short_integer_number", "fallback"))
    }

    @Test
    fun `object getString reads long integer number value`() {
        assertEquals("1099511627776", jsonObject.getString("long_integer_number"))
    }

    @Test
    fun `object getString with fallback reads long integer number value`() {
        assertEquals("1099511627776", jsonObject.getString("long_integer_number", "fallback"))
    }

    @Test
    fun `object optString reads long integer number value`() {
        assertEquals("1099511627776", jsonObject.optString("long_integer_number"))
    }

    @Test
    fun `object optString with fallback reads long integer number value`() {
        assertEquals("1099511627776", jsonObject.optString("long_integer_number", "fallback"))
    }

    @Test
    fun `object getString reads decimal number value`() {
        assertEquals("3.141592653589793", jsonObject.getString("decimal_number"))
    }

    @Test
    fun `object getString with fallback reads decimal number value`() {
        assertEquals("3.141592653589793", jsonObject.getString("decimal_number", "fallback"))
    }

    @Test
    fun `object optString reads decimal number value`() {
        assertEquals("3.141592653589793", jsonObject.optString("decimal_number"))
    }

    @Test
    fun `object optString with fallback reads decimal number value`() {
        assertEquals("3.141592653589793", jsonObject.optString("decimal_number", "fallback"))
    }

    @Test
    fun `object getString reads boolean value`() {
        assertEquals("true", jsonObject.getString("boolean"))
    }

    @Test
    fun `object getString with fallback reads boolean value`() {
        assertEquals("true", jsonObject.getString("boolean", "fallback"))
    }

    @Test
    fun `object optString reads boolean value`() {
        assertEquals("true", jsonObject.optString("boolean"))
    }

    @Test
    fun `object optString with fallback reads boolean value`() {
        assertEquals("true", jsonObject.optString("boolean", "fallback"))
    }

    @Test
    fun `object getString reads string value`() {
        assertEquals("Lorem ipsum", jsonObject.getString("string"))
    }

    @Test
    fun `object getString with fallback reads string value`() {
        assertEquals("Lorem ipsum", jsonObject.getString("string", "fallback"))
    }

    @Test
    fun `object optString reads string value`() {
        assertEquals("Lorem ipsum", jsonObject.optString("string"))
    }

    @Test
    fun `object optString with fallback reads string value`() {
        assertEquals("Lorem ipsum", jsonObject.optString("string", "fallback"))
    }

    @Test
    fun `object getInt throws for missing key`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getInt("absent")
        }
    }

    @Test
    fun `object getInt with fallback returns fallback for missing key`() {
        assertEquals(-1, jsonObject.getInt("absent", -1))
    }

    @Test
    fun `object optInt returns null for missing key`() {
        assertNull(jsonObject.optInt("absent"))
    }

    @Test
    fun `object optInt with fallback returns fallback for missing key`() {
        assertEquals(-1, jsonObject.optInt("absent", -1))
    }

    @Test
    fun `object getLong throws for missing key`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getLong("absent")
        }
    }

    @Test
    fun `object getLong with fallback returns fallback for missing key`() {
        assertEquals(-1L, jsonObject.getLong("absent", -1L))
    }

    @Test
    fun `object optLong returns null for missing key`() {
        assertNull(jsonObject.optLong("absent"))
    }

    @Test
    fun `object optLong with fallback returns fallback for missing key`() {
        assertEquals(-1L, jsonObject.optLong("absent", -1L))
    }

    @Test
    fun `object getFloat throws for missing key`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getFloat("absent")
        }
    }

    @Test
    fun `object getFloat with fallback returns fallback for missing key`() {
        assertEquals(-1f, jsonObject.getFloat("absent", -1f), 0f)
    }

    @Test
    fun `object optFloat returns null for missing key`() {
        assertNull(jsonObject.optFloat("absent"))
    }

    @Test
    fun `object optFloat with fallback returns fallback for missing key`() {
        assertEquals(-1f, jsonObject.optFloat("absent", -1f), 0f)
    }

    @Test
    fun `object getDouble throws for missing key`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getDouble("absent")
        }
    }

    @Test
    fun `object getDouble with fallback returns fallback for missing key`() {
        assertEquals(-1.0, jsonObject.getDouble("absent", -1.0), 0.0)
    }

    @Test
    fun `object optDouble returns null for missing key`() {
        assertNull(jsonObject.optDouble("absent"))
    }

    @Test
    fun `object optDouble with fallback returns fallback for missing key`() {
        assertEquals(-1.0, jsonObject.optDouble("absent", -1.0), 0.0)
    }

    @Test
    fun `object getBoolean throws for missing key`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getBoolean("absent")
        }
    }

    @Test
    fun `object getBoolean with fallback returns fallback for missing key`() {
        assertEquals(true, jsonObject.getBoolean("absent", true))
    }

    @Test
    fun `object optBoolean returns null for missing key`() {
        assertNull(jsonObject.optBoolean("absent"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for missing key`() {
        assertEquals(true, jsonObject.optBoolean("absent", true))
    }

    @Test
    fun `object getString throws for missing key`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getString("absent")
        }
    }

    @Test
    fun `object getString with fallback returns fallback for missing key`() {
        assertEquals("fallback", jsonObject.getString("absent", "fallback"))
    }

    @Test
    fun `object optString returns null for missing key`() {
        assertNull(jsonObject.optString("absent"))
    }

    @Test
    fun `object optString with fallback returns fallback for missing key`() {
        assertEquals("fallback", jsonObject.optString("absent", "fallback"))
    }

    @Test
    fun `object getInt throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getInt("null")
        }
    }

    @Test
    fun `object getInt with fallback returns fallback for json null`() {
        assertEquals(-1, jsonObject.getInt("null", -1))
    }

    @Test
    fun `object optInt returns null for json null`() {
        assertNull(jsonObject.optInt("null"))
    }

    @Test
    fun `object optInt with fallback returns fallback for json null`() {
        assertEquals(-1, jsonObject.optInt("null", -1))
    }

    @Test
    fun `object getLong throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getLong("null")
        }
    }

    @Test
    fun `object getLong with fallback returns fallback for json null`() {
        assertEquals(-1L, jsonObject.getLong("null", -1L))
    }

    @Test
    fun `object optLong returns null for json null`() {
        assertNull(jsonObject.optLong("null"))
    }

    @Test
    fun `object optLong with fallback returns fallback for json null`() {
        assertEquals(-1L, jsonObject.optLong("null", -1L))
    }

    @Test
    fun `object getFloat throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getFloat("null")
        }
    }

    @Test
    fun `object getFloat with fallback returns fallback for json null`() {
        assertEquals(-1f, jsonObject.getFloat("null", -1f), 0f)
    }

    @Test
    fun `object optFloat returns null for json null`() {
        assertNull(jsonObject.optFloat("null"))
    }

    @Test
    fun `object optFloat with fallback returns fallback for json null`() {
        assertEquals(-1f, jsonObject.optFloat("null", -1f), 0f)
    }

    @Test
    fun `object getDouble throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getDouble("null")
        }
    }

    @Test
    fun `object getDouble with fallback returns fallback for json null`() {
        assertEquals(-1.0, jsonObject.getDouble("null", -1.0), 0.0)
    }

    @Test
    fun `object optDouble returns null for json null`() {
        assertNull(jsonObject.optDouble("null"))
    }

    @Test
    fun `object optDouble with fallback returns fallback for json null`() {
        assertEquals(-1.0, jsonObject.optDouble("null", -1.0), 0.0)
    }

    @Test
    fun `object getBoolean throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getBoolean("null")
        }
    }

    @Test
    fun `object getBoolean with fallback returns fallback for json null`() {
        assertEquals(true, jsonObject.getBoolean("null", true))
    }

    @Test
    fun `object optBoolean returns null for json null`() {
        assertNull(jsonObject.optBoolean("null"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for json null`() {
        assertEquals(true, jsonObject.optBoolean("null", true))
    }

    @Test
    fun `object getString throws for json null`() {
        assertThrows(NoSuchElementException::class.java) {
            jsonObject.getString("null")
        }
    }

    @Test
    fun `object getString with fallback returns fallback for json null`() {
        assertEquals("fallback", jsonObject.getString("null", "fallback"))
    }

    @Test
    fun `object optString returns null for json null`() {
        assertNull(jsonObject.optString("null"))
    }

    @Test
    fun `object optString with fallback returns fallback for json null`() {
        assertEquals("fallback", jsonObject.optString("null", "fallback"))
    }

    @Test
    fun `object getInt throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("string")
        }
        assertEquals("Value for key 'string' of type string cannot be read as Int", exception.message)
    }

    @Test
    fun `object getInt with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("string", -1)
        }
        assertEquals("Value for key 'string' of type string cannot be read as Int", exception.message)
    }

    @Test
    fun `object optInt returns null for non-numeric string`() {
        assertNull(jsonObject.optInt("string"))
    }

    @Test
    fun `object optInt with fallback returns fallback for non-numeric string`() {
        assertEquals(-1, jsonObject.optInt("string", -1))
    }

    @Test
    fun `object getInt throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("boolean")
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Int", exception.message)
    }

    @Test
    fun `object getInt with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("boolean", -1)
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Int", exception.message)
    }

    @Test
    fun `object optInt returns null for boolean value`() {
        assertNull(jsonObject.optInt("boolean"))
    }

    @Test
    fun `object optInt with fallback returns fallback for boolean value`() {
        assertEquals(-1, jsonObject.optInt("boolean", -1))
    }

    @Test
    fun `object getInt throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("object")
        }
        assertEquals("Value for key 'object' of type object cannot be read as Int", exception.message)
    }

    @Test
    fun `object getInt with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("object", -1)
        }
        assertEquals("Value for key 'object' of type object cannot be read as Int", exception.message)
    }

    @Test
    fun `object optInt returns null for object value`() {
        assertNull(jsonObject.optInt("object"))
    }

    @Test
    fun `object optInt with fallback returns fallback for object value`() {
        assertEquals(-1, jsonObject.optInt("object", -1))
    }

    @Test
    fun `object getInt throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("array")
        }
        assertEquals("Value for key 'array' of type array cannot be read as Int", exception.message)
    }

    @Test
    fun `object getInt with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getInt("array", -1)
        }
        assertEquals("Value for key 'array' of type array cannot be read as Int", exception.message)
    }

    @Test
    fun `object optInt returns null for array value`() {
        assertNull(jsonObject.optInt("array"))
    }

    @Test
    fun `object optInt with fallback returns fallback for array value`() {
        assertEquals(-1, jsonObject.optInt("array", -1))
    }

    @Test
    fun `object getLong throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("string")
        }
        assertEquals("Value for key 'string' of type string cannot be read as Long", exception.message)
    }

    @Test
    fun `object getLong with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("string", -1L)
        }
        assertEquals("Value for key 'string' of type string cannot be read as Long", exception.message)
    }

    @Test
    fun `object optLong returns null for non-numeric string`() {
        assertNull(jsonObject.optLong("string"))
    }

    @Test
    fun `object optLong with fallback returns fallback for non-numeric string`() {
        assertEquals(-1L, jsonObject.optLong("string", -1L))
    }

    @Test
    fun `object getLong throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("boolean")
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Long", exception.message)
    }

    @Test
    fun `object getLong with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("boolean", -1L)
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Long", exception.message)
    }

    @Test
    fun `object optLong returns null for boolean value`() {
        assertNull(jsonObject.optLong("boolean"))
    }

    @Test
    fun `object optLong with fallback returns fallback for boolean value`() {
        assertEquals(-1L, jsonObject.optLong("boolean", -1L))
    }

    @Test
    fun `object getLong throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("object")
        }
        assertEquals("Value for key 'object' of type object cannot be read as Long", exception.message)
    }

    @Test
    fun `object getLong with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("object", -1L)
        }
        assertEquals("Value for key 'object' of type object cannot be read as Long", exception.message)
    }

    @Test
    fun `object optLong returns null for object value`() {
        assertNull(jsonObject.optLong("object"))
    }

    @Test
    fun `object optLong with fallback returns fallback for object value`() {
        assertEquals(-1L, jsonObject.optLong("object", -1L))
    }

    @Test
    fun `object getLong throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("array")
        }
        assertEquals("Value for key 'array' of type array cannot be read as Long", exception.message)
    }

    @Test
    fun `object getLong with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getLong("array", -1L)
        }
        assertEquals("Value for key 'array' of type array cannot be read as Long", exception.message)
    }

    @Test
    fun `object optLong returns null for array value`() {
        assertNull(jsonObject.optLong("array"))
    }

    @Test
    fun `object optLong with fallback returns fallback for array value`() {
        assertEquals(-1L, jsonObject.optLong("array", -1L))
    }

    @Test
    fun `object getFloat throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("string")
        }
        assertEquals("Value for key 'string' of type string cannot be read as Float", exception.message)
    }

    @Test
    fun `object getFloat with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("string", -1f)
        }
        assertEquals("Value for key 'string' of type string cannot be read as Float", exception.message)
    }

    @Test
    fun `object optFloat returns null for non-numeric string`() {
        assertNull(jsonObject.optFloat("string"))
    }

    @Test
    fun `object optFloat with fallback returns fallback for non-numeric string`() {
        assertEquals(-1f, jsonObject.optFloat("string", -1f), 0f)
    }

    @Test
    fun `object getFloat throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("boolean")
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Float", exception.message)
    }

    @Test
    fun `object getFloat with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("boolean", -1f)
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Float", exception.message)
    }

    @Test
    fun `object optFloat returns null for boolean value`() {
        assertNull(jsonObject.optFloat("boolean"))
    }

    @Test
    fun `object optFloat with fallback returns fallback for boolean value`() {
        assertEquals(-1f, jsonObject.optFloat("boolean", -1f), 0f)
    }

    @Test
    fun `object getFloat throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("object")
        }
        assertEquals("Value for key 'object' of type object cannot be read as Float", exception.message)
    }

    @Test
    fun `object getFloat with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("object", -1f)
        }
        assertEquals("Value for key 'object' of type object cannot be read as Float", exception.message)
    }

    @Test
    fun `object optFloat returns null for object value`() {
        assertNull(jsonObject.optFloat("object"))
    }

    @Test
    fun `object optFloat with fallback returns fallback for object value`() {
        assertEquals(-1f, jsonObject.optFloat("object", -1f), 0f)
    }

    @Test
    fun `object getFloat throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("array")
        }
        assertEquals("Value for key 'array' of type array cannot be read as Float", exception.message)
    }

    @Test
    fun `object getFloat with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getFloat("array", -1f)
        }
        assertEquals("Value for key 'array' of type array cannot be read as Float", exception.message)
    }

    @Test
    fun `object optFloat returns null for array value`() {
        assertNull(jsonObject.optFloat("array"))
    }

    @Test
    fun `object optFloat with fallback returns fallback for array value`() {
        assertEquals(-1f, jsonObject.optFloat("array", -1f), 0f)
    }

    @Test
    fun `object getDouble throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("string")
        }
        assertEquals("Value for key 'string' of type string cannot be read as Double", exception.message)
    }

    @Test
    fun `object getDouble with fallback throws for non-numeric string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("string", -1.0)
        }
        assertEquals("Value for key 'string' of type string cannot be read as Double", exception.message)
    }

    @Test
    fun `object optDouble returns null for non-numeric string`() {
        assertNull(jsonObject.optDouble("string"))
    }

    @Test
    fun `object optDouble with fallback returns fallback for non-numeric string`() {
        assertEquals(-1.0, jsonObject.optDouble("string", -1.0), 0.0)
    }

    @Test
    fun `object getDouble throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("boolean")
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Double", exception.message)
    }

    @Test
    fun `object getDouble with fallback throws for boolean value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("boolean", -1.0)
        }
        assertEquals("Value for key 'boolean' of type boolean cannot be read as Double", exception.message)
    }

    @Test
    fun `object optDouble returns null for boolean value`() {
        assertNull(jsonObject.optDouble("boolean"))
    }

    @Test
    fun `object optDouble with fallback returns fallback for boolean value`() {
        assertEquals(-1.0, jsonObject.optDouble("boolean", -1.0), 0.0)
    }

    @Test
    fun `object getDouble throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("object")
        }
        assertEquals("Value for key 'object' of type object cannot be read as Double", exception.message)
    }

    @Test
    fun `object getDouble with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("object", -1.0)
        }
        assertEquals("Value for key 'object' of type object cannot be read as Double", exception.message)
    }

    @Test
    fun `object optDouble returns null for object value`() {
        assertNull(jsonObject.optDouble("object"))
    }

    @Test
    fun `object optDouble with fallback returns fallback for object value`() {
        assertEquals(-1.0, jsonObject.optDouble("object", -1.0), 0.0)
    }

    @Test
    fun `object getDouble throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("array")
        }
        assertEquals("Value for key 'array' of type array cannot be read as Double", exception.message)
    }

    @Test
    fun `object getDouble with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getDouble("array", -1.0)
        }
        assertEquals("Value for key 'array' of type array cannot be read as Double", exception.message)
    }

    @Test
    fun `object optDouble returns null for array value`() {
        assertNull(jsonObject.optDouble("array"))
    }

    @Test
    fun `object optDouble with fallback returns fallback for array value`() {
        assertEquals(-1.0, jsonObject.optDouble("array", -1.0), 0.0)
    }

    @Test
    fun `object getBoolean throws for uppercase boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("uppercase_boolean_string")
        }
        assertEquals("Value for key 'uppercase_boolean_string' of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object getBoolean with fallback throws for uppercase boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("uppercase_boolean_string", true)
        }
        assertEquals("Value for key 'uppercase_boolean_string' of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object optBoolean returns null for uppercase boolean string`() {
        assertNull(jsonObject.optBoolean("uppercase_boolean_string"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for uppercase boolean string`() {
        assertEquals(true, jsonObject.optBoolean("uppercase_boolean_string", true))
    }

    @Test
    fun `object getBoolean throws for mixed-case boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("mixed_case_boolean_string")
        }
        assertEquals("Value for key 'mixed_case_boolean_string' of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object getBoolean with fallback throws for mixed-case boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("mixed_case_boolean_string", true)
        }
        assertEquals("Value for key 'mixed_case_boolean_string' of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object optBoolean returns null for mixed-case boolean string`() {
        assertNull(jsonObject.optBoolean("mixed_case_boolean_string"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for mixed-case boolean string`() {
        assertEquals(true, jsonObject.optBoolean("mixed_case_boolean_string", true))
    }

    @Test
    fun `object getBoolean throws for short integer number value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("short_integer_number")
        }
        assertEquals("Value for key 'short_integer_number' of type number cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object getBoolean with fallback throws for short integer number value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("short_integer_number", true)
        }
        assertEquals("Value for key 'short_integer_number' of type number cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object optBoolean returns null for short integer number value`() {
        assertNull(jsonObject.optBoolean("short_integer_number"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for short integer number value`() {
        assertEquals(true, jsonObject.optBoolean("short_integer_number", true))
    }

    @Test
    fun `object getBoolean throws for non-boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("string")
        }
        assertEquals("Value for key 'string' of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object getBoolean with fallback throws for non-boolean string`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("string", true)
        }
        assertEquals("Value for key 'string' of type string cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object optBoolean returns null for non-boolean string`() {
        assertNull(jsonObject.optBoolean("string"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for non-boolean string`() {
        assertEquals(true, jsonObject.optBoolean("string", true))
    }

    @Test
    fun `object getBoolean throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("object")
        }
        assertEquals("Value for key 'object' of type object cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object getBoolean with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("object", true)
        }
        assertEquals("Value for key 'object' of type object cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object optBoolean returns null for object value`() {
        assertNull(jsonObject.optBoolean("object"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for object value`() {
        assertEquals(true, jsonObject.optBoolean("object", true))
    }

    @Test
    fun `object getBoolean throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("array")
        }
        assertEquals("Value for key 'array' of type array cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object getBoolean with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getBoolean("array", true)
        }
        assertEquals("Value for key 'array' of type array cannot be read as Boolean", exception.message)
    }

    @Test
    fun `object optBoolean returns null for array value`() {
        assertNull(jsonObject.optBoolean("array"))
    }

    @Test
    fun `object optBoolean with fallback returns fallback for array value`() {
        assertEquals(true, jsonObject.optBoolean("array", true))
    }

    @Test
    fun `object getString throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getString("object")
        }
        assertEquals("Value for key 'object' of type object cannot be read as String", exception.message)
    }

    @Test
    fun `object getString with fallback throws for object value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getString("object", "fallback")
        }
        assertEquals("Value for key 'object' of type object cannot be read as String", exception.message)
    }

    @Test
    fun `object optString returns null for object value`() {
        assertNull(jsonObject.optString("object"))
    }

    @Test
    fun `object optString with fallback returns fallback for object value`() {
        assertEquals("fallback", jsonObject.optString("object", "fallback"))
    }

    @Test
    fun `object getString throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getString("array")
        }
        assertEquals("Value for key 'array' of type array cannot be read as String", exception.message)
    }

    @Test
    fun `object getString with fallback throws for array value`() {
        val exception = assertThrows(ClassCastException::class.java) {
            jsonObject.getString("array", "fallback")
        }
        assertEquals("Value for key 'array' of type array cannot be read as String", exception.message)
    }

    @Test
    fun `object optString returns null for array value`() {
        assertNull(jsonObject.optString("array"))
    }

    @Test
    fun `object optString with fallback returns fallback for array value`() {
        assertEquals("fallback", jsonObject.optString("array", "fallback"))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<JsonBackend> = JsonBackend.entries
    }
}
