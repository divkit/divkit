package com.yandex.generator

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithStringEnumPropertyWithDefaultValue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithStringEnumPropertyWithDefaultValueTest {

    private val case = EntityTestCase(
        ctor = EntityWithStringEnumPropertyWithDefaultValue.Companion::invoke
    )

    @Test
    fun `property value is set`() {
        val json = """{ "value": "third" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(EntityWithStringEnumPropertyWithDefaultValue.Value.THIRD,
            entity.value.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `invalid value resolves as default value`() {
        val json = """{ "value": "no value" }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertEquals(EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND,
            entity.value.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `invalid value type resolves as default value`() {
        val json = """{ "value": [ "first" ] }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertEquals(EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND,
            entity.value.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `null value resolves as default value`() {
        val json = """{ }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND,
            entity.value.evaluate(ExpressionResolver.EMPTY))
    }
}
