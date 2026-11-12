package com.yandex.div.core.util

import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.DivBlockSearchEnvironment
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData

internal class ViewDivBlockSearchEnvironment(
    private val divView: Div2View,
) : DivBlockSearchEnvironment {

    override fun rootBlock(): DivBlock? {
        val state = divView.currentCardState() ?: return null
        val resolver = divView.expressionResolver
        val path = rootPath(state, resolver)
        val rootResolver = resolver.asImpl?.runtimeStore
            ?.getOrCreateRuntime(path.fullPath, state.div, resolver)
            ?.expressionResolver ?: resolver
        return DivBlock.create(state.div, rootResolver, path)
    }

    override fun findDescendants(root: DivBlock): Iterable<DivBlock> {
        val blocks = mutableListOf<DivBlock>()
        val pending = ArrayDeque<DivBlock>()
        pending.addLast(root)
        while (pending.isNotEmpty()) {
            val block = pending.removeLast()
            blocks.add(block)
            pending.addAll(block.div.getChildBlocks(block.expressionResolver, block.path).asReversed())
        }
        return blocks
    }

    override fun findPathsFor(resolver: ExpressionResolver): Collection<DivStatePath> {
        return resolver.asImpl?.runtimeStore?.getUniquePathsAndRuntimes()
            ?.filterValues { it.expressionResolver === resolver }
            ?.keys
            ?.mapNotNull(String::toRuntimePathOrNull)
            .orEmpty()
    }

    override fun activeStatesPaths(): Set<DivStatePath> {
        val root = rootBlock() ?: return emptySet()
        return findDescendants(root)
            .filterIsInstance<DivBlock.State>()
            .mapNotNullTo(mutableSetOf()) { block ->
                val statePath = "${block.path.statesString}/${block.divValue.getId()}"
                val stateId = divView.dataComponent.stateManager.getState(
                    block.divValue, block.expressionResolver, statePath,
                )
                val selectedStateId = block.divValue.states.find { it.stateId == stateId }?.stateId
                    ?: block.divValue.getDefaultState(block.expressionResolver)?.stateId
                selectedStateId?.let { DivStatePath.parse("$statePath/$it") }
            }
    }

    override fun itemBuilderSegment(resolver: ExpressionResolver): String? =
        resolver.asImpl?.itemBuilderData

    private fun rootPath(state: DivData.State, resolver: ExpressionResolver): DivStatePath {
        if (resolver.asImpl != null) {
            return divView.currentRootPath
        }
        val path = DivStatePath.fromState(state.stateId)
        val root = state.div
        val id = (root as? Div.State)?.value?.divId ?: root.value().id
        return id?.let(path::appendDiv) ?: path
    }

    private fun Div2View.currentCardState(): DivData.State? {
        return divData?.states?.firstOrNull { it.stateId == stateId }
    }
}

private fun String.toRuntimePathOrNull(): DivStatePath? {
    if (isEmpty()) {
        return null
    }
    val segments = split('/')
    val stateId = segments.first().substringBefore(':').toLongOrNull() ?: return null
    return DivStatePath(stateId, path = segments)
}
