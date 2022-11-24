package com.yandex.generator

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.testing.EntityWithSimpleProperties
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SimplePropertiesTest {

    private val case = EntityTemplateTestCase(
        ctor = EntityWithSimpleProperties.Companion::invoke
    )

    @Test
    fun `boolean as int`() {
        val actual = case.parse(
            directory = "simple_properties",
            filename = "boolean_as_int.json"
        )
        // TODO: boolean property represented by integer value must fail
        // Assert.assertNull(actual.boolean!!.evaluate(ExpressionResolver.EMPTY))
        Assert.assertTrue(actual.boolean!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `boolean_int as boolean`() {
        val actual = case.parse(
            directory = "simple_properties",
            filename = "boolean_int_as_boolean.json"
        )
        Assert.assertTrue(actual.booleanInt!!.evaluate(ExpressionResolver.EMPTY))
    }

    @Test
    fun `boolean_int as int`() {
        val actual = case.parse(
            directory = "simple_properties",
            filename = "boolean_int_as_int.json"
        )
        Assert.assertTrue(actual.booleanInt!!.evaluate(ExpressionResolver.EMPTY))
    }

}
