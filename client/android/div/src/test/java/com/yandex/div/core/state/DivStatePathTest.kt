package com.yandex.div.core.state

import org.junit.Assert
import org.junit.Test


class DivStatePathTest {
    private val rootPath = DivStatePath(0, emptyList(), listOf("0", "a"))
    private val firstLvlPath = DivStatePath(0, listOf("a" to "s"), listOf("0", "a", "s", "d"))
    private val scndLvlPath = DivStatePath(0, listOf("a" to "s", "d" to "f"), listOf("0", "a", "s", "d", "f", "g"))
    private val thrdLvlPath = DivStatePath(
        0,
        listOf("a" to "s", "d" to "f", "g" to "h"),
        listOf("0", "a", "s", "d", "f", "g", "h")
    )

    @Test
    fun parse() {
        Assert.assertEquals(
            DivStatePath(1, listOf("foo" to "bar"), listOf("1", "foo", "bar"), true),
            DivStatePath.parse("1/foo/bar"),
        )
        Assert.assertEquals(
            DivStatePath(1, listOf("foo" to "bar", "lol" to "kek"), listOf("1", "foo", "bar", "lol", "kek"), true),
            DivStatePath.parse("1/foo/bar/lol/kek"),
        )
    }

    @Test
    fun `top state only is parsed`() {
        Assert.assertEquals(
            DivStatePath(1123123, mutableListOf(), listOf("1123123"), true),
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
    fun `parentState works correctly`() {
        var tested = thrdLvlPath.parentState()

        Assert.assertEquals(tested, scndLvlPath)

        tested = tested.parentState()

        Assert.assertEquals(tested, firstLvlPath)

        tested = tested.parentState()

        Assert.assertEquals(tested, rootPath)

        tested = tested.parentState()

        Assert.assertEquals(tested, rootPath)
    }

    @Test
    fun `lowestCommonAncestor with root divId prefix`() {
        // Simulates paths created by fromRootDiv when root div is DivState with divId.
        // path[0] has format "stateId:divId" but states store bare "divId".
        val path1 = DivStatePath(
            0,
            listOf("rootId" to "disabled", "stateA" to "false", "switchA" to "on"),
            listOf("0:rootId", "disabled", "child#0", "stateA", "false", "child#1", "switchA", "on")
        )
        val path2 = DivStatePath(
            0,
            listOf("rootId" to "disabled", "stateB" to "false", "switchB" to "on"),
            listOf("0:rootId", "disabled", "child#0", "stateB", "false", "child#1", "switchB", "on")
        )

        val result = DivStatePath.lowestCommonAncestor(path1, path2)

        Assert.assertNotNull(result)
        Assert.assertEquals(listOf("rootId" to "disabled"), result!!.getStates())
        // extractStates finds "rootId" at index 0 (via "0:rootId"), stateId "disabled" at index 1,
        // returns index 1, addChild increments to 2, so subList(0, 2) = ["0:rootId", "disabled"]
        Assert.assertEquals("0:rootId/disabled", result.fullPath)
    }

    @Test
    fun `lowestCommonAncestor with root divId prefix and no shared nested states`() {
        val path1 = DivStatePath(
            0,
            listOf("rootId" to "active", "stateA" to "on"),
            listOf("0:rootId", "active", "child#0", "stateA", "on")
        )
        val path2 = DivStatePath(
            0,
            listOf("rootId" to "active", "stateB" to "on"),
            listOf("0:rootId", "active", "child#1", "stateB", "on")
        )

        val result = DivStatePath.lowestCommonAncestor(path1, path2)

        Assert.assertNotNull(result)
        Assert.assertEquals(listOf("rootId" to "active"), result!!.getStates())
    }

    @Test
    fun `namespace prefixes states string`() {
        Assert.assertEquals("ns/0/a/s/d/f", scndLvlPath.withNamespace("ns").statesString)
        Assert.assertEquals("ns/0", rootPath.withNamespace("ns").statesString)
    }

    @Test
    fun `namespace is kept by derived paths`() {
        val path = firstLvlPath.withNamespace("ns")

        Assert.assertEquals("ns", path.append("d", "f", "g").namespace)
        Assert.assertEquals("ns", path.appendDiv("x").namespace)
        Assert.assertEquals("ns", path.append("d", "f", "g").parentState().namespace)
        Assert.assertEquals("ns/0/a/s/d", path.append("d", "f", "g").pathToLastState)
    }

    @Test
    fun `paths with different namespaces are not equal`() {
        Assert.assertNotEquals(firstLvlPath, firstLvlPath.withNamespace("ns"))
        Assert.assertEquals(firstLvlPath.withNamespace("ns"), firstLvlPath.withNamespace("ns"))
        Assert.assertEquals(firstLvlPath.withNamespace("ns").hashCode(), firstLvlPath.withNamespace("ns").hashCode())
    }

    @Test
    fun `path is not ancestor of path with another namespace`() {
        Assert.assertFalse(firstLvlPath.withNamespace("ns").isAncestorOf(scndLvlPath))
        Assert.assertFalse(firstLvlPath.isAncestorOf(scndLvlPath.withNamespace("ns")))
        Assert.assertTrue(firstLvlPath.withNamespace("ns").isAncestorOf(scndLvlPath.withNamespace("ns")))
    }

    @Test
    fun `lowestCommonAncestor of paths with different namespaces is null`() {
        Assert.assertNull(DivStatePath.lowestCommonAncestor(scndLvlPath.withNamespace("ns"), thrdLvlPath))
        Assert.assertEquals(
            "ns",
            DivStatePath.lowestCommonAncestor(scndLvlPath.withNamespace("ns"), thrdLvlPath.withNamespace("ns"))?.namespace,
        )
    }

    @Test
    fun `alphabeticalComparator orders paths without namespace first`() {
        val sorted = listOf(firstLvlPath.withNamespace("b"), scndLvlPath, firstLvlPath.withNamespace("a"))
            .sortedWith(DivStatePath.alphabeticalComparator())

        Assert.assertEquals(listOf(null, "a", "b"), sorted.map { it.namespace })
    }
}
