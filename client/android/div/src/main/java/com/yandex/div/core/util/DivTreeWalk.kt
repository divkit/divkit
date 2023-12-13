package com.yandex.div.core.util

import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
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
) : Sequence<Div> {

    internal constructor(root: Div, resolver: ExpressionResolver) : this(root, resolver, null, null)

    override fun iterator(): Iterator<Div> = DivTreeWalkIterator(root, resolver)

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
    ) : AbstractIterator<Div>() {

        private val stack = ArrayDeque<Node>().apply {
            addLast(node(root, resolver))
        }

        override fun computeNext() {
            val nextDiv = nextDiv()
            if (nextDiv != null) {
                setNext(nextDiv)
            } else {
                done()
            }
        }

        private fun nextDiv(): Div? {
            val node = stack.lastOrNull() ?: return null
            val div = node.step()
            return if (div == null) {
                stack.removeLast()
                nextDiv()
            } else if (div == node.div || div.isLeaf || stack.size >= maxDepth) {
                div
            } else {
                stack.addLast(node(div, resolver))
                nextDiv()
            }
        }

        private fun node(div: Div, resolver: ExpressionResolver): Node {
            return if (div.isBranch) {
                BranchNode(div, resolver, onEnter, onLeave)
            } else {
                LeafNode(div, resolver)
            }
        }
    }

    private interface Node {

        val div: Div
        val resolver: ExpressionResolver

        fun step(): Div?
    }

    private class LeafNode(
        override val div: Div,
        override val resolver: ExpressionResolver
    ) : Node {

        private var visited = false

        override fun step(): Div? {
            if (visited) {
                return null
            }

            visited = true
            return div
        }
    }

    private class BranchNode(
        override val div: Div,
        override val resolver: ExpressionResolver,
        private val onEnter: ((Div) -> Boolean)?,
        private val onLeave: ((Div) -> Unit)?
    ) : Node {

        private var rootVisited = false
        private var children: List<Div>? = null
        private var childIndex = 0

        override fun step(): Div? {
            if (!rootVisited) {
                if (onEnter?.invoke(div) == false) {
                    return null
                }
                rootVisited = true
                return div
            }

            var children = this.children
            if (children == null) {
                children = div.getItems(resolver)
                this.children = children
            }

            return if (childIndex < children.size) {
                children[childIndex++]
            } else {
                onLeave?.invoke(div)
                null
            }
        }
    }
}

private fun Div.getItems(resolver: ExpressionResolver): List<Div> {
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
        is Div.Grid -> value.nonNullItems
        is Div.Gallery -> value.nonNullItems
        is Div.Pager -> value.nonNullItems
        is Div.Tabs -> value.items.map { tab -> tab.div }
        is Div.State -> value.states.mapNotNull { state -> state.div }
    }
}
