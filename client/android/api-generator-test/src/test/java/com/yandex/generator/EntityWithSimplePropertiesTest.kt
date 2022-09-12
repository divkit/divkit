package com.yandex.generator

import android.graphics.Color
import android.net.Uri
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithSimpleProperties
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithSimplePropertiesTest {

    private val case = EntityTestCase(
        ctor = EntityWithSimpleProperties.Companion::invoke
    )

    @Test
    fun `string value`() {
        val json = """{ "string": "Some text" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals("Some text", entity.string!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `integer value`() {
        val json = """{ "integer": 20 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(20, entity.integer!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `double value as integer`() {
        // sometimes Moshi reads integers as doubles for some reason
        val json = """{ "integer": 20.0 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(20, entity.integer!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `double value`() {
        val json = """{ "double": 20.123 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(20.123, entity.double!!.evaluate(ExpressionResolver.EMPTY), 0.0)
    }

    @Test
    fun `integer value as double`() {
        val json = """{ "double": 20 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(20.0, entity.double!!.evaluate(ExpressionResolver.EMPTY), 0.0)
    }

    @Test
    fun `positive integer value`() {
        val json = """{ "positive_integer": 20 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(20, entity.positiveInteger!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `invalid positive integer value`() {
        val json = """{ "positive_integer": 0 }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertNull(entity.positiveInteger)
    }

    @Test
    fun `1 as true`() {
        val json = """{ "boolean": 1 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertTrue(entity.boolean!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `0 as false`() {
        val json = """{ "boolean": 0 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertFalse(entity.boolean!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `random integer value as null`() {
        val json = """{ "boolean": 123 }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertNull(entity.boolean)
    }

    @Test
    fun `double value as true`() {
        // sometimes Moshi reads integers as doubles for some reason
        val json = """{ "boolean": 1.0 }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertTrue(entity.boolean!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `Url value`() {
        val json = """{ "url": "https://yandex.ru" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(Uri.parse("https://yandex.ru"), entity.url!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `Color without alpha value`() {
        val json = """{ "color": "#BBCCDD" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(Color.parseColor("#BBCCDD"), entity.color!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `Color with alpha value`() {
        val json = """{ "color": "#AABBCCDD" }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(Color.parseColor("#AABBCCDD"), entity.color!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `invalid Color value`() {
        val json = """{ "color": "#ABBCCDD" }"""

        val entity = case.parse(LOG_ENVIRONMENT, json)

        assertNull(entity.color)
    }
}
