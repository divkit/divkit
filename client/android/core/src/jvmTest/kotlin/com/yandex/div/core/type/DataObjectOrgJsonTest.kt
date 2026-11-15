package com.yandex.div.core.type

import com.yandex.div.core.json.asJsonObject
import org.json.JSONObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DataObjectOrgJsonTest {

    @Test
    fun `get returns nested JSON object as is`() {
        val nested = JSONObject().asJsonObject()

        val obj = DataObject.of(mapOf("nested" to nested))

        assertSame(nested, obj.get("nested"))
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
}
