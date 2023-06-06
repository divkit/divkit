package com.yandex.div.core.util.validator

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegexInputValidatorTest {
    private val atLeast8CharsNotEmptyValidator = createValidator(".{8,}", false)

    private val atLeast8CharsAllowEmptyValidator = createValidator(".{8,}", true)

    private val ignoreCaseValidator = createValidator("(?i)[a-z]{2}")

    @Test
    fun `valid value is valid`() {
        atLeast8CharsNotEmptyValidator.validateAndAssert("12345678", true)
    }

    @Test
    fun `invalid value is invalid`() {
        atLeast8CharsNotEmptyValidator.validateAndAssert("1234", false)
    }

    @Test
    fun `empty is not valid when it disallowed`() {
        atLeast8CharsNotEmptyValidator.validateAndAssert("", false)
    }

    @Test
    fun `empty is valid when it allowed`() {
        atLeast8CharsAllowEmptyValidator.validateAndAssert("", true)
    }

    @Test
    fun `regex flags are working`() {
        ignoreCaseValidator.validateAndAssert("Aa", true)
    }

    private fun createValidator(pattern: String, allowEmpty: Boolean = false): BaseValidator {
        val regex = Regex(pattern)

        return RegexValidator(regex, allowEmpty)
    }

    private fun BaseValidator.validateAndAssert(value: String, expectedResult: Boolean) {
        val isValid = validate(value)

        Assert.assertEquals(expectedResult, isValid)
    }
}
