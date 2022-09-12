package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArrayWithTransformTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `invalid item with transform skipped`() {
        val actual = case.parse(
            directory = "array_with_transform",
            filename = "test_array_with_transform_not_templated_one_invalid_item.json"
        )
        assertEquals(ENTITY_WITH_ARRAY_WITH_TRANSFORM, actual)
    }

    @Test
    fun `invalid templated item with transform skipped`() {
        val actual = case.parse(
            directory = "array_with_transform",
            filename = "test_array_with_transform_one_invalid_item.json"
        )
        assertEquals(ENTITY_WITH_ARRAY_WITH_TRANSFORM, actual)
    }

    @Test(expected = ParsingException::class)
    fun `array parsing failed when all items is invalid`() {
        case.parse(
            directory = "array_with_transform",
            filename = "test_array_with_transform_not_templated_invalid_items.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `array parsing failed when all templated items is invalid`() {
        case.parse(
            directory = "array_with_transform",
            filename = "test_array_with_transform_invalid_items.json"
        )
    }
}
