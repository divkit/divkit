package com.yandex.div.evaluable.types

import org.junit.Assert
import org.junit.Test

class ColorTest {

    private val testColor9Digits = "#FF001122"
    private val testColor7Digits = "#001122"
    private val testColor5Digits = "#F012"
    private val testColor4Digits = "#012"
    private val invalidColorStringText = "invalid color string"
    private val invalidColorStringWithoutSharp = "FF001122"
    private val invalidColorStringWrongCharacters = "#FF0011QQ"
    private val testColorInt = 0xFF001122.toInt()

    @Test
    fun `check parseColor`() {
        Assert.assertEquals(testColorInt, Color.parse(testColor9Digits).value)
        Assert.assertEquals(testColorInt, Color.parse(testColor7Digits).value)
        Assert.assertEquals(testColorInt, Color.parse(testColor5Digits).value)
        Assert.assertEquals(testColorInt, Color.parse(testColor4Digits).value)
    }

    @Test
    fun `check components functions 9 digits`() {
        Assert.assertEquals(0xff, Color(testColorInt).alpha())
        Assert.assertEquals(0x00, Color(testColorInt).red())
        Assert.assertEquals(0x11, Color(testColorInt).green())
        Assert.assertEquals(0x22, Color(testColorInt).blue())
    }

    @Test
    fun `check create functions`() {
        Assert.assertEquals(testColorInt, Color.argb(0xff, 0x00, 0x11, 0x22).value)
        Assert.assertEquals(0, Color.argb(0x00, 0x00, 0x00, 0x00).value)
        Assert.assertEquals(testColorInt, Color.rgb(0x00, 0x11, 0x22).value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `error parsing invalid color string`() {
        Color.parse(invalidColorStringText)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `error parsing invalid color without #`() {
        Color.parse(invalidColorStringWithoutSharp)
    }

    @Test(expected = NumberFormatException::class)
    fun `error parsing invalid color with wrong character`() {
        Color.parse(invalidColorStringWrongCharacters)
    }
}