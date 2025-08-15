package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithRequiredProperty
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithRequiredPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithRequiredProperty.Companion::invoke
    )

    @Test
    fun `property value is set`() {
        val json = """{ "property": "Some text" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals("Some text", entity.property.evaluate(ExpressionResolver.EMPTY))
    }

    @Test(expected = ParsingException::class)
    fun `invalid property type`() {
        val json = """{ "property": [ "Some text" ] }"""

        case.parse(LOG_ENVIRONMENT, json)
    }

    @Test(expected = ParsingException::class)
    fun `null property value`() {
        val json = """{ }"""

        case.parse(LOG_ENVIRONMENT, json)
    }
}
