package com.yandex.div.core.type

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorTest {

    @Test
    fun `parse expands RGB format to opaque color`() {
        val colorString = "#123"

        val parsed = Color.parse(colorString)

        assertEquals(Color.create(0xFF112233.toInt()), parsed)
    }

    @Test
    fun `parse expands ARGB format`() {
        val colorString = "#8123"

        val parsed = Color.parse(colorString)

        assertEquals(Color.create(0x88112233.toInt()), parsed)
    }

    @Test
    fun `parse treats RRGGBB format as opaque color`() {
        val colorString = "#112233"

        val parsed = Color.parse(colorString)

        assertEquals(Color.create(0xFF112233.toInt()), parsed)
    }

    @Test
    fun `parse keeps alpha of AARRGGBB format`() {
        val colorString = "#80112233"

        val parsed = Color.parse(colorString)

        assertEquals(Color.create(0x80112233.toInt()), parsed)
    }

    @Test
    fun `parse accepts lowercase hex digits`() {
        val colorString = "#80aabbcc"

        val parsed = Color.parse(colorString)

        assertEquals(Color.create(0x80AABBCC.toInt()), parsed)
    }

    @Test
    fun `parse throws when string is empty`() {
        val colorString = ""

        assertFailsWith<IllegalArgumentException> { Color.parse(colorString) }
    }

    @Test
    fun `parse throws when string does not start with hash`() {
        val colorString = "80112233"

        assertFailsWith<IllegalArgumentException> { Color.parse(colorString) }
    }

    @Test
    fun `parse throws when string length is unsupported`() {
        val colorString = "#11223"

        assertFailsWith<IllegalArgumentException> { Color.parse(colorString) }
    }

    @Test
    fun `parse throws when string contains non hex digits`() {
        val colorString = "#8011223G"

        assertFailsWith<NumberFormatException> { Color.parse(colorString) }
    }

    @Test
    fun `parse throws when hex digits start with plus sign`() {
        val colorString = "#+1223344"

        assertFailsWith<NumberFormatException> { Color.parse(colorString) }
    }

    @Test
    fun `parse throws when hex digits start with minus sign`() {
        val colorString = "#-1223344"

        assertFailsWith<NumberFormatException> { Color.parse(colorString) }
    }

    @Test
    fun `argb composes color from components`() {
        val composed = Color.argb(0x80, 0x11, 0x22, 0x33)

        assertEquals(Color.create(0x80112233.toInt()), composed)
    }

    @Test
    fun `rgb composes opaque color from components`() {
        val composed = Color.rgb(0x11, 0x22, 0x33)

        assertEquals(Color.create(0xFF112233.toInt()), composed)
    }

    @Test
    fun `create from color copies value`() {
        val source = Color.create(0x80112233.toInt())

        val copy = Color.create(source)

        assertEquals(Color.create(0x80112233.toInt()), copy)
    }

    @Test
    fun `alpha returns unsigned alpha component`() {
        val opaqueColor = Color.create(0xFF000000.toInt())

        val alpha = opaqueColor.alpha()

        assertEquals(0xFF, alpha)
    }

    @Test
    fun `red returns red component`() {
        val sourceColor = Color.create(0x80112233.toInt())

        val red = sourceColor.red()

        assertEquals(0x11, red)
    }

    @Test
    fun `green returns green component`() {
        val sourceColor = Color.create(0x80112233.toInt())

        val green = sourceColor.green()

        assertEquals(0x22, green)
    }

    @Test
    fun `blue returns blue component`() {
        val sourceColor = Color.create(0x80112233.toInt())

        val blue = sourceColor.blue()

        assertEquals(0x33, blue)
    }

    @Test
    fun `toString formats color as uppercase AARRGGBB with hash`() {
        val sourceColor = Color.create(0x0000ABCD)

        val string = sourceColor.toString()

        assertEquals("#0000ABCD", string)
    }

    @Test
    fun `toEncodedString formats color with url encoded hash`() {
        val sourceColor = Color.create(0x80112233.toInt())

        val string = sourceColor.toEncodedString()

        assertEquals("%2380112233", string)
    }
}
