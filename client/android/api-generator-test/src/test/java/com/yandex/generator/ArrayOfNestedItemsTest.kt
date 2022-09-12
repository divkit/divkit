package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.generator.ENTITY_WITH_ARRAY_OF_NESTED_ITEMS
import com.yandex.generator.EntityTemplateTestCase
import com.yandex.testing.Entity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArrayOfNestedItemsTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `array of nested items is parsed correctly`() {
        val actual = case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_without_templates.json"
        )
        Assert.assertEquals(ENTITY_WITH_ARRAY_OF_NESTED_ITEMS, actual)
    }

    @Test
    fun `array of nested item templates is parsed correctly`() {
        val actual = case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_with_item_template.json"
        )
        Assert.assertEquals(ENTITY_WITH_ARRAY_OF_NESTED_ITEMS, actual)
    }

    @Test
    fun `referenced array of nested items is parsed correctly`() {
        val actual = case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_with_link_for_items.json"
        )
        Assert.assertEquals(ENTITY_WITH_ARRAY_OF_NESTED_ITEMS, actual)
    }

    @Test
    fun `referenced array of nested item templates is parsed correctly`() {
        val actual = case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_with_link_for_items_with_item_template.json"
        )
        Assert.assertEquals(ENTITY_WITH_ARRAY_OF_NESTED_ITEMS, actual)
    }

    @Test(expected = ParsingException::class)
    fun `when data for nested item property references is missing parsing fails`() {
        case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_with_item_template_without_data.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when required property of nested item is missing parsing fails`() {
        case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_without_item_required_property.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when required property of nested item template is missing parsing fails`() {
        case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_with_item_template_without_item_required_property.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when referenced array contains nested items without required property parsing fails`() {
        case.parse(
            directory = "array_of_nested_items",
            filename = "test_array_of_nested_items_with_link_for_items_without_item_required_property.json"
        )
    }
}
