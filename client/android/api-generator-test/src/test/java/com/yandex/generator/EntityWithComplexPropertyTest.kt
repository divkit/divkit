package com.yandex.generator

import android.net.Uri
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithComplexProperty
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityWithComplexPropertyTest {

    private val case = EntityTestCase(
        ctor = EntityWithComplexProperty.Companion::invoke,
    )

    @Test
    fun `property value is set`() {
        val json = """{
            "property": {
                "value": "https://ya.ru"
            }
        }"""

        val entity = case.parse(ASSERT_ENVIRONMENT, json)

        assertEquals(Uri.parse("https://ya.ru"), entity.property.value.evaluate(ExpressionResolver.EMPTY))
    }

    @Test(expected = ParsingException::class)
    fun `null property value`() {
        val json = """{ }"""

        case.parse(LOG_ENVIRONMENT, json)
    }

    @Test(expected = ParsingException::class)
    fun `invalid property`() {
        val json = """{
            "property": {
                "value": 1
            }
        }"""

        case.parse(LOG_ENVIRONMENT, json)
    }
}
