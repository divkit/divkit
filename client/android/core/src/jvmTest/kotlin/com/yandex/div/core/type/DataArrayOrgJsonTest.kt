package com.yandex.div.core.type

import com.yandex.div.core.json.asJsonArray
import org.json.JSONArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DataArrayOrgJsonTest {

    @Test
    fun `get returns nested JSON array as is`() {
        val nested = JSONArray().asJsonArray()

        val array = DataArray.of(listOf(nested))

        assertSame(nested, array.get(0))
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
}
