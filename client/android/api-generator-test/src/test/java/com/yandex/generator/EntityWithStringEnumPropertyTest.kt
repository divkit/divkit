package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithStringEnumProperty
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithStringEnumPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithStringEnumProperty.Companion::invoke
    )

    @Test
    fun `property value is set`() {
        val json = """{ "property": "second" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(EntityWithStringEnumProperty.Property.SECOND,
            entity.property.evaluate(ExpressionResolver.EMPTY))
    }

    @Test(expected = ParsingException::class)
    fun `null property value`() {
        val json = """{ }"""

        case.parse(LOG_ENVIRONMENT, json)
    }
}
