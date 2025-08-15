package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArrayTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `when array is defined by link it is parsed correctly`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_with_link.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test
    fun `when array does not contain link it is parsed correctly`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_without_link.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test
    fun `when array contains elements defined by links it is parsed correctly`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_with_internal_links.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test
    fun `when array contains links inside elements it is parsed correctly`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_with_complex_items_with_internal_links.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `array has minItems 1 and empty array received`() {
        case.parse(
            directory = "array",
            filename = "test_array_empty.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when array has all items invalid in template parsing fails`() {
        case.parse(
            directory = "array",
            filename = "test_array_invalid_items.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when array has all items invalid in data parsing fails`() {
        case.parse(
            directory = "array",
            filename = "test_array_invalid_items_in_data.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `when data is missing parsing fails`() {
        case.parse(
            directory = "array",
            filename = "test_array_invalid_items_in_data.json"
        )
    }

    @Test
    fun `when array has some invalid items they are skipped`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_one_invalid_item.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test
    fun `when array has some invalid links they are skipped`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_missing_one_link.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test
    fun `when array is not templated parsing succeeds`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_not_templated.json"
        )
        assertEquals(ENTITY_WITH_ARRAY, actual)
    }

    @Test
    fun `when array has heterogeneous items parsing succeeds`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_with_heterogeneous_items.json"
        )
        assertEquals(ENTITY_WITH_HETEROGENOUS_ARRAY, actual)
    }

    @Test
    fun `when array has heterogeneous items with internal links parsing succeeds`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_with_heterogeneous_items_with_internal_links.json"
        )
        assertEquals(ENTITY_WITH_HETEROGENOUS_ARRAY, actual)
    }

    @Test
    fun `when array has nested array parsing succeeds`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_nested.json"
        )
        assertEquals(ENTITY_WITH_NESTED_ARRAY, actual)
    }

    @Test
    fun `when array has nested array with links inside parsing succeeds`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_nested_with_internal_link.json"
        )
        assertEquals(ENTITY_WITH_NESTED_ARRAY, actual)
    }

    @Test
    fun `when array has nested templated array parsing succeeds`() {
        val actual = case.parse(
            directory = "array",
            filename = "test_array_nested_with_internal_link.json"
        )
        assertEquals(ENTITY_WITH_NESTED_ARRAY, actual)
    }
}
