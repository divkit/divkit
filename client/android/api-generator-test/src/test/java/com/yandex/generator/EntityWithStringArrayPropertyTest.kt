package com.yandex.generator

import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithStringArrayProperty
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithStringArrayPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithStringArrayProperty.Companion::invoke
    )

    @Test
    fun `valid array`() {
        val json = """{
            "array": [
                "Some text 1",
                "Some text 2"
             ]
        }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)
        val array = entity.array.evaluate(ExpressionResolver.EMPTY)
        assertThat(array, hasSize(2))
        assertEquals("Some text 1", array[0])
        assertEquals("Some text 2", array[1])
    }

    @Test
    fun `invalid object in array`() {
        val json = """{
            "array": [
                "Some text 1",
                123,
                "Some text 3"
             ]
        }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)
        val array = entity.array.evaluate(ExpressionResolver.EMPTY)
        assertThat(array, hasSize(2))
        assertEquals("Some text 1", array[0])
        assertEquals("Some text 3", array[1])
    }

    @Test
    fun `array of expressions`() {
        val json = """{
            "array": [
                "@{expression_1}",
                "@{expression_2}",
                "@{expression_3}"
             ]
        }"""


        val entity = case.parse(LOG_ENVIRONMENT, json)
        val array = entity.array.evaluate(ExpressionResolver.EMPTY)
        assertThat(array, hasSize(3))

        val typeDefault = ""
        assertEquals(typeDefault, array[0])
        assertEquals(typeDefault, array[1])
        assertEquals(typeDefault, array[2])
    }

    @Test
    fun `mixed array from expressions and values`() {
        val json = """{
            "array": [
                "@{expression_1}",
                "Some Text 2",
                "@{expression_3}"
             ]
        }"""


        val entity = case.parse(LOG_ENVIRONMENT, json)
        val array = entity.array.evaluate(ExpressionResolver.EMPTY)
        assertThat(array, hasSize(3))

        val typeDefault = ""
        assertEquals(typeDefault, array[0])
        assertEquals("Some Text 2", array[1])
        assertEquals(typeDefault, array[2])
    }

    @Test(expected = ParsingException::class)
    fun `null property value`() {
        val json = """{ }"""

        case.parse(LOG_ENVIRONMENT, json)
    }
}
