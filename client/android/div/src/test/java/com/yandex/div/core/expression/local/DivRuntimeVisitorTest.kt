package com.yandex.div.core.expression.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.container
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.state
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.json.JSONArray
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class DivRuntimeVisitorTest {

    private val stateManager = mock<DivStateManager> {
        on { getState(any<DivState>(), any(), any()) } doReturn "s1"
    }
    private val runtimeStore = mock<RuntimeStore> {
        on { rootRuntime } doReturn ExpressionsRuntime(mock())
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
    }
    private val divView = mock<Div2View> {
        on { runtimeStore } doReturn runtimeStore
    }
    private val visitor = DivRuntimeVisitor(stateManager, mock())
    private val path = DivStatePath(0)
    private val inner = state(id = "inner", states = listOf(state(stateId = "x", div = text(text = "x"))))
    private val outer = state(
        id = "outer",
        states = listOf(state(stateId = "s1", div = container(items = listOf(inner)))),
    )

    @Test
    fun `nested state inside root state is looked up by state id`() {
        val rootPath = DivStatePath.fromState(0).withNamespace("tooltip#1#t").appendDiv("tooltip#t#outer")

        DivRuntimeVisitor(stateManager, mock()).createAndAttachRuntimes(outer, rootPath, divView)

        val keys = argumentCaptor<String>()
        verify(stateManager, times(2)).getState(any<DivState>(), any(), keys.capture())
        assertEquals(listOf("tooltip#1#t/0/outer", "tooltip#1#t/0/outer/s1/inner"), keys.allValues)
    }

    @Test
    fun `each invocation traverses nested state nodes and cannot skip reattachment`() {
        val leaf = text(text = "value")
        val root = state(states = listOf(state(stateId = "s1", div = leaf)))

        repeat(2) {
            clearInvocations(runtimeStore)

            val canSkip = visitor.createAndAttachRuntimes(root, path, divView)

            assertFalse(canSkip)
            val nodes = argumentCaptor<Div>()
            verify(runtimeStore, times(2)).getOrCreateRuntime(any(), nodes.capture(), any())
            assertEquals(listOf(root, leaf), nodes.allValues)
        }
    }

    @Test
    fun `runtime lookups follow store replacement during a root attach callback`() {
        val root = container(items = listOf(text(text = "value")))
        val oldTriggers = mock<TriggersController>()
        val oldRuntime = ExpressionsRuntime(mock(), null, oldTriggers)
        whenever(runtimeStore.getOrCreateRuntime(any(), any(), any())).thenReturn(oldRuntime)
        val currentStore = mock<RuntimeStore> {
            on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
        }
        val rootTriggers = mock<TriggersController>()
        whenever(runtimeStore.rootRuntime)
            .thenReturn(ExpressionsRuntime(mock(), null, rootTriggers))
        doAnswer {
            whenever(divView.runtimeStore).thenReturn(currentStore)
            null
        }.whenever(rootTriggers).onAttachedToWindow(divView)

        val canSkip = visitor.createAndAttachRuntimes(root, path, divView)

        assertFalse(canSkip)
        verify(oldTriggers, never()).onAttachedToWindow(any())
        verify(runtimeStore, never()).getOrCreateRuntime(any(), any(), any())
        verify(currentStore, times(2)).getOrCreateRuntime(any(), any(), any())
    }

    @Test
    fun `static nested nodes allow skipping only subsequent traversals`() {
        val leaf = text(text = "value")
        val nested = container(items = listOf(leaf))
        val root = gallery(items = listOf(nested))

        val canSkip = visitor.createAndAttachRuntimes(root, path, divView)

        assertTrue(canSkip)
        val nodes = argumentCaptor<Div>()
        verify(runtimeStore, times(3)).getOrCreateRuntime(any(), nodes.capture(), any())
        assertEquals(listOf(root, nested, leaf), nodes.allValues)
    }

    @Test
    fun `root runtime with attach work prevents skipping and receives its callback`() {
        val triggers = mock<TriggersController>()
        whenever(runtimeStore.rootRuntime) doReturn ExpressionsRuntime(mock(), null, triggers)

        val canSkip = visitor.createAndAttachRuntimes(container(), path, divView)

        assertFalse(canSkip)
        verify(triggers).onAttachedToWindow(divView)
    }

    @Test
    fun `deep runtime with attach work prevents skipping the whole traversal`() {
        val leaf = text(text = "value")
        val triggers = mock<TriggersController>()
        whenever(runtimeStore.getOrCreateRuntime(any(), eq(leaf), any())) doReturn
            ExpressionsRuntime(mock(), null, triggers)
        val root = container(items = listOf(container(items = listOf(leaf))))

        val canSkip = visitor.createAndAttachRuntimes(root, path, divView)

        assertFalse(canSkip)
        verify(triggers).onAttachedToWindow(divView)
    }

    @Test
    fun `container with attach work still attaches all descendants`() {
        val leaf = text(text = "value")
        val nested = container(items = listOf(leaf))
        val root = container(items = listOf(nested))
        val rootTriggers = mock<TriggersController>()
        val leafTriggers = mock<TriggersController>()
        whenever(runtimeStore.getOrCreateRuntime(any(), eq(root), any())) doReturn
            ExpressionsRuntime(mock(), null, rootTriggers)
        whenever(runtimeStore.getOrCreateRuntime(any(), eq(leaf), any())) doReturn
            ExpressionsRuntime(mock(), null, leafTriggers)

        assertFalse(visitor.createAndAttachRuntimes(root, path, divView))

        verify(rootTriggers).onAttachedToWindow(divView)
        verify(leafTriggers).onAttachedToWindow(divView)
        val nodes = argumentCaptor<Div>()
        verify(runtimeStore, times(3)).getOrCreateRuntime(any(), nodes.capture(), any())
        assertEquals(listOf(root, nested, leaf), nodes.allValues)
    }

    @Test
    fun `non skippable sibling does not prevent remaining branch attach`() {
        val first = text(text = "first")
        val last = text(text = "last")
        val nested = container(items = listOf(last))
        val root = container(items = listOf(first, nested))
        val firstTriggers = mock<TriggersController>()
        val lastTriggers = mock<TriggersController>()
        whenever(runtimeStore.getOrCreateRuntime(any(), eq(first), any())) doReturn
            ExpressionsRuntime(mock(), null, firstTriggers)
        whenever(runtimeStore.getOrCreateRuntime(any(), eq(last), any())) doReturn
            ExpressionsRuntime(mock(), null, lastTriggers)

        assertFalse(visitor.createAndAttachRuntimes(root, path, divView))

        verify(firstTriggers).onAttachedToWindow(divView)
        verify(lastTriggers).onAttachedToWindow(divView)
        val nodes = argumentCaptor<Div>()
        verify(runtimeStore, times(4)).getOrCreateRuntime(any(), nodes.capture(), any())
        assertEquals(listOf(root, first, nested, last), nodes.allValues)
    }

    @Test
    fun `nested traversal cannot overwrite the outer skip result`() {
        val nestedStore = mock<RuntimeStore> {
            on { rootRuntime } doReturn ExpressionsRuntime(mock())
            on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
        }
        val nestedView = mock<Div2View> {
            on { runtimeStore } doReturn nestedStore
        }
        val triggers = mock<TriggersController>()
        whenever(runtimeStore.rootRuntime) doReturn ExpressionsRuntime(mock(), null, triggers)
        doAnswer {
            assertTrue(visitor.createAndAttachRuntimes(container(), path, nestedView))
            null
        }.whenever(triggers).onAttachedToWindow(divView)

        assertFalse(visitor.createAndAttachRuntimes(container(), path, divView))

        verify(triggers).onAttachedToWindow(divView)
    }

    @Test
    fun `custom leaf without attach work allows skipping repeated traversal`() {
        val root = container(items = listOf(Div.Custom(mock())))

        assertTrue(visitor.createAndAttachRuntimes(root, path, divView))
    }

    @Test
    fun `custom leaf with triggers prevents skipping repeated traversal`() {
        val custom = Div.Custom(mock())
        val triggers = mock<TriggersController>()
        whenever(runtimeStore.getOrCreateRuntime(any(), eq(custom), any())) doReturn
            ExpressionsRuntime(mock(), null, triggers)

        assertFalse(visitor.createAndAttachRuntimes(container(items = listOf(custom)), path, divView))

        verify(triggers).onAttachedToWindow(divView)
    }

    @Test
    fun `selected tabs prevent skipping the whole traversal`() {
        val root = container(items = listOf(tabs(items = listOf(
            tabItem(text(text = "first"), "First"),
            tabItem(text(text = "second"), "Second"),
        ))))

        assertFalse(visitor.createAndAttachRuntimes(root, path, divView))
        verify(runtimeStore).traverseFrom(any(), any(), any())
    }

    @Test
    fun `container item builder prevents skipping even when it produces no nodes`() {
        val builder = DivCollectionItemBuilder(
            Expression.constant(JSONArray()), prototypes = emptyList(),
        )
        val root = Div.Container(DivContainer(itemBuilder = builder))

        assertFalse(visitor.createAndAttachRuntimes(root, path, divView))
    }

    @Test
    fun `gallery item builder prevents skipping even when it produces no nodes`() {
        val builder = DivCollectionItemBuilder(
            Expression.constant(JSONArray()), prototypes = emptyList(),
        )
        val root = Div.Gallery(DivGallery(itemBuilder = builder))

        assertFalse(visitor.createAndAttachRuntimes(root, path, divView))
    }

    @Test
    fun `pager item builder prevents skipping even when it produces no nodes`() {
        val builder = DivCollectionItemBuilder(
            Expression.constant(JSONArray()), prototypes = emptyList(),
        )
        val root = Div.Pager(DivPager(
            layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
            itemBuilder = builder,
        ))

        assertFalse(visitor.createAndAttachRuntimes(root, path, divView))
    }

    @Test
    fun `non skippable traversal does not affect the next independent traversal`() {
        visitor.createAndAttachRuntimes(outer, path, divView)

        assertTrue(visitor.createAndAttachRuntimes(container(), path, divView))
    }
}
