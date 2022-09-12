package com.yandex.generator

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithOptionalProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithOptionalPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithOptionalProperty.Companion::invoke
    )

    @Test
    fun `property value is set`() {
        val json = """{ "property": "Some text" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals("Some text", entity.property!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `invalid value type`() {
        val json = """{ "value": [ "Some text" ] }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertNull(entity.property)
    }

    @Test(expected = AssertionError::class)
    fun `error in value is logged`() {
        val json = """{ "property": [ "Some text" ] }"""

        case.parse(ASSERT_ENVIRONMENT, json)
    }

    @Test
    fun `null property value`() {
        val json = """{ }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertNull(entity.property)
    }
}
