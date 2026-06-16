package com.yandex.div.internal.extensions

import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.doubleExpression
import org.json.JSONArray
import org.json.JSONObject
import kotlin.test.Test
import kotlin.test.assertEquals

class ShimmerExtensionParamsTest {

    @Test
    fun `fromJson() returns default params for empty json`() {
        assertEquals(
            ShimmerExtensionParams(),
            ShimmerExtensionParams.fromJson(JSONObject())
        )
    }

    @Test
    fun `fromJson() parses all params`() {
        val params = ShimmerExtensionParams.fromJson(
            JSONObject()
                .put("angle", 123.45)
                .put("duration", "@{duration}")
                .put("locations", JSONArray(listOf(0.1, 0.5, 0.8)))
                .put(
                    "corner_radius",
                    JSONObject()
                        .put("bottom-left", 10)
                        .put("bottom-right", 20)
                        .put("top-left", 30)
                        .put("top-right", 40)
                )
        )

        assertEquals(constant(123.45), params.angle)
        assertEquals(doubleExpression("@{duration}"), params.duration)
        assertEquals(ConstantExpressionList(listOf(0.1, 0.5, 0.8)), params.locations)
        assertEquals(constant(10L), params.cornerRadius?.bottomLeft)
        assertEquals(constant(20L), params.cornerRadius?.bottomRight)
        assertEquals(constant(30L), params.cornerRadius?.topLeft)
        assertEquals(constant(40L), params.cornerRadius?.topRight)
    }
}
