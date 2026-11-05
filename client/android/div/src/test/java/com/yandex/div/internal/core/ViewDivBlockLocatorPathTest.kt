package com.yandex.div.internal.core

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.ViewDivBlockSearchEnvironment
import com.yandex.div.internal.core.DivBlockLocator
import com.yandex.div.internal.core.DivBlockSearchEnvironment
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ViewDivBlockLocatorPathTest {
    private val resolver = ExpressionResolver.EMPTY
    private val localPath = DivStatePath.fromState(0)

    @Test
    fun `View retains original child segments including hidden siblings`() {
        val store = mock<RuntimeStore>()
        val viewResolver = mock<ExpressionResolverImpl> { on { runtimeStore } doReturn store }
        whenever(store.getOrCreateRuntime(any(), any(), any())) doReturn ExpressionsRuntime(viewResolver)
        val paths = createLocator(siblings, viewResolver)

        val segments = resolveChildSegments(paths, DivStatePath.fromState(0, "root"))

        assertEquals(listOf("child#0", "child#1", "duplicate#2", "duplicate#3"), segments)
    }

    @Test
    fun `source runtime path selects the nearest occurrence of a shared model`() {
        val store = mock<RuntimeStore>()
        val rootResolver = mock<ExpressionResolverImpl> { on { runtimeStore }.thenReturn(store) }
        val actionResolver = mock<ExpressionResolverImpl> { on { runtimeStore }.thenReturn(store) }
        whenever(store.getOrCreateRuntime(any(), any(), any())).thenReturn(ExpressionsRuntime(rootResolver))
        whenever(store.getUniquePathsAndRuntimes()).thenReturn(mapOf(
            "0:root/right/source" to ExpressionsRuntime(actionResolver),
        ))
        val target = text(id = "target", text = "Target")
        val root = container(id = "root", items = listOf(
            container(id = "left", items = listOf(target)),
            container(id = "right", items = listOf(target)),
        ))
        val locator = createLocator(root, rootResolver)

        val path = locator.resolveDivPath(target, actionResolver, DivStatePath.fromState(0, "root"))

        assertEquals("0:root/right/target", path.getOrThrow().fullPath)
    }

    private val siblings = container(
        id = "root",
        items = listOf(
            text(text = "Hidden", visibility = constant(DivVisibility.GONE)),
            text(text = "Visible"),
            text(id = "duplicate", text = "First"),
            text(id = "duplicate", text = "Second"),
        ),
    ) as Div.Container

    private fun resolveChildSegments(locator: DivBlockLocator, parentPath: DivStatePath): List<String> {
        val localPaths = listOf(parentPath, parentPath,
            parentPath.appendDiv("duplicate#2"), parentPath.appendDiv("duplicate#3"))
        return siblings.value.items!!.zip(localPaths).map { (div, path) ->
            locator.resolveDivPath(div, resolver, path).getOrThrow().lastDivId
        }
    }

    private fun createLocator(
        root: Div,
        rootResolver: ExpressionResolver = resolver,
        activeStates: Set<DivStatePath> = emptySet(),
    ): DivBlockLocator {
        return DivBlockLocator(
            environment = createEnvironment(root, rootResolver, activeStates),
        )
    }

    private fun createEnvironment(
        root: Div,
        rootResolver: ExpressionResolver,
        activeStates: Set<DivStatePath>,
    ): DivBlockSearchEnvironment {
        val environment = ViewDivBlockSearchEnvironment(mock())
        return object : DivBlockSearchEnvironment by environment {
            override fun activeStatesPaths(): Set<DivStatePath> = activeStates

            override fun rootBlock(): DivBlock {
                val path = DivStatePath.fromState(0, root.value().id)
                return DivBlock.create(root, rootResolver, path)
            }
        }
    }
}
