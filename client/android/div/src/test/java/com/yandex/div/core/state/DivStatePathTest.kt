package com.yandex.div.core.state

import org.junit.Assert
import org.junit.Test


class DivStatePathTest {
    private val rootPath = DivStatePath.fromState(0)
    private val firstLvlPath = DivStatePath.parse("0/a/s")
    private val scndLvlPath = DivStatePath.parse("0/a/s/d/f")
    private val thrdLvlPath = DivStatePath.parse("0/a/s/d/f/g/h")

    @Test
    fun parse() {
        Assert.assertEquals(
            DivStatePath(1, mutableListOf("foo" to "bar")),
            DivStatePath.parse("1/foo/bar"),
        )
        Assert.assertEquals(
            DivStatePath(1, mutableListOf("foo" to "bar", "lol" to "kek")),
            DivStatePath.parse("1/foo/bar/lol/kek"),
        )
    }

    @Test
    fun `top state only is parsed`() {
        Assert.assertEquals(
            DivStatePath(1123123, mutableListOf()),
            DivStatePath.parse("1123123"),
        )
    }

    @Test
    fun `path to last state`() {
        val topLevelOnly = DivStatePath.parse("1")
        Assert.assertNull(null, topLevelOnly.pathToLastState)
        Assert.assertNull(null, topLevelOnly.lastStateId)

        val singleDiv = DivStatePath.parse("1/foo/bar")
        Assert.assertEquals("1/foo", singleDiv.pathToLastState)
        Assert.assertEquals("bar", singleDiv.lastStateId)

        val manyDivs = DivStatePath.parse("1/foo/bar/lol/kek")
        Assert.assertEquals("1/foo/bar/lol", manyDivs.pathToLastState)
        Assert.assertEquals("kek", manyDivs.lastStateId)
    }

    @Test(expected = PathFormatException::class)
    fun `not integer top level state id generates exception`() {
        DivStatePath.parse("1a/foo/bar")
    }

    @Test(expected = PathFormatException::class)
    fun `not even number of state ids`() {
        DivStatePath.parse("1a/foo/bar/kek")
    }

    @Test
    fun `previousStep works correctly`() {
        var tested = thrdLvlPath.parentState()

        Assert.assertEquals(tested, scndLvlPath)

        tested = tested.parentState()

        Assert.assertEquals(tested, firstLvlPath)

        tested = tested.parentState()

        Assert.assertEquals(tested, rootPath)

        tested = tested.parentState()

        Assert.assertEquals(tested, rootPath)
    }
}
