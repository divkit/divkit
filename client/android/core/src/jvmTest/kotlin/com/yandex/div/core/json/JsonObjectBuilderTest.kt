package com.yandex.div.core.json

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.internal.FormatLanguage
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlinx.serialization.json.Json as KtxJson
import kotlinx.serialization.json.JsonElement as KtxElement

@RunWith(Parameterized::class)
class JsonObjectBuilderTest(private val backend: JsonBackend) {

    @Test
    fun `object put adds int value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", 65536) }

        assertEquals(json("""{"value":65536}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces int value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", 65536) }

        assertEquals(json("""{"value":65536,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds long value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", 1099511627776L) }

        assertEquals(json("""{"value":1099511627776}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces long value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", 1099511627776L) }

        assertEquals(json("""{"value":1099511627776,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds float value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", 3.1415927f) }

        assertEquals(json("""{"value":3.1415927}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces float value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", 3.1415927f) }

        assertEquals(json("""{"value":3.1415927,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds double value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", 3.141592653589793) }

        assertEquals(json("""{"value":3.141592653589793}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces double value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", 3.141592653589793) }

        assertEquals(json("""{"value":3.141592653589793,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds boolean value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", true) }

        assertEquals(json("""{"value":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces boolean value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", true) }

        assertEquals(json("""{"value":true,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds string value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", "Lorem ipsum") }

        assertEquals(json("""{"value":"Lorem ipsum"}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces string value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", "Lorem ipsum") }

        assertEquals(json("""{"value":"Lorem ipsum","untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds org json object value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", OrgJsonObject(JSONObject("""{"nested":"value"}"""))) }

        assertEquals(json("""{"value":{"nested":"value"}}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces org json object value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", OrgJsonObject(JSONObject("""{"nested":"value"}"""))) }

        assertEquals(json("""{"value":{"nested":"value"},"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds ktx json object value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", KtxJsonObject(json("""{"nested":"value"}""").jsonObject)) }

        assertEquals(json("""{"value":{"nested":"value"}}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces ktx json object value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", KtxJsonObject(json("""{"nested":"value"}""").jsonObject)) }

        assertEquals(json("""{"value":{"nested":"value"},"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds org json array value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", OrgJsonArray(JSONArray("[65536]"))) }

        assertEquals(json("""{"value":[65536]}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces org json array value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", OrgJsonArray(JSONArray("[65536]"))) }

        assertEquals(json("""{"value":[65536],"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put adds ktx json array value`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", KtxJsonArray(json("[65536]").jsonArray)) }

        assertEquals(json("""{"value":[65536]}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put replaces ktx json array value`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val modified = source.modify { put("value", KtxJsonArray(json("[65536]").jsonArray)) }

        assertEquals(json("""{"value":[65536],"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object remove removes existing value`() {
        val source = createObject("""{"value":65536,"untouched":true}""")
        val modified = source.modify { remove("value") }

        assertEquals(json("""{"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object remove removes json null`() {
        val source = createObject("""{"value":null,"untouched":true}""")
        val modified = source.modify { remove("value") }

        assertEquals(json("""{"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object remove ignores missing key`() {
        val source = createObject("""{"untouched":true}""")
        val modified = source.modify { remove("value") }

        assertEquals(json("""{"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put new value preserves source`() {
        val source = createObject(JSON_OBJECT)
        source.modify { put("added", 7) }

        assertEquals(json(JSON_OBJECT), source.asKtxJsonObject())
    }

    @Test
    fun `object put replacement preserves source`() {
        val source = createObject(JSON_OBJECT)
        source.modify { put("short_integer_number", 7) }

        assertEquals(json(JSON_OBJECT), source.asKtxJsonObject())
    }

    @Test
    fun `object remove preserves source`() {
        val source = createObject(JSON_OBJECT)
        source.modify { remove("short_integer_number") }

        assertEquals(json(JSON_OBJECT), source.asKtxJsonObject())
    }

    @Test
    fun `object put writes string through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = "Lorem ipsum"
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":"Lorem ipsum","untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes boolean through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = true
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":true,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes int through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = 65536
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":65536,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes long through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = 1099511627776L
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":1099511627776,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes float through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = 3.1415927f
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":3.1415927,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes double through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = 3.141592653589793
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":3.141592653589793,"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes json object through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = KtxJsonObject(json("""{"nested":"value"}""").jsonObject)
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":{"nested":"value"},"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put writes json array through Any overload`() {
        val source = createObject("""{"value":"old","untouched":true}""")
        val value: Any = OrgJsonArray(JSONArray("[65536]"))
        val modified = source.modify { put("value", value) }

        assertEquals(json("""{"value":[65536],"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put null does not add missing key`() {
        val source = createObject("""{}""")
        val modified = source.modify { put("value", null) }

        assertEquals(json("""{}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put null removes existing value`() {
        val source = createObject("""{"value":65536,"untouched":true}""")
        val modified = source.modify { put("value", null) }

        assertEquals(json("""{"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put null removes existing json null`() {
        val source = createObject("""{"value":null,"untouched":true}""")
        val modified = source.modify { put("value", null) }

        assertEquals(json("""{"untouched":true}"""), modified.asKtxJsonObject())
    }

    @Test
    fun `object put null preserves source`() {
        val source = createObject(JSON_OBJECT)
        source.modify { put("short_integer_number", null) }

        assertEquals(json(JSON_OBJECT), source.asKtxJsonObject())
    }

    private fun createObject(source: String): JsonObject = when (backend) {
        JsonBackend.ORG_JSON -> OrgJsonObject(JSONObject(source))
        JsonBackend.KTX_JSON -> KtxJsonObject(json(source).jsonObject)
    }

    @OptIn(InternalSerializationApi::class)
    private fun json(@FormatLanguage("json", "", "")jsonString: String): KtxElement {
        return KtxJson.parseToJsonElement(jsonString)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<JsonBackend> = JsonBackend.entries
    }
}
