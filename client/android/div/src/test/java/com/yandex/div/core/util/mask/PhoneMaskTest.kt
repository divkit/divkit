package com.yandex.div.core.util.mask

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class PhoneMaskTest {

    @Test
    fun `empty string is empty`() {
        val phoneMask = createPhoneMask()

        phoneMask.assertMask("", 0)
    }

    @Test
    fun `remove all decimal into empty string`() {
        val phoneMask = createPhoneMask().withValue("+7 (800) 555-35-35")

        phoneMask.applyChangeFrom("+", 1)

        phoneMask.assertMask("", 0)
    }

    @Test
    fun `insert decimal to empty string`() {
        val phoneMask = createPhoneMask()

        phoneMask.applyChangeFrom("1", 1)

        phoneMask.assertMask("+1 (", 4)
    }

    @Test
    fun `insert multiple decimals at once to empty string without mask changing`() {
        val phoneMask = createPhoneMask()

        phoneMask.applyChangeFrom("7950", 4)

        phoneMask.assertMask("+7 (950) ", 9)
    }

    @Test
    fun `insert one decimal without mask changing`() {
        val phoneMask = createPhoneMask().withValue("+1 (")

        phoneMask.applyChangeFrom("+1 (2", 5)

        phoneMask.assertMask("+1 (2", 5)
    }

    @Test
    fun `insert multiple decimals without mask changing`() {
        val phoneMask = createPhoneMask().withValue("+1 (")

        phoneMask.applyChangeFrom("+1 (2345", 8)

        phoneMask.assertMask("+1 (234) 5", 10)
    }

    @Test
    fun `remove one decimal without mask changing`() {
        val phoneMask = createPhoneMask().withValue("8 (950) ")

        phoneMask.applyChangeFrom("8 (950)", 7)

        phoneMask.assertMask("8 (95", 5)
    }

    @Test
    fun `remove multiple decimal without mask changing`() {
        val phoneMask = createPhoneMask().withValue("+1 (234) 567-89-00")

        phoneMask.applyChangeFrom("+1 (9-00", 4)

        phoneMask.assertMask("+1 (900) ", 4)
    }

    @Test
    fun `insert one decimal with mask changing`() {
        val phoneMask = createPhoneMask().withValue("+351 (11) 111-")

        phoneMask.applyChangeFrom("+3151 (11) 111-", 3)

        phoneMask.assertMask("+31 51 111 11", 4)
    }

    @Test
    fun `insert multiple decimals with mask changing`() {
        val phoneMask = createPhoneMask().withValue("+31 11 111 1")

        phoneMask.applyChangeFrom("+3735 11 111 1", 5)

        phoneMask.assertMask("+373 (511) 1-11-1", 7)
    }

    @Test
    fun `remove one decimal with mask changing`() {
        val phoneMask = createPhoneMask().withValue("+7 (895) 011-11-11")

        phoneMask.applyChangeFrom("+7 895) 011-11-11", 3)

        phoneMask.assertMask("8 (950) 111-11-1", 0)
    }

    @Test
    fun `remove multiple decimal with mask changing`() {
        val phoneMask = createPhoneMask().withValue("+7 (111) 189-50-111")

        phoneMask.applyChangeFrom("89-50-111", 0)

        phoneMask.assertMask("8 (950) 111-", 0)
    }

    @Test
    fun `change format of full input without extra numbers`() {
        val phoneMask = createPhoneMask().withValue("+1 (234) 567-89-01")

        phoneMask.applyChangeFrom("+91 (234) 567-89-01", 2)

        phoneMask.assertMask("+91 (234) 567-89-01", 2)
    }

    @Test
    fun `don't change format of full input with extra numbers`() {
        val phoneMask = createPhoneMask().withValue("+1 (234) 567-89-0100")

        phoneMask.applyChangeFrom("+91 (234) 567-89-0100", 2)

        phoneMask.assertMask("+1 (234) 567-89-0100", 1)
    }

    private fun createPhoneMask(): PhoneInputMask {
        return PhoneInputMask { }
    }

    private fun PhoneInputMask.withValue(value: String): PhoneInputMask {
        applyChangeFrom(value)
        return this
    }

    private fun PhoneInputMask.assertMask(
        expectedMaskedValue: String,
        expectedCursorPosition: Int,
    ) {
        Assert.assertEquals(expectedMaskedValue, value)
        Assert.assertEquals(expectedCursorPosition, cursorPosition)
    }

}
