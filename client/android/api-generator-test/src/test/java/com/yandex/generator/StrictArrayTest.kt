package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.generator.ENTITY_WITH_STRICT_ARRAY
import com.yandex.generator.EntityTemplateTestCase
import com.yandex.testing.Entity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StrictArrayTest {

    private val case = EntityTemplateTestCase(
        ctor = Entity.Companion::invoke
    )

    @Test
    fun `strict array is parsed correctly`() {
        val actual = case.parse(
            directory = "strict_array",
            filename = "test_strict_array_happy_case.json"
        )
        assertEquals(ENTITY_WITH_STRICT_ARRAY, actual)
    }

    @Test
    fun `referenced strict array is parsed correctly`() {
        val actual = case.parse(
            directory = "strict_array",
            filename = "test_strict_array_happy_case_referenced.json"
        )
        assertEquals(ENTITY_WITH_STRICT_ARRAY, actual)
    }

    @Test(expected = ParsingException::class)
    fun `strict array with non-object item parsing failed`() {
        case.parse(
            directory = "strict_array",
            filename = "test_strict_array_with_non_object_item.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `referenced strict array with non-object item parsing failed`() {
        case.parse(
            directory = "strict_array",
            filename = "test_referenced_strict_array_with_non_object_item.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `strict array with non-resolvable item parsing failed`() {
        case.parse(
            directory = "strict_array",
            filename = "test_strict_array_with_non_resolvable_item.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `referenced strict array with non-resolvable item parsing failed`() {
        case.parse(
            directory = "strict_array",
            filename = "test_referenced_strict_array_with_non_resolvable_item.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `strict array with non-object item reference parsing failed`() {
        case.parse(
            directory = "strict_array",
            filename = "test_strict_array_with_non_object_item_ref.json"
        )
    }

    @Test(expected = ParsingException::class)
    fun `strict array with non-resolvable item reference parsing failed`() {
        case.parse(
            directory = "strict_array",
            filename = "test_strict_array_with_non_resolvable_item_ref.json"
        )
    }
}
