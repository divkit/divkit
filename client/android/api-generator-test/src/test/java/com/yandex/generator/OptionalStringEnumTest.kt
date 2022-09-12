package com.yandex.generator

import com.yandex.generator.ENTITY_WITH_MISSING_STRING_ENUM_PROPERTY
import com.yandex.generator.ENTITY_WITH_OPTIONAL_STRING_ENUM_PROPERTY
import com.yandex.generator.EntityTemplateTestCase
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OptionalStringEnumTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `optional string enum property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_string_enum_property",
            filename = "test_optional_string_enum_property_with_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `optional string enum property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_string_enum_property",
            filename = "test_optional_string_enum_property_without_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `missing optional string enum property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_string_enum_property",
            filename = "test_optional_string_enum_property_with_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `missing optional string enum property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_string_enum_property",
            filename = "test_optional_string_enum_property_without_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `string optional enum property with override is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_string_enum_property",
            filename = "test_optional_string_enum_property_with_override.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `string optional enum property without template is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_string_enum_property",
            filename = "test_optional_string_enum_property_not_templated.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_STRING_ENUM_PROPERTY, actual)
    }
}
