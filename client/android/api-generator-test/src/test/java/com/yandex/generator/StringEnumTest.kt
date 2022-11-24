package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StringEnumTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `string enum property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "string_enum_property",
            filename = "test_string_enum_property_with_link.json"
        )
        assertEquals(ENTITY_WITH_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `string enum property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "string_enum_property",
            filename = "test_string_enum_property_without_link.json"
        )
        assertEquals(ENTITY_WITH_STRING_ENUM_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `missing string enum property with reference parsing fails`() {
        case.parse(
            directory = "string_enum_property",
            filename = "test_string_enum_property_with_link_missing_data.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `missing string enum property without reference parsing fails`() {
        case.parse(
            directory = "string_enum_property",
            filename = "test_string_enum_property_without_link_missing_data.json"
        )
    }

    @Test
    fun `string enum property with override is parsed correctly`() {
        val actual = case.parse(
            directory = "string_enum_property",
            filename = "test_string_enum_property_with_override.json"
        )
        assertEquals(ENTITY_WITH_STRING_ENUM_PROPERTY, actual)
    }

    @Test
    fun `string enum property without template is parsed correctly`() {
        val actual = case.parse(
            directory = "string_enum_property",
            filename = "test_string_enum_property_not_templated.json"
        )
        assertEquals(ENTITY_WITH_STRING_ENUM_PROPERTY, actual)
    }
}
