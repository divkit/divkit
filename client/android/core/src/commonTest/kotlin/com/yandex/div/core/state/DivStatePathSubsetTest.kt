package com.yandex.div.core.state

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DivStatePathSubsetTest {
    private val rootPath = DivStatePath.fromState(0).appendDiv("root")

    @Test
    fun `equal full paths are subsets`() {
        assertTrue(rootPath.isSubsetOf(rootPath.copy()))
    }

    @Test
    fun `parent full path is a subset of its child`() {
        assertTrue(rootPath.isSubsetOf(rootPath.appendDiv("child")))
    }

    @Test
    fun `child full path is not a subset of its parent`() {
        assertFalse(rootPath.appendDiv("child").isSubsetOf(rootPath))
    }

    @Test
    fun `string prefix within a segment does not make a path subset`() {
        assertFalse(rootPath.isSubsetOf(DivStatePath.fromState(0).appendDiv("root_suffix")))
    }

    @Test
    fun `matching states do not make different full paths subsets`() {
        val localPath = DivStatePath.parse("0/state/active")
        val blockPath = rootPath.append("state", "active", "active")

        assertFalse(localPath.isSubsetOf(blockPath))
    }

    @Test
    fun `different top level segments do not make paths subsets`() {
        assertFalse(rootPath.isSubsetOf(DivStatePath.fromState(1).appendDiv("root")))
    }

    @Test
    fun `matching segments in different namespaces do not make paths subsets`() {
        assertFalse(rootPath.isSubsetOf(rootPath.withNamespace("tooltip")))
    }
}
