package com.yandex.div.core.view2.divs

import com.yandex.div.core.view2.DivValidator
import com.yandex.div.json.expressions.ExpressionResolver
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters

@RunWith(ParameterizedRobolectricTestRunner::class)
class DivValidatorTest(
    private val dir: String,
    private val case: String,
    private val expected: Boolean
) {

    private val validator = DivValidator()

    @Test
    fun `div validation`() {
        val div = UnitTestData(dir, case).div
        assertEquals(expected, validator.validate(div, ExpressionResolver.EMPTY))
    }

    companion object {
        @JvmStatic
        @Parameters
        fun parameters(): Collection<Array<Any>> {
            return listOf(
                arrayOf("div-grid", "incompatible-horizontal-traits.json", false),
                arrayOf("div-grid", "incompatible-vertical-traits.json", false),
                arrayOf("div-grid", "invalid-column-span1.json", false),
                arrayOf("div-grid", "invalid-column-span2.json", false),
                arrayOf("div-grid", "invalid-column-span3.json", false),
                arrayOf("div-grid", "invalid-row-span1.json", false),
                arrayOf("div-grid", "invalid-row-span2.json", false),
                arrayOf("div-grid", "invalid-row-span3.json", false)
            )
        }
    }
}
