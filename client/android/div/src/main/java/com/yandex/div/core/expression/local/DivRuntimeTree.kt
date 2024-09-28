package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.getChildPathUnit
import com.yandex.div2.Div

internal class DivRuntimeTree(
    rootDiv: Div,
    rootPath: DivStatePath,
) {
    private val rootNode = buildTree(rootDiv, rootPath)

    internal fun accept(visitor: ExpressionRuntimeVisitor) {
        visitor.visit(rootNode)
    }

    private fun buildTree(rootDiv: Div, path: DivStatePath): Node {
        return when (rootDiv) {
            is Div.Container -> buildNodeForContainer(rootDiv, rootDiv.value.items, path)
            is Div.Grid -> buildNodeForContainer(rootDiv, rootDiv.value.items, path)
            is Div.Gallery -> buildNodeForContainer(rootDiv, rootDiv.value.items, path)
            is Div.Pager -> buildNodeForContainer(rootDiv, rootDiv.value.items, path)
            is Div.State -> buildNodeForContainer(rootDiv, rootDiv.value.states.map { it.div }, path)
            is Div.Tabs -> buildNodeForContainer(rootDiv, rootDiv.value.items.map { it.div }, path)
            else -> Node(rootDiv, path, emptyList())
        }
    }

    private fun buildNodeForContainer(div: Div, data: List<Div?>?, path: DivStatePath): Node {
        val children = mutableListOf<Node>()
        data?.forEachIndexed { index, div ->
            if (div != null) children.add(buildChildNode(div, index, path))
        }
        return Node(div, path, children)
    }

    private fun buildChildNode(div: Div, index: Int, path: DivStatePath): Node {
        val id = div.value().getChildPathUnit(index)
        return buildTree(div, path.appendDiv(id))
    }

    internal class Node(
        internal val div: Div,
        internal val path: DivStatePath,
        internal val children: List<Node>,
        internal var parentRuntime: ExpressionsRuntime? = null,
    )
}
