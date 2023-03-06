package com.yandex.div.core.util.mask

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MaskHelperTest {
    private val noisyString = "!a@9#b$8%c^7&d*"

    @Test
    fun `insert valid character to valid position`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+7 (1___) ___-__-__")

        val expectedMaskedValue = "+7 (1__) ___-__-__"
        val actualMaskedValue = maskHelper.value

        Assert.assertEquals(expectedMaskedValue, actualMaskedValue)
    }

    @Test
    fun `insert invalid character to valid position`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+7 (a___) ___-__-__")

        val expectedMaskedValue = "+7 (___) ___-__-__"
        val actualMaskedValue = maskHelper.value

        Assert.assertEquals(expectedMaskedValue, actualMaskedValue)
    }

    @Test
    fun `insert valid character before valid position`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+17 (___) ___-__-__")

        maskHelper.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `insert valid character after valid position`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+7 (___) ___-_1-__")

        maskHelper.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `insert between filled dynamic`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (1203) 45_-__-__")

        maskHelper.assertMask("+7 (120) 345-__-__", 9)
    }

    @Test
    fun `replace valid character before valid position`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+1(___) ___-__-__")

        maskHelper.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `replace valid character after valid position`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+7 (___) ___-__-__1")

        maskHelper.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `remove static char before first dynamic`() {
        val maskData = buildPhoneMask(false)

        val maskHelper = MaskHelper(maskData)

        maskHelper.applyChangeFrom("+7 ")
        maskHelper.assertMask("+7 (", 4)
    }

    @Test
    fun `remove static char after dynamic`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (123)45_-__-__")
        maskHelper.assertMask("+7 (124) 5__-__-__", 6)
    }

    @Test
    fun `remove character before first slot`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+ (123) 45_-__-__")
        maskHelper.assertMask("+7 (123) 45_-__-__", 4)
    }

    @Test
    fun `remove first dynamic character`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (23) 45_-__-__")
        maskHelper.assertMask("+7 (234) 5__-__-__", 4)
    }

    @Test
    fun `remove multiple dynamic character at center`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (1) 45_-__-__")
        maskHelper.assertMask("+7 (145) ___-__-__", 5)
    }

    @Test
    fun `remove end dynamic character`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (123) 4_-__-__")
        maskHelper.assertMask("+7 (123) 4__-__-__", 10)
    }

    @Test
    fun `remove placeholder after last dynamic character`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (123) 45_-_-__")
        maskHelper.assertMask("+7 (123) 4__-__-__", 10)
    }

    @Test
    fun `remove static character after last dynamic character`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        maskHelper.applyChangeFrom("+7 (123) 45_-____")
        maskHelper.assertMask("+7 (123) 4__-__-__", 10)
    }

    @Test
    fun `calculate multikey insertable string from center`() {
        val maskData = buildMultikeyMask(true)

        val maskHelper = MaskHelper(maskData)

        val expectedInsertableString = "98cd"
        val actualInsertableString = maskHelper.calculateInsertableSubstring(noisyString, 3)

        Assert.assertEquals(expectedInsertableString, actualInsertableString)
    }

    @Test
    fun `collect value range from start to end`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        val expectedValue = "12345"
        val actualValue = maskHelper.collectValueRange(0, maskHelper.value.length - 1)

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `collect value range from center`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        val expectedValue = "34"
        val actualValue = maskHelper.collectValueRange(6, 9)

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `characters replacement with clear substring`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        val expectedValue = "+7 (198) 75_-__-__"

        maskHelper.replaceChars("987", 5)

        val actualValue = maskHelper.value

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `characters replacement with noisy substring`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("12345")

        val expectedValue = "+7 (198) 75_-__-__"

        maskHelper.replaceChars(noisyString, 5)

        val actualValue = maskHelper.value

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `characters replacement with substring at the end`() {
        val maskData = buildPhoneMask(true)

        val maskHelper = MaskHelper(maskData).withValue("1234567890")

        val expectedValue = "+7 (123) 456-78-55"

        maskHelper.replaceChars("555", 16)

        val actualValue = maskHelper.value

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `remove character from center multikey mask`() {
        val maskData = buildMultikeyMask(true)

        val maskHelper = MaskHelper(maskData).withValue("A123BC")

        maskHelper.applyChangeFrom("A 13 BC")

        maskHelper.assertMask("A 13_ --", 3)
    }

    @Test
    fun `remove char from code mask`() {
        val maskData = buildCodeMask(true)

        val maskHelper = MaskHelper(maskData).withValue("abcde012345")

        maskHelper.applyChangeFrom("abcd-e12-345_-____")

        maskHelper.assertMask("abcd-e123-45__-____", 6)
    }

    private fun buildPhoneMask(alwaysVisible: Boolean): MaskHelper.MaskData {
        return MaskHelper.MaskData(
            pattern = "+7 (###) ###-##-##",
            decoding = listOf(
                MaskHelper.MaskKey(
                    key = '#',
                    filter = "\\d",
                    placeholder = '_'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun buildCodeMask(alwaysVisible: Boolean): MaskHelper.MaskData {
        return MaskHelper.MaskData(
            pattern = "####-####-####-####",
            decoding = listOf(
                MaskHelper.MaskKey(
                    key = '#',
                    filter = ".",
                    placeholder = '_'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun buildMultikeyMask(alwaysVisible: Boolean): MaskHelper.MaskData {
        return MaskHelper.MaskData(
            pattern = "$ ### $$",
            decoding = listOf(
                MaskHelper.MaskKey(
                    key = '#',
                    filter = "\\d",
                    placeholder = '_'
                ),
                MaskHelper.MaskKey(
                    key = '$',
                    filter = "[a-zA-Z]",
                    placeholder = '-'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun MaskHelper.withValue(value: String): MaskHelper {
        overrideRawValue(value)

        return this
    }

    private fun MaskHelper.assertMask(
        expectedMaskedValue: String,
        expectedCursorPosition: Int
    ) {
        Assert.assertEquals(expectedMaskedValue, value)
        Assert.assertEquals(expectedCursorPosition, cursorPosition)
    }
}