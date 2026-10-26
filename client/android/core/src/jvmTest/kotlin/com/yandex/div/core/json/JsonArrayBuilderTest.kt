package com.yandex.div.core.json

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.internal.FormatLanguage
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlinx.serialization.json.Json as KtxJson
import kotlinx.serialization.json.JsonElement as KtxElement

@RunWith(Parameterized::class)
class JsonArrayBuilderTest(private val backend: JsonBackend) {

    @Test
    fun `array add writes int value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(65536) }

        assertEquals(json("""[false,65536]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes long value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(1099511627776L) }

        assertEquals(json("""[false,1099511627776]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes float value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(3.1415927f) }

        assertEquals(json("""[false,3.1415927]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes double value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(3.141592653589793) }

        assertEquals(json("""[false,3.141592653589793]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes boolean value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(true) }

        assertEquals(json("""[false,true]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes string value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add("Lorem ipsum") }

        assertEquals(json("""[false,"Lorem ipsum"]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes org json object value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(OrgJsonObject(JSONObject("""{"nested":"value"}"""))) }

        assertEquals(json("""[false,{"nested":"value"}]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes ktx json object value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(KtxJsonObject(json("""{"nested":"value"}""").jsonObject)) }

        assertEquals(json("""[false,{"nested":"value"}]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes org json array value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(OrgJsonArray(JSONArray("[65536]"))) }

        assertEquals(json("""[false,[65536]]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes ktx json array value`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(KtxJsonArray(json("[65536]").jsonArray)) }

        assertEquals(json("""[false,[65536]]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes int value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, 65536) }

        assertEquals(json("""[65536,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes long value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, 1099511627776L) }

        assertEquals(json("""[1099511627776,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes float value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, 3.1415927f) }

        assertEquals(json("""[3.1415927,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes double value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, 3.141592653589793) }

        assertEquals(json("""[3.141592653589793,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes boolean value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, true) }

        assertEquals(json("""[true,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes string value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, "Lorem ipsum") }

        assertEquals(json("""["Lorem ipsum",false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes org json object value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, OrgJsonObject(JSONObject("""{"nested":"value"}"""))) }

        assertEquals(json("""[{"nested":"value"},false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes ktx json object value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, KtxJsonObject(json("""{"nested":"value"}""").jsonObject)) }

        assertEquals(json("""[{"nested":"value"},false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes org json array value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, OrgJsonArray(JSONArray("[65536]"))) }

        assertEquals(json("""[[65536],false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes ktx json array value`() {
        val source = createArray("""["old",false]""")
        val modified = source.modify { put(0, KtxJsonArray(json("[65536]").jsonArray)) }

        assertEquals(json("""[[65536],false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put appends at size`() {
        val source = createArray("""[65536]""")
        val modified = source.modify { put(source.size, 7) }

        assertEquals(json("""[65536,7]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put pads skipped indices with null`() {
        val source = createArray("""[65536]""")
        val modified = source.modify { put(source.size + 2, 7) }

        assertEquals(json("""[65536,null,null,7]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put pads empty array with null`() {
        val source = createArray("""[]""")
        val modified = source.modify { put(2, 7) }

        assertEquals(json("""[null,null,7]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array remove removes first value`() {
        val source = createArray("""[1,2,3]""")
        val modified = source.modify { remove(0) }

        assertEquals(json("""[2,3]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array remove removes middle value`() {
        val source = createArray("""[1,2,3]""")
        val modified = source.modify { remove(1) }

        assertEquals(json("""[1,3]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array remove removes last value`() {
        val source = createArray("""[1,2,3]""")
        val modified = source.modify { remove(2) }

        assertEquals(json("""[1,2]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array remove removes json null`() {
        val source = createArray("""[1,null,3]""")
        val modified = source.modify { remove(1) }

        assertEquals(json("""[1,3]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put rejects negative index`() {
        val source = createArray("[65536]")

        assertThrows(IllegalArgumentException::class.java) {
            source.modify { put(-1, 7) }
        }
    }

    @Test
    fun `array add preserves source`() {
        val source = createArray(JSON_ARRAY)
        source.modify { add(7) }

        assertEquals(json(JSON_ARRAY), source.asKtxJsonArray())
    }

    @Test
    fun `array put preserves source`() {
        val source = createArray(JSON_ARRAY)
        source.modify { put(0, 7) }

        assertEquals(json(JSON_ARRAY), source.asKtxJsonArray())
    }

    @Test
    fun `array remove preserves source`() {
        val source = createArray(JSON_ARRAY)
        source.modify { remove(0) }

        assertEquals(json(JSON_ARRAY), source.asKtxJsonArray())
    }

    @Test
    fun `array add writes int through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = 65536
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,65536]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes long through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = 1099511627776L
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,1099511627776]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes float through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = 3.1415927f
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,3.1415927]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes double through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = 3.141592653589793
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,3.141592653589793]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes boolean through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = true
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,true]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes string through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = "Lorem ipsum"
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,"Lorem ipsum"]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes json object through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = KtxJsonObject(json("""{"nested":"value"}""").jsonObject)
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,{"nested":"value"}]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add writes json array through Any overload`() {
        val source = createArray("""[false]""")
        val value: Any = OrgJsonArray(JSONArray("[65536]"))
        val modified = source.modify { add(value) }

        assertEquals(json("""[false,[65536]]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes int through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = 65536
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[65536,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes long through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = 1099511627776L
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[1099511627776,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes float through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = 3.1415927f
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[3.1415927,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes double through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = 3.141592653589793
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[3.141592653589793,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes boolean through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = true
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[true,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes string through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = "Lorem ipsum"
        val modified = source.modify { put(0, value) }

        assertEquals(json("""["Lorem ipsum",false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes json object through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = KtxJsonObject(json("""{"nested":"value"}""").jsonObject)
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[{"nested":"value"},false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put writes JsonArray through Any overload`() {
        val source = createArray("""["old",false]""")
        val value: Any = OrgJsonArray(JSONArray("[65536]"))
        val modified = source.modify { put(0, value) }

        assertEquals(json("""[[65536],false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array add null writes json null`() {
        val source = createArray("""[false]""")
        val modified = source.modify { add(null) }

        assertEquals(json("""[false,null]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put null replaces value with json null`() {
        val source = createArray("""[65536,false]""")
        val modified = source.modify { put(0, null) }

        assertEquals(json("""[null,false]"""), modified.asKtxJsonArray())
    }

    @Test
    fun `array put null pads skipped indices with json null`() {
        val source = createArray("""[65536]""")
        val modified = source.modify { put(source.size + 1, null) }

        assertEquals(json("""[65536,null,null]"""), modified.asKtxJsonArray())
    }

    private fun createArray(source: String): JsonArray = when (backend) {
        JsonBackend.ORG_JSON -> OrgJsonArray(JSONArray(source))
        JsonBackend.KTX_JSON -> KtxJsonArray(json(source).jsonArray)
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
