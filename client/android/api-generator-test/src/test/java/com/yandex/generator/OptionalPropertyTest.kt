package com.yandex.generator

import com.yandex.generator.ENTITY_WITH_MISSING_OPTIONAL_PROPERTY
import com.yandex.generator.ENTITY_WITH_OPTIONAL_PROPERTY
import com.yandex.generator.EntityTemplateTestCase
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OptionalPropertyTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `optional property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_property",
            filename = "test_optional_property_with_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_PROPERTY, actual)
    }

    @Test
    fun `optional property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_property",
            filename = "test_optional_property_without_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_PROPERTY, actual)
    }

    @Test
    fun `missing optional property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_property",
            filename = "test_optional_property_with_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_OPTIONAL_PROPERTY, actual)
    }

    @Test
    fun `missing optional property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_property",
            filename = "test_optional_property_without_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_OPTIONAL_PROPERTY, actual)
    }

    @Test
    fun `optional property with override is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_property",
            filename = "test_optional_property_with_override.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_PROPERTY, actual)
    }

    @Test
    fun `optional property without template is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_property",
            filename = "test_optional_property_not_templated.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_PROPERTY, actual)
    }
}
