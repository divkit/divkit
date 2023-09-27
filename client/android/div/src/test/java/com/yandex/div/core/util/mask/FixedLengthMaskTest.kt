package com.yandex.div.core.util.mask

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FixedLengthMaskTest {
    private val noisyString = "!a@9#b$8%c^7&d*"

    @Test
    fun `calculate multikey insertable string from center`() {
        val maskData = buildMultikeyMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        val expectedInsertableString = "98cd"
        val actualInsertableString = fixedInputMask.publicCalculateInsertableSubstring(noisyString, 3)

        Assert.assertEquals(expectedInsertableString, actualInsertableString)
    }

    @Test
    fun `collect value range from start to end`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        val expectedValue = "12345"
        val actualValue = fixedInputMask.publicCollectValueRange(0, fixedInputMask.value.length - 1)

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `collect value range from center`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        val expectedValue = "34"
        val actualValue = fixedInputMask.publicCollectValueRange(6, 9)

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `characters replacement with clear substring`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        val expectedValue = "+7 (198) 75_-__-__"

        fixedInputMask.publicReplaceChars("987", 5)

        val actualValue = fixedInputMask.value

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `characters replacement with noisy substring`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        val expectedValue = "+7 (198) 75_-__-__"

        fixedInputMask.publicReplaceChars(noisyString, 5)

        val actualValue = fixedInputMask.value

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `characters replacement with substring at the end`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("1234567890")

        val expectedValue = "+7 (123) 456-78-55"

        fixedInputMask.publicReplaceChars("555", 16)

        val actualValue = fixedInputMask.value

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `insert valid character to valid position`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+7 (1___) ___-__-__", 5)

        val expectedMaskedValue = "+7 (1__) ___-__-__"
        val actualMaskedValue = fixedInputMask.value

        Assert.assertEquals(expectedMaskedValue, actualMaskedValue)
    }

    @Test
    fun `insert invalid character to valid position`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+7 (a___) ___-__-__", 5)

        val expectedMaskedValue = "+7 (___) ___-__-__"
        val actualMaskedValue = fixedInputMask.value

        Assert.assertEquals(expectedMaskedValue, actualMaskedValue)
    }

    @Test
    fun `insert valid character before valid position`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+17 (___) ___-__-__", 2)

        fixedInputMask.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `insert valid character after valid position`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+7 (___) ___-_1-__", 15)

        fixedInputMask.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `insert between filled dynamic`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (1203) 45_-__-__", 7)

        fixedInputMask.assertMask("+7 (120) 345-__-__", 9)
    }

    @Test
    fun `insert valid character to single pattern mask`() {
        val maskData = buildSinglePatternMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("a", 1)

        fixedInputMask.assertMask("a", 1)
    }

    @Test
    fun `insert invalid character to single pattern mask`() {
        val maskData = buildSinglePatternMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("1", 1)

        fixedInputMask.assertMask("_", 0)
    }

    @Test
    fun `insert valid character to multikey single pattern mask`() {
        val maskData = buildMultikeySinglePatternMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("a", 1)

        fixedInputMask.assertMask("a", 1)
    }

    @Test
    fun `insert invalid character to multikey single pattern mask`() {
        val maskData = buildMultikeySinglePatternMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("1", 1)

        fixedInputMask.assertMask("_", 0)
    }

    @Test
    fun `replace valid character before valid position`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+1(___) ___-__-__", 2)

        fixedInputMask.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `replace valid character after valid position`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+7 (___) ___-__-__1", 19)

        fixedInputMask.assertMask("+7 (1__) ___-__-__", 5)
    }

    @Test
    fun `remove static char before first dynamic`() {
        val maskData = buildPhoneMask(false)

        val fixedInputMask = createFixedLengthMask(maskData)

        fixedInputMask.applyChangeFrom("+7 ", 3)
        fixedInputMask.assertMask("+7 (", 4)
    }

    @Test
    fun `remove static char after dynamic`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (123)45_-__-__", 8)
        fixedInputMask.assertMask("+7 (124) 5__-__-__", 6)
    }

    @Test
    fun `remove character before first slot`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+ (123) 45_-__-__", 1)
        fixedInputMask.assertMask("+7 (123) 45_-__-__", 4)
    }

    @Test
    fun `remove first dynamic character`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (23) 45_-__-__", 4)
        fixedInputMask.assertMask("+7 (234) 5__-__-__", 4)
    }

    @Test
    fun `remove multiple dynamic character at center`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (1) 45_-__-__", 5)
        fixedInputMask.assertMask("+7 (145) ___-__-__", 5)
    }

    @Test
    fun `remove end dynamic character`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (123) 4_-__-__", 10)
        fixedInputMask.assertMask("+7 (123) 4__-__-__", 10)
    }

    @Test
    fun `remove placeholder after last dynamic character`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (123) 45_-_-__", 14)
        fixedInputMask.assertMask("+7 (123) 4__-__-__", 10)
    }

    @Test
    fun `remove static character after last dynamic character`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12345")

        fixedInputMask.applyChangeFrom("+7 (123) 45_-____", 15)
        fixedInputMask.assertMask("+7 (123) 4__-__-__", 10)
    }

    @Test
    fun `remove character from center multikey mask`() {
        val maskData = buildMultikeyMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("A123BC")

        fixedInputMask.applyChangeFrom("A 13 BC", 3)

        fixedInputMask.assertMask("A 13_ --", 3)
    }

    @Test
    fun `remove char from code mask`() {
        val maskData = buildCodeMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("abcde012345")

        fixedInputMask.applyChangeFrom("abcd-e12-345_-____", 6)

        fixedInputMask.assertMask("abcd-e123-45__-____", 6)
    }

    @Test
    fun `remove char from repeated part`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("11111")

        fixedInputMask.applyChangeFrom("+7 (11) 11_-__-__", 5)

        fixedInputMask.assertMask("+7 (111) 1__-__-__", 5)
    }

    @Test
    fun `insert char to repeated part`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("1111")

        fixedInputMask.applyChangeFrom("+7 (1111) 1__-__-__", 6)

        fixedInputMask.assertMask("+7 (111) 11_-__-__", 6)
    }

    @Test
    fun `cursor jumps over static char`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("12")

        fixedInputMask.applyChangeFrom("+7 (123_) ___-__-__", 7)

        fixedInputMask.assertMask("+7 (123) ___-__-__", 9)
    }

    @Test
    fun `char insertion will not cause character lost`() {
        val maskData = buildPhoneMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("1234567890")

        fixedInputMask.applyChangeFrom("+7 (123) 4056-78-90", 11)

        fixedInputMask.assertMask("+7 (123) 456-78-90", 10)
    }

    @Test
    fun `char insertion will not cause character lost with multikey`() {
        val maskData = buildMultikeyMask(true)

        val fixedInputMask = createFixedLengthMask(maskData).withValue("A124")

        fixedInputMask.applyChangeFrom("A 1234 --", 5)

        fixedInputMask.assertMask("A 124 --", 4)
    }

    @Test
    fun `multiple char insertion with multikey`() {
        val maskData = BaseInputMask.MaskData(
            pattern = "###$$$",
            decoding = listOf(
                BaseInputMask.MaskKey(
                    key = '#',
                    filter = "[1-9]",
                    placeholder = '_'
                ),
                BaseInputMask.MaskKey(
                    key = '$',
                    filter = "[5-9]",
                    placeholder = '_'
                )
            ),
            alwaysVisible = false
        )

        val fixedInputMask = createFixedLengthMask(maskData).withValue("1256")

        fixedInputMask.applyChangeFrom("123456", 4)

        fixedInputMask.assertMask("12356", 3)
    }

    private fun createFixedLengthMask(maskData: BaseInputMask.MaskData): TestFixedLengthInputMask {
        return TestFixedLengthInputMask(maskData)
    }

    private fun buildSinglePatternMask(alwaysVisible: Boolean): BaseInputMask.MaskData {
        return BaseInputMask.MaskData(
            pattern = "#",
            decoding = listOf(
                BaseInputMask.MaskKey(
                    key = '#',
                    filter = "[a-z]",
                    placeholder = '_'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun buildMultikeySinglePatternMask(alwaysVisible: Boolean): BaseInputMask.MaskData {
        return BaseInputMask.MaskData(
            pattern = "#",
            decoding = listOf(
                BaseInputMask.MaskKey(
                    key = '#',
                    filter = "[a-z]",
                    placeholder = '_'
                ),
                BaseInputMask.MaskKey(
                    key = '$',
                    filter = "[a-z]",
                    placeholder = '*'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun buildPhoneMask(alwaysVisible: Boolean): BaseInputMask.MaskData {
        return BaseInputMask.MaskData(
            pattern = "+7 (###) ###-##-##",
            decoding = listOf(
                BaseInputMask.MaskKey(
                    key = '#',
                    filter = "\\d",
                    placeholder = '_'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun buildCodeMask(alwaysVisible: Boolean): BaseInputMask.MaskData {
        return BaseInputMask.MaskData(
            pattern = "####-####-####-####",
            decoding = listOf(
                BaseInputMask.MaskKey(
                    key = '#',
                    filter = ".",
                    placeholder = '_'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun buildMultikeyMask(alwaysVisible: Boolean): BaseInputMask.MaskData {
        return BaseInputMask.MaskData(
            pattern = "$ ### $$",
            decoding = listOf(
                BaseInputMask.MaskKey(
                    key = '#',
                    filter = "\\d",
                    placeholder = '_'
                ),
                BaseInputMask.MaskKey(
                    key = '$',
                    filter = "[a-zA-Z]",
                    placeholder = '-'
                )
            ),
            alwaysVisible = alwaysVisible
        )
    }

    private fun TestFixedLengthInputMask.withValue(value: String): TestFixedLengthInputMask {
        overrideRawValue(value)

        return this
    }

    private fun TestFixedLengthInputMask.assertMask(
        expectedMaskedValue: String,
        expectedCursorPosition: Int
    ) {
        Assert.assertEquals(expectedMaskedValue, value)
        Assert.assertEquals(expectedCursorPosition, cursorPosition)
    }
}