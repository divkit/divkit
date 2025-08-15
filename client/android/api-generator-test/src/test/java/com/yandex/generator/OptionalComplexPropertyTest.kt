package com.yandex.generator

import com.yandex.generator.ENTITY_WITH_MISSING_OPTIONAL_COMPLEX_PROPERTY
import com.yandex.generator.ENTITY_WITH_OPTIONAL_COMPLEX_PROPERTY
import com.yandex.generator.EntityTemplateTestCase
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OptionalComplexPropertyTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `optional complex property with reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_with_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `optional complex property without reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_without_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `optional complex property with internal reference is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_with_internal_link.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `when data for optional complex property with reference is missing entity created`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_with_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `when data for optional complex property without reference is missing entity created`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_without_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `when data for optional complex property with internal reference is missing entity created`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_with_internal_link_missing_data.json"
        )
        assertEquals(ENTITY_WITH_MISSING_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `optional complex property without template is parsed correctly`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_complex_property_not_templated.json"
        )
        assertEquals(ENTITY_WITH_OPTIONAL_COMPLEX_PROPERTY, actual)
    }

    @Test
    fun `when optional complex property is invalid entity created`() {
        val actual = case.parse(
            directory = "optional_complex_property",
            filename = "test_optional_invalid_complex_property_not_templated.json"
        )
        assertEquals(ENTITY_WITH_MISSING_OPTIONAL_COMPLEX_PROPERTY, actual)
    }
}
