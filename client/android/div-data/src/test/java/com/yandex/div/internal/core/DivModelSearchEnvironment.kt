package com.yandex.div.internal.core

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData

internal class DivModelSearchEnvironment(
    private val state: DivData.State?,
    private val rootResolver: ExpressionResolver,
    private val activeStates: Set<DivStatePath> = emptySet(),
) : DivBlockSearchEnvironment {

    override fun rootBlock(): DivBlock? {
        val rootState = state ?: return null
        return DivBlock.create(rootState.div, rootResolver, rootPath(rootState))
    }

    override fun findDescendants(root: DivBlock): Iterable<DivBlock> {
        val blocks = mutableListOf<DivBlock>()
        val pending = ArrayDeque<DivBlock>()
        pending.addLast(root)
        while (pending.isNotEmpty()) {
            val block = pending.removeLast()
            blocks.add(block)
            pending.addAll(children(block).asReversed())
        }
        return blocks
    }

    override fun activeStatesPaths(): Set<DivStatePath> = activeStates

    override fun findPathsFor(resolver: ExpressionResolver): Collection<DivStatePath> = emptyList()

    override fun itemBuilderSegment(resolver: ExpressionResolver): String? = null

    private fun rootPath(state: DivData.State): DivStatePath {
        val path = DivStatePath.fromState(state.stateId)
        val root = state.div
        val id = (root as? Div.State)?.value?.divId ?: root.value().id
        return id?.let(path::appendDiv) ?: path
    }

    private fun children(block: DivBlock): List<DivBlock> {
        val div = block.div
        if (div is Div.State) {
            val id = div.value.divId ?: div.value.id.orEmpty()
            return div.value.states.mapNotNull { state ->
                val child = state.div ?: return@mapNotNull null
                val path = block.path.append(id, state.stateId, child.value().id ?: state.stateId)
                DivBlock.create(child, block.expressionResolver, path)
            }
        }
        val items = when (div) {
            is Div.Container -> div.value.items.takeIf { div.value.itemBuilder == null }
            is Div.Gallery -> div.value.items.takeIf { div.value.itemBuilder == null }
            is Div.Pager -> div.value.items.takeIf { div.value.itemBuilder == null }
            is Div.Grid -> div.value.items
            is Div.Custom -> div.value.items
            is Div.Tabs -> div.value.items.map { it.div }
            else -> null
        }.orEmpty()
        val idCounts = items.mapNotNull { it.pathId() }.groupingBy { it }.eachCount()
        return items.mapIndexed { index, child ->
            val id = child.pathId()
            val segment = when {
                id == null -> "child#$index"
                idCounts.getValue(id) > 1 -> "$id#$index"
                else -> id
            }
            DivBlock.create(child, block.expressionResolver, block.path.appendDiv(segment))
        }
    }
}

private fun Div.pathId(): String? = when (this) {
    is Div.State -> value.divId ?: value.id.orEmpty()
    else -> value().id
}
