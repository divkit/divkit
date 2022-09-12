package com.yandex.generator

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithComplexPropertyWithDefaultValue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithComplexPropertyWithDefaultValueTest {

    private val case = EntityTestCase(
        ctor = EntityWithComplexPropertyWithDefaultValue.Companion::invoke,
    )

    @Test
    fun `property value is set`() {
        val json = """{
            "property": {
                "value": "Some text"
            }
        }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals("Some text", entity.property.value.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `null property value resolves as default value`() {
        val json = """{ }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals("Default text", entity.property.value.evaluate(ExpressionResolver.EMPTY))
    }
}
