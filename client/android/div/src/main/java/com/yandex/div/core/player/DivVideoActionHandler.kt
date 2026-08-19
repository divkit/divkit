package com.yandex.div.core.player

import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
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

    scopeId ?: return allBlocks.findSinglePath(id, inScope = false)

    val scopeResult = allBlocks.findSingle<DivBlock>(scopeId, isScope = true, inScope = false)
    val scopeError = scopeResult.exceptionOrNull()
    scopeResult.onSuccess { scope ->
        return scope.div.walk(scope.expressionResolver, scope.path).toList().findSinglePath(id, inScope = true)
    }

    if (scopeError !is ViewLocator.MissingTarget) {
        return Result.failure(scopeError ?: run {
            ViewLocator.DuplicateTarget(scopeId, isScope = true, inScope = false)
        })
    }

    return allBlocks.findSinglePath(id, inScope = false)
        .onSuccess { logWarning(scopeError) }
        .onFailure { if (it is ViewLocator.DuplicateTarget) return Result.failure(scopeError) }
}

private inline fun <reified T : DivBlock> List<DivBlock>.findSingle(
    id: String,
    isScope: Boolean = false,
    inScope: Boolean,
): Result<T> {
    val found = filter { it.div.value().id == id }.filterIsInstance<T>()
    return when {
        found.isEmpty() -> Result.failure(ViewLocator.MissingTarget(id, isScope, inScope))
        found.size > 1 -> Result.failure(ViewLocator.DuplicateTarget(id, isScope, inScope))
        else -> Result.success(found.first())
    }
}

private fun List<DivBlock>.findSinglePath(id: String, inScope: Boolean): Result<DivStatePath> =
    findSingle<DivBlock.Video>(id, inScope = inScope).map { it.path }
