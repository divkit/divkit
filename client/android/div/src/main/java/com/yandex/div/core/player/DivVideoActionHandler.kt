package com.yandex.div.core.player

import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.getDefaultState
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVideoViewState
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.DivBlock
import javax.inject.Inject

@DivScope
internal class DivVideoActionHandler @Inject constructor() {

    fun handleAction(
        div2View: Div2View,
        divId: String,
        scopeId: String?,
        action: String,
    ): Boolean {
        val state = when (action) {
            START_COMMAND -> DivVideoPlaybackState.PLAYING
            PAUSE_COMMAND -> DivVideoPlaybackState.PAUSED
            else -> {
                KAssert.fail { "No such video action: $action" }
                return false
            }
        }

        val path = div2View.findVideoPath(divId, scopeId)
            .onFailure { div2View.logActionError("video", it) }
            .getOrNull() ?: return false

        div2View.viewStateStore.put(path.fullPath, DivVideoViewState(state))
        return true
    }

    companion object {
        const val START_COMMAND = "start"
        const val PAUSE_COMMAND = "pause"
    }
}

private fun Div2View.findVideoPath(id: String, scopeId: String?): Result<DivStatePath> {
    val root = rootDiv() ?: return Result.failure(
        ViewLocator.MissingTarget(id, isScope = false, inScope = false)
    )
    val allBlocks = root.walk(expressionResolver, currentRootPath).toList()
    val activeStatePaths = getActiveStatePaths(allBlocks)

    scopeId ?: return allBlocks.findSinglePath(id, activeStatePaths, inScope = false)

    val scopeResult = allBlocks.findSingle<DivBlock>(scopeId, activeStatePaths, isScope = true, inScope = false)
    val scopeError = scopeResult.exceptionOrNull()
    scopeResult.onSuccess { scope ->
        return scope.div.walk(scope.expressionResolver, scope.path)
            .toList()
            .findSinglePath(id, activeStatePaths, inScope = true)
    }

    if (scopeError !is ViewLocator.MissingTarget) {
        return Result.failure(scopeError ?: run {
            ViewLocator.DuplicateTarget(scopeId, isScope = true, inScope = false)
        })
    }

    return allBlocks.findSinglePath(id, activeStatePaths, inScope = false)
        .onSuccess { logWarning(scopeError) }
        .onFailure { if (it is ViewLocator.DuplicateTarget) return Result.failure(scopeError) }
}

/**
 * Builds the full path of the selected branch for every state block.
 *
 * State resolution mirrors [DivStateBinder], so nested blocks with the same id can later be
 * distinguished by whether all state segments in their path belong to selected branches.
 */
private fun Div2View.getActiveStatePaths(blocks: List<DivBlock>): Set<String> {
    return blocks.filterIsInstance<DivBlock.State>().mapNotNullTo(mutableSetOf()) { block ->
        val divId = block.divValue.getId()
        val statePath = "${block.path.statesString}/$divId"
        val stateId = dataComponent.stateManager.getState(
            block.divValue,
            block.expressionResolver,
            statePath,
        )
        val selectedStateId = block.divValue.states.find { it.stateId == stateId }?.stateId
            ?: block.divValue.getDefaultState(block.expressionResolver)
                ?.stateId
        selectedStateId?.let { "$statePath/$it" }
    }
}

/**
 * Returns `true` when this block belongs to the selected branch of every enclosing state.
 *
 * Each matching active path accounts for one state segment in [DivStatePath]; matching all of
 * them means that none of the block's ancestors is an inactive state branch.
 */
private fun DivStatePath.isInActiveState(activeStatePaths: Set<String>): Boolean {
    val fullStatePath = statesString
    val stateAncestors = activeStatePaths.count { activePath ->
        fullStatePath == activePath || fullStatePath.startsWith("$activePath/")
    }
    return stateAncestors == getStates().size
}

private inline fun <reified T : DivBlock> List<DivBlock>.findSingle(
    id: String,
    activeStatePaths: Set<String>,
    isScope: Boolean = false,
    inScope: Boolean,
): Result<T> {
    val found = filter { it.div.value().id == id }.filterIsInstance<T>()
    val candidates = if (found.size == 1) {
        found
    } else {
        found.filter { it.path.isInActiveState(activeStatePaths) }
    }
    return when {
        candidates.isEmpty() -> Result.failure(ViewLocator.MissingTarget(id, isScope, inScope))
        candidates.size > 1 -> Result.failure(ViewLocator.DuplicateTarget(id, isScope, inScope))
        else -> Result.success(candidates.first())
    }
}

private fun List<DivBlock>.findSinglePath(
    id: String,
    activeStatePaths: Set<String>,
    inScope: Boolean,
): Result<DivStatePath> = findSingle<DivBlock.Video>(id, activeStatePaths, inScope = inScope).map { it.path }
