package com.yandex.generator

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithEntityProperty
import com.yandex.testing.EntityWithRequiredProperty
import com.yandex.testing.EntityWithStringEnumProperty
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithEntityPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithEntityProperty.Companion::invoke
    )

    @Test
    fun `property value is set`() {
        val json = """{
            "entity": {
                "type": "entity_with_required_property",
                "property": "Some text"
            }
        }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals("Some text", (entity.entity.value() as EntityWithRequiredProperty).property
            .evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `invalid value resoles as default value`() {
        val json = """{
            "entity": {
                "type": "entity_with_required_property"
            }
        }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertEquals(
            EntityWithStringEnumProperty.Property.SECOND,
            (entity.entity.value() as EntityWithStringEnumProperty).property
                .evaluate(ExpressionResolver.EMPTY)
        )
    }

    @Test
    fun `null value resolves as default value`() {
        val json = """{ }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(
            EntityWithStringEnumProperty.Property.SECOND,
            (entity.entity.value() as EntityWithStringEnumProperty).property
                .evaluate(ExpressionResolver.EMPTY)
        )
    }
}
