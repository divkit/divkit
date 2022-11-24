package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PropertyTest {

    @Test
    fun `required property with value`() {
        val actual = parse(filename = "test_property_not_templated.json")
        assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }

    @Test
    fun `required property with value in template`() {
        val actual = parse(filename = "test_property_without_link.json")
        assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `required property with invalid value in template`() {
        parse(filename = "test_property_without_link_invalid.json")
    }

    @Test
    fun `required property with link`() {
        val actual = parse(filename = "test_property_with_link.json")
        assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `required property with link to link`() {
        parse(filename = "test_property_with_link_to_link.json")

        // expected correct value, but not supported yet
        // assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `required property with link value in template`() {
        parse(filename = "test_property_with_link_value_in_template.json")

        // expected correct value, but not supported yet
        // assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `required property with link without value`() {
        parse(filename = "test_property_with_link_missing_data.json")
    }

    @Test(expected = ParsingException::class)
    fun `required property without value`() {
        parse(filename = "test_property_without_link_missing_data.json")
    }

    @Test
    fun `required property with overridden value`() {
        val actual = parse(filename = "test_property_with_override.json")
        assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }

    @Test
    fun `required property with overridden value in template`() {
        val actual = parse(filename = "test_property_with_override_in_template.json")
        assertEquals(ENTITY_WITH_REQUIRED_PROPERTY, actual)
    }
}

private fun parse(filename: String): Entity? {
    return EntityTemplateTestCase(ctor = Entity.Companion::invoke)
        .parse(directory = "property", filename = filename)
}
