package com.yandex.div.core.expression.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.test.data.container
import com.yandex.div.test.data.gallery
import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class RuntimeAttachCacheManagerTest {
    private val store = mock<RuntimeStore>()
    private val view = mock<Div2View> {
        on { runtimeStore } doReturn store
    }
    private val visitor = mock<DivRuntimeVisitor> {
        on { createAndAttachRuntimes(any(), any(), any()) } doReturn true
    }
    private val manager = RuntimeAttachCacheManager(visitor)
    private val path = DivStatePath(0)
    private val root = gallery(items = listOf(container()))

    @Test
    fun `repeat window attach skips traversal and runtime access`() {
        manager.reattachRuntimes(root, path, view)
        clearInvocations(visitor, store)

        repeat(3) { manager.reattachRuntimes(root, DivStatePath(0), view) }

        verifyNoInteractions(visitor, store)
    }

    @Test
    fun `different model with equal contents does not reuse entry`() {
        manager.reattachRuntimes(root, path, view)
        val replacement = gallery(items = listOf(container()))
        clearInvocations(visitor)

        manager.reattachRuntimes(replacement, path, view)

        verify(visitor).createAndAttachRuntimes(replacement, path, view)
    }

    @Test
    fun `different store requires full traversal even for same root`() {
        manager.reattachRuntimes(root, path, view)
        whenever(view.runtimeStore) doReturn mock()
        clearInvocations(visitor)

        manager.reattachRuntimes(root, path, view)

        verify(visitor).createAndAttachRuntimes(root, path, view)
    }

    @Test
    fun `different root path requires full traversal`() {
        manager.reattachRuntimes(root, path, view)
        val replacement = DivStatePath(1)
        clearInvocations(visitor)

        manager.reattachRuntimes(root, replacement, view)

        verify(visitor).createAndAttachRuntimes(root, replacement, view)
    }

    @Test
    fun `namespaced root path requires full traversal`() {
        manager.reattachRuntimes(root, path, view)
        val replacement = path.withNamespace("tooltip")
        clearInvocations(visitor)

        manager.reattachRuntimes(root, replacement, view)

        verify(visitor).createAndAttachRuntimes(root, replacement, view)
    }

    @Test
    fun `non skippable traversal is repeated on every attach`() {
        whenever(visitor.createAndAttachRuntimes(root, path, view)) doReturn false

        repeat(2) { manager.reattachRuntimes(root, path, view) }

        verify(visitor, times(2)).createAndAttachRuntimes(root, path, view)
    }

    @Test
    fun `invalidation requires full traversal even with identical key`() {
        manager.reattachRuntimes(root, path, view)
        clearInvocations(visitor)
        manager.invalidate()

        manager.reattachRuntimes(root, path, view)

        verify(visitor).createAndAttachRuntimes(root, path, view)
    }

    @Test
    fun `non static miss discards the previous static entry`() {
        manager.reattachRuntimes(root, path, view)
        val dynamicRoot = container()
        whenever(visitor.createAndAttachRuntimes(dynamicRoot, path, view)) doReturn false
        manager.reattachRuntimes(dynamicRoot, path, view)
        clearInvocations(visitor)

        manager.reattachRuntimes(root, path, view)

        verify(visitor).createAndAttachRuntimes(root, path, view)
    }

    @Test
    fun `failed traversal never caches an incomplete result`() {
        whenever(visitor.createAndAttachRuntimes(root, path, view)) doThrow
            IllegalStateException("test failure")
        assertFailsWith<IllegalStateException> { manager.reattachRuntimes(root, path, view) }
        doReturn(true).whenever(visitor).createAndAttachRuntimes(root, path, view)
        clearInvocations(visitor)

        manager.reattachRuntimes(root, path, view)

        verify(visitor).createAndAttachRuntimes(root, path, view)
    }

    @Test
    fun `another view can skip traversal for the same key`() {
        manager.reattachRuntimes(root, path, view)
        val otherView = mock<Div2View> { on { runtimeStore } doReturn store }
        clearInvocations(visitor, store)

        manager.reattachRuntimes(root, path, otherView)

        verifyNoInteractions(visitor, store)
    }

}
