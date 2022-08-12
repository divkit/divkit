package com.yandex.div.json

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonUtilsTest {

    @Test
    fun `getStringOrEmpty() gets string value`() {
        val json = JSONObject("""{ "key": "value" }""")
        assertEquals("value", json.getStringOrEmpty("key"))
    }

    @Test
    fun `getStringOrEmpty() is empty for int value`() {
        val json = JSONObject("""{ "key": 123 }""")
        assertEquals("", json.getStringOrEmpty("key"))
    }

    @Test
    fun `getStringOrEmpty() is empty for null value`() {
        val json = JSONObject("""{ "key": null }""")
        assertEquals("", json.getStringOrEmpty("key"))
    }

    @Test
    fun `getStringOrEmpty() is empty for object value`() {
        val json = JSONObject("""{ "key": { "inner_key": "value" } }""")
        assertEquals("", json.getStringOrEmpty("key"))
    }

    @Test
    fun `getStringOrEmpty() is empty for missing key`() {
        val json = JSONObject("""{ }""")
        assertEquals("", json.getStringOrEmpty("key"))
    }
}
