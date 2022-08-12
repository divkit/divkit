package com.yandex.div.evaluable

import com.yandex.div.evaluable.function.Colors
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorsTest {

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
        assertEquals(testColorInt, Colors.parseColor(testColor9Digits))
        assertEquals(testColorInt, Colors.parseColor(testColor7Digits))
        assertEquals(testColorInt, Colors.parseColor(testColor5Digits))
        assertEquals(testColorInt, Colors.parseColor(testColor4Digits))
    }

    @Test
    fun `check components functions 9 digits`() {
        assertEquals(0xff, Colors.alpha(testColorInt))
        assertEquals(0x00, Colors.red(testColorInt))
        assertEquals(0x11, Colors.green(testColorInt))
        assertEquals(0x22, Colors.blue(testColorInt))
    }

    @Test
    fun `check create functions`() {
        assertEquals(testColorInt, Colors.argb(0xff, 0x00, 0x11, 0x22))
        assertEquals(0, Colors.argb(0x00, 0x00, 0x00, 0x00))
        assertEquals(testColorInt, Colors.rgb(0x00, 0x11, 0x22))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `error parsing invalid color string`() {
        Colors.parseColor(invalidColorStringText)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `error parsing invalid color without #`() {
        Colors.parseColor(invalidColorStringWithoutSharp)
    }

    @Test(expected = NumberFormatException::class)
    fun `error parsing invalid color with wrong character`() {
        Colors.parseColor(invalidColorStringWrongCharacters)
    }
}
