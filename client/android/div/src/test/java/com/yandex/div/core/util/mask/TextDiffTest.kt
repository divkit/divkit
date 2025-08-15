package com.yandex.div.core.util.mask

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextDiffTest {
    @Test
    fun `build diff for empty left`() {
        val left = ""
        val right = "content"

        val expectedTextDiff = TextDiff(
            start = 0,
            added = 7,
            removed = 0
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for empty right`() {
        val left = "content"
        val right = ""

        val expectedTextDiff = TextDiff(
            start = 0,
            added = 0,
            removed = 7
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for both empty`() {
        val left = ""
        val right = ""

        val expectedTextDiff = TextDiff(
            start = 0,
            added = 0,
            removed = 0
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for shorter left`() {
        val left = "abc"
        val right = "abcde"

        val expectedTextDiff = TextDiff(
            start = 3,
            added = 2,
            removed = 0
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for shorter right`() {
        val left = "abcde"
        val right = "abc"

        val expectedTextDiff = TextDiff(
            start = 3,
            added = 0,
            removed = 2
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for fully different`() {
        val left = "abcde"
        val right = "12345"

        val expectedTextDiff = TextDiff(
            start = 0,
            added = 5,
            removed = 5
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for change at start`() {
        val left = "abcde"
        val right = "ABcde"

        val expectedTextDiff = TextDiff(
            start = 0,
            added = 2,
            removed = 2
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for change at center`() {
        val left = "abcde"
        val right = "aBCDe"

        val expectedTextDiff = TextDiff(
            start = 1,
            added = 3,
            removed = 3
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for change at end`() {
        val left = "abcde"
        val right = "abcDE"

        val expectedTextDiff = TextDiff(
            start = 3,
            added = 2,
            removed = 2
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for shorting replacement`() {
        val left = "abcde"
        val right = "aFe"

        val expectedTextDiff = TextDiff(
            start = 1,
            added = 1,
            removed = 3
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }

    @Test
    fun `build diff for adding replacement`() {
        val left = "aFe"
        val right = "abcde"

        val expectedTextDiff = TextDiff(
            start = 1,
            added = 3,
            removed = 1
        )
        val actualTextDiff = TextDiff.build(left, right)

        Assert.assertEquals(expectedTextDiff, actualTextDiff)
    }
}