package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

/**
 * Resolves model targets without requiring their views to be bound.
 *
 * Card-specific model traversal and action-source metadata are provided by
 * [DivBlockSearchEnvironment].
 */
@InternalApi
class DivBlockLocator(
    private val environment: DivBlockSearchEnvironment,
) {
    /**
     * Finds a block matching [divId] within [scopeId], when the scope is available.
     * A null [divId] skips ID filtering and selects blocks using [matches].
     *
     * [actionResolver] is the context in which the action runs. If several blocks remain after
     * filtering by scope and active state, its repeated-item data, resolver identity and runtime
     * path help select the block closest to the action source.
     */
    fun findTarget(
        divId: String?,
        scopeId: String?,
        actionResolver: ExpressionResolver,
        matches: (DivBlock) -> Boolean,
        reportWarning: (Throwable) -> Unit = {},
    ): Result<DivBlock> {
        val blocks = collectBlocks()
        return createSearch(blocks, actionResolver).find(divId, scopeId, matches, reportWarning)
    }

    /**
     * Resolves the path of [div] by model identity.
     * Generated models with IDs fall back to matching within [localPath] only when identity is missing.
     */
    fun resolveDivPath(
        div: Div,
        resolver: ExpressionResolver,
        localPath: DivStatePath,
    ): Result<DivStatePath> {
        val divId = div.value().id
        val identityResult = findTarget(
            divId = divId,
            scopeId = null,
            actionResolver = resolver,
            matches = { it.div === div },
        )
        if (divId == null || identityResult.exceptionOrNull() !is MissingTarget) {
            return identityResult.map { it.path }
        }
        return findTarget(
            divId = divId,
            scopeId = null,
            actionResolver = resolver,
            matches = { block ->
                block.path.isSubsetOf(localPath) || localPath.isSubsetOf(block.path)
            },
        ).map { it.path }
    }

    private fun createSearch(blocks: List<DivBlock>, resolver: ExpressionResolver): TargetSearch {
        return TargetSearch(
            blocks = blocks,
            sourceResolver = resolver,
            sourcePath = findSourcePath(blocks, resolver),
            activeStatesPaths = environment.activeStatesPaths(),
        )
    }

    private fun collectBlocks(): List<DivBlock> {
        val root = environment.rootBlock() ?: return emptyList()
        return environment.findDescendants(root).toList()
    }

    private fun findSourcePath(blocks: List<DivBlock>, resolver: ExpressionResolver): List<String>? {
        // A shared resolver identifies the common ancestor of its blocks.
        val environmentPaths = environment.findPathsFor(resolver)
        val paths = environmentPaths.takeIf { it.isNotEmpty() }
            ?: blocks.filter { it.expressionResolver === resolver }.map { it.path }
        return paths.map { it.fullPath.split('/') }
            .reduceOrNull { common, path ->
                common.zip(path)
                    .takeWhile { (left, right) -> left == right }
                    .map { it.first }
            }
    }

    private inner class TargetSearch(
        private val blocks: List<DivBlock>,
        private val sourceResolver: ExpressionResolver,
        private val sourcePath: List<String>?,
        private val activeStatesPaths: Set<DivStatePath>,
    ) {
        fun find(
            id: String?,
            scopeId: String?,
            matches: (DivBlock) -> Boolean,
            reportWarning: (Throwable) -> Unit,
        ): Result<DivBlock> {
            if (scopeId == null) {
                return select(blocks, id, matches = matches)
            }
            val scopeResult = select(blocks, scopeId, isScope = true)
            return scopeResult.getOrNull()?.let { scope ->
                val children = environment.findDescendants(scope).toList()
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
            id: String?,
            isScope: Boolean = false,
            inScope: Boolean = false,
            matches: (DivBlock) -> Boolean = { true },
        ): Result<DivBlock> {
            val candidates = blocks.filter { (id == null || it.div.value().id == id) && matches(it) }
            return selectCandidates(candidates, id, isScope, inScope)
        }

        private fun selectCandidates(
            blocks: List<DivBlock>,
            id: String?,
            isScope: Boolean = false,
            inScope: Boolean = false,
        ): Result<DivBlock> {
            var candidates = blocks
            if (candidates.size > 1) {
                candidates = candidates.filter { it.path.isInActiveState() }
            }
            // A segment match is a heuristic for preferring the action's repeated item.
            // When no candidate matches, preserve all candidates for the remaining checks.
            val itemSegment = environment.itemBuilderSegment(sourceResolver)
            if (candidates.size > 1 && itemSegment != null) {
                val localCandidates = candidates.filter { itemSegment in it.path.fullPath.split('/') }
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
                val ranks = candidates.groupBy { candidate ->
                    candidate.path.fullPath.split('/')
                        .zip(sourcePath)
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

    private class MissingTarget(id: String?, isScope: Boolean = false, inScope: Boolean = false) :
        Exception(buildMessage(id, isScope, "not found", inScope))

    private class DuplicateTarget(id: String?, isScope: Boolean = false, inScope: Boolean = false) :
        Exception(buildMessage(id, isScope, "is ambiguous", inScope))
}

private fun buildMessage(id: String?, isScope: Boolean, error: String, inScope: Boolean): String {
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
    val identifier = id?.let { "with id '$it'" } ?: "without id"
    return "$target $identifier $error$suffix"
}
