package com.yandex.generator

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithOptionalStringEnumProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithOptionalStringEnumPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithOptionalStringEnumProperty.Companion::invoke
    )

    @Test
    fun `property value is set`() {
        val json = """{ "property": "second" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(EntityWithOptionalStringEnumProperty.Property.SECOND,
            entity.property?.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `null property value`() {
        val json = """{ }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertNull(entity.property)
    }
}
