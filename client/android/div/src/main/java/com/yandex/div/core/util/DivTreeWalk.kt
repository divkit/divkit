package com.yandex.div.core.util

import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.itemsToDivItemBuilderResult
import com.yandex.div.internal.core.statesToDivItemBuilderResult
import com.yandex.div.internal.core.toItemBuilderResult
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

/**
 * Gets a sequence for visiting this [Div] and all its children.
 */
internal fun Div.walk(resolver: ExpressionResolver): DivTreeWalk {
    return DivTreeWalk(this, resolver)
}

internal class DivTreeWalk private constructor(
    private val root: Div,
    private val resolver: ExpressionResolver,
    private val onEnter: ((Div) -> Boolean)?,
    private val onLeave: ((Div) -> Unit)?,
    private val maxDepth: Int = Int.MAX_VALUE
) : Sequence<DivItemBuilderResult> {

    internal constructor(root: Div, resolver: ExpressionResolver) : this(root, resolver, null, null)

    override fun iterator(): Iterator<DivItemBuilderResult> = DivTreeWalkIterator(root, resolver)

    /**
     * Sets a [predicate], that is called on any entered container div (div-container, div-gallery, etc.)
     * before its children are visited, and before it is visited itself.
     *
     * If the [predicate] returns `false` the div is not entered and neither it nor its children are visited.
     */
    fun onEnter(predicate: (Div) -> Boolean): DivTreeWalk {
        return DivTreeWalk(root, resolver, onEnter = predicate, onLeave = onLeave, maxDepth = maxDepth)
    }

    /**
     * Sets a callback [function], that is called on any left container div after its children are visited,
     * and after it is visited itself.
     */
    fun onLeave(function: (Div) -> Unit): DivTreeWalk {
        return DivTreeWalk(root, resolver, onEnter = onEnter, onLeave = function, maxDepth = maxDepth)
    }

    /**
     * Sets the maximum [depth] of a div tree to traverse. By default there is no limit.
     *
     * The value must be positive.
     *
     * With a value of 1, walker visits only the origin div and all its immediate children,
     * with a value of 2 also grandchildren, etc.
     */
    fun maxDepth(depth: Int): DivTreeWalk {
        if (depth <= 0) {
            throw IllegalArgumentException("depth must be positive, but was $depth.")
        }

        return DivTreeWalk(root, resolver, onEnter, onLeave, depth)
    }

    private inner class DivTreeWalkIterator(
        private val root: Div,
        private val resolver: ExpressionResolver
    ) : AbstractIterator<DivItemBuilderResult>() {

        private val stack = ArrayDeque<Node>().apply {
            addLast(node(root.toItemBuilderResult(resolver)))
        }

        override fun computeNext() {
            val nextItem = nextItem()
            if (nextItem != null) {
                setNext(nextItem)
            } else {
                done()
            }
        }

        private fun nextItem(): DivItemBuilderResult? {
            val node = stack.lastOrNull() ?: return null
            val item = node.step()
            return if (item == null) {
                stack.removeLast()
                nextItem()
            } else if (item === node.item || item.div.isLeaf || stack.size >= maxDepth) {
                item
            } else {
                stack.addLast(node(item))
                nextItem()
            }
        }

        private fun node(item: DivItemBuilderResult): Node {
            return if (item.div.isBranch) {
                BranchNode(item, onEnter, onLeave)
            } else {
                LeafNode(item)
            }
        }
    }

    private interface Node {

        val item: DivItemBuilderResult

        fun step(): DivItemBuilderResult?
    }

    private class LeafNode(
        override val item: DivItemBuilderResult,
    ) : Node {

        private var visited = false

        override fun step(): DivItemBuilderResult? {
            if (visited) {
                return null
            }

            visited = true
            return item
        }
    }

    private class BranchNode(
        override val item: DivItemBuilderResult,
        private val onEnter: ((Div) -> Boolean)?,
        private val onLeave: ((Div) -> Unit)?
    ) : Node {

        private var rootVisited = false
        private var children: List<DivItemBuilderResult>? = null
        private var childIndex = 0

        override fun step(): DivItemBuilderResult? {
            if (!rootVisited) {
                if (onEnter?.invoke(item.div) == false) {
                    return null
                }
                rootVisited = true
                return item
            }

            var children = this.children
            if (children == null) {
                children = item.div.getItems(item.expressionResolver)
                this.children = children
            }

            return if (childIndex < children.size) {
                children[childIndex++]
            } else {
                onLeave?.invoke(item.div)
                null
            }
        }
    }
}

private fun Div.getItems(resolver: ExpressionResolver): List<DivItemBuilderResult> {
    return when (this) {
        is Div.Text -> emptyList()
        is Div.Image -> emptyList()
        is Div.GifImage -> emptyList()
        is Div.Separator -> emptyList()
        is Div.Indicator -> emptyList()
        is Div.Slider -> emptyList()
        is Div.Input -> emptyList()
        is Div.Custom -> emptyList()
        is Div.Select -> emptyList()
        is Div.Video -> emptyList()
        is Div.Container -> value.buildItems(resolver)
        is Div.Grid -> value.itemsToDivItemBuilderResult(resolver)
        is Div.Gallery -> value.buildItems(resolver)
        is Div.Pager -> value.buildItems(resolver)
        is Div.Tabs -> value.itemsToDivItemBuilderResult(resolver)
        is Div.State -> value.statesToDivItemBuilderResult(resolver)
    }
}
