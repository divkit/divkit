package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithArray
import com.yandex.testing.EntityWithRequiredProperty
import com.yandex.testing.EntityWithStringEnumPropertyWithDefaultValue
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithEntityArrayPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithArray.Companion::invoke
    )

    @Test
    fun `homogeneous array`() {
        val json = """{
            "array": [
                {
                    "type": "entity_with_required_property",
                    "property": "Some text 1"
                },
                {
                    "type": "entity_with_required_property",
                    "property": "Some text 2"
                }
             ]
        }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertThat(entity.array, hasSize(2))
        assertEquals("Some text 1", (entity.array[0].value() as EntityWithRequiredProperty).property.evaluate(ExpressionResolver.EMPTY))
        assertEquals("Some text 2", (entity.array[1].value() as EntityWithRequiredProperty).property.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `heterogeneous array`() {
        val json = """{
            "array": [
                {
                    "type": "entity_with_required_property",
                    "property": "Some text 1"
                },
                {
                    "type": "entity_with_string_enum_property_with_default_value"
                }
             ]
        }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertThat(entity.array, hasSize(2))
        assertEquals("Some text 1", (entity.array[0].value() as EntityWithRequiredProperty).property
            .evaluate(ExpressionResolver.EMPTY))
        assertEquals(
            EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND,
            (entity.array[1].value() as EntityWithStringEnumPropertyWithDefaultValue).value
                .evaluate(ExpressionResolver.EMPTY)
        )
    }

    @Test
    fun `invalid object in array`() {
        val json = """{
            "array": [
                {
                    "type": "entity_with_required_property",
                    "property": "Some text 1"
                },
                {
                    "type": "entity_with_required_property"
                },
                {
                    "type": "entity_with_required_property",
                    "property": "Some text 3"
                }
             ]
        }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertThat(entity.array, hasSize(2))
        assertEquals("Some text 1", (entity.array[0].value() as EntityWithRequiredProperty).property
            .evaluate(ExpressionResolver.EMPTY))
        assertEquals("Some text 3", (entity.array[1].value() as EntityWithRequiredProperty).property
            .evaluate(ExpressionResolver.EMPTY))
    }

    @Test(expected = ParsingException::class)
    fun `null property value`() {
        val json = """{ }"""

        case.parse(LOG_ENVIRONMENT, json)
    }
}
