package com.yandex.div.core.util

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.player.DivVideoActionHandler
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData

/**
 * Resolves model targets without requiring their views to be bound.
 *
 * The ID, scope, and active-state lookup was extracted from [DivVideoActionHandler] and generalized
 * so extension actions can reuse the established lookup behavior.
 */
@InternalApi
class DivBlockLocator(
    private val state: DivData.State?,
    private val rootResolver: ExpressionResolver,
    private val activeStates: ActiveStateProvider,
) {
    /**
     * Finds a block matching [divId] within [scopeId], when the scope is available.
     *
     * [actionResolver] is the context in which the action runs. If several blocks remain after
     * filtering by scope and active state, its repeated-item data, resolver identity and runtime
     * path help select the block closest to the action source.
     */
    fun findTarget(
        divId: String,
        scopeId: String?,
        actionResolver: ExpressionResolver,
        matches: (DivBlock) -> Boolean,
        reportWarning: (Throwable) -> Unit = {},
    ): Result<DivBlock> {
        val blocks = collectBlocks()
        val activeStatesPaths = activeStates.activeStatesPaths()
        return TargetSearch(
            blocks = blocks,
            sourceResolver = actionResolver,
            sourceItemSegment = actionResolver.asImpl?.itemBuilderData,
            sourcePath = findSourcePath(blocks, actionResolver),
            activeStatesPaths = activeStatesPaths,
        ).find(divId, scopeId, matches, reportWarning)
    }

    private fun collectBlocks(): List<DivBlock> {
        val rootState = state ?: return emptyList()
        return rootState.div.walk(
            resolver = rootResolver,
            path = rootPath(rootState),
        ).toList()
    }

    private fun rootPath(state: DivData.State): DivStatePath {
        if (rootResolver.asImpl != null) {
            return DivStatePath.fromState(state)
        }

        val statePath = DivStatePath.fromState(state.stateId)
        return state.div.value().id?.let { statePath.appendDiv(it) } ?: statePath
    }

    private fun findSourcePath(blocks: List<DivBlock>, resolver: ExpressionResolver): String? {
        // A shared resolver identifies the common ancestor of its blocks.
        val runtimePaths = resolver.asImpl?.runtimeStore?.getUniquePathsAndRuntimes()
            ?.filterValues { it.expressionResolver === resolver }
            ?.keys
            ?.filter { it.isNotEmpty() }
        val paths = runtimePaths?.takeIf { it.isNotEmpty() }
            ?: blocks.filter { it.expressionResolver === resolver }.map { it.path.fullPath }
        return paths.map { it.split('/') }
            .reduceOrNull { common, path ->
                common.zip(path)
                    .takeWhile { (left, right) -> left == right }
                    .map { it.first }
            }
            ?.joinToString("/")
    }

    private class TargetSearch(
        private val blocks: List<DivBlock>,
        private val sourceResolver: ExpressionResolver,
        private val sourceItemSegment: String?,
        private val sourcePath: String?,
        private val activeStatesPaths: Set<DivStatePath>,
    ) {
        fun find(
            id: String,
            scopeId: String?,
            matches: (DivBlock) -> Boolean,
            reportWarning: (Throwable) -> Unit,
        ): Result<DivBlock> {
            if (scopeId == null) {
                return select(blocks, id, matches = matches)
            }
            val scopeResult = select(blocks, scopeId, isScope = true)
            return scopeResult.getOrNull()?.let { scope ->
                val children = scope.div.walk(scope.expressionResolver, scope.path).toList()
                select(children, id, inScope = true, matches = matches)
            } ?: when (val error = scopeResult.exceptionOrNull()) {
                is MissingTarget -> {
                    reportWarning(error)
                    select(blocks, id, matches = matches)
                }
                null -> scopeResult
                else -> Result.failure(error)
            }
        }

        private fun select(
            blocks: List<DivBlock>,
            id: String,
            isScope: Boolean = false,
            inScope: Boolean = false,
            matches: (DivBlock) -> Boolean = { true },
        ): Result<DivBlock> {
            var candidates = blocks.filter { it.div.value().id == id && matches(it) }
            if (candidates.size > 1) {
                candidates = candidates.filter { it.path.isInActiveState() }
            }
            if (candidates.size > 1 && sourceItemSegment != null) {
                val localCandidates = candidates.filter { sourceItemSegment in it.path.fullPath.split('/') }
                if (localCandidates.isNotEmpty()) {
                    candidates = localCandidates
                }
            }
            if (candidates.size > 1) {
                val localCandidates = candidates.filter { it.expressionResolver === sourceResolver }
                if (localCandidates.isNotEmpty()) {
                    candidates = localCandidates
                }
            }
            if (candidates.size > 1 && sourcePath != null) {
                val segments = sourcePath.split('/')
                val ranks = candidates.groupBy { candidate ->
                    candidate.path.fullPath.split('/')
                        .zip(segments)
                        .takeWhile { (target, source) -> target == source }
                        .size
                }
                candidates = ranks.getValue(ranks.keys.max())
            }
            return when {
                candidates.isEmpty() -> Result.failure(MissingTarget(id, isScope, inScope))
                candidates.size > 1 -> Result.failure(DuplicateTarget(id, isScope, inScope))
                else -> Result.success(candidates.single())
            }
        }

        private fun DivStatePath.isInActiveState(): Boolean {
            val stateAncestors = activeStatesPaths.count { activePath ->
                statesString == activePath.statesString || activePath.isAncestorOf(this)
            }
            return stateAncestors == getStates().size
        }
    }

    private class MissingTarget(id: String, isScope: Boolean = false, inScope: Boolean = false) :
        Exception(buildMessage(id, isScope, "not found", inScope))

    private class DuplicateTarget(id: String, isScope: Boolean = false, inScope: Boolean = false) :
        Exception(buildMessage(id, isScope, "is ambiguous", inScope))
}

private fun buildMessage(id: String, isScope: Boolean, error: String, inScope: Boolean): String {
    val target = if (isScope) {
        "Scope"
    } else {
        "Element"
    }
    val suffix = if (inScope) {
        " in scope"
    } else {
        ""
    }
    return "$target with id '$id' $error$suffix"
}
