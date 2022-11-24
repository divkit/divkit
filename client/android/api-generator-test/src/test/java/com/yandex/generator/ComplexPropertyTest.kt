package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ComplexPropertyTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `required complex property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "complex_property",
            filename = "test_complex_property_with_link.json"
        )
        assertEquals(ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `required complex property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "complex_property",
            filename = "test_complex_property_without_link.json"
        )
        assertEquals(ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `required complex property with internal reference is parsed correctly`() {
        val actual = case.parse(
            directory = "complex_property",
            filename = "test_complex_property_with_internal_link.json"
        )
        assertEquals(ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `when data for required complex property with reference is missing parsing fails`() {
        case.parse(
            directory = "complex_property",
            filename = "test_complex_property_with_link_missing_data.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when data for required complex property without reference is missing parsing fails`() {
        case.parse(
            directory = "complex_property",
            filename = "test_complex_property_without_link_missing_data.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when data for required complex property with internal reference is missing parsing fails`() {
        case.parse(
            directory = "complex_property",
            filename = "test_complex_property_with_internal_link_missing_data.json"
        )
    }

    @Test
    fun `required complex property without template is parsed correctly`() {
        val actual = case.parse(
            directory = "complex_property",
            filename = "test_complex_property_not_templated.json"
        )
        assertEquals(ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `when required complex property is invalid parsing fails`() {
        case.parse(
            directory = "complex_property",
            filename = "test_invalid_complex_property_not_templated.json"
        )
    }
}
