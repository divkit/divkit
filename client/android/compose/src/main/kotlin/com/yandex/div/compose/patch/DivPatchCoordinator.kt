package com.yandex.div.compose.patch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.doOnPreDraw
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.context.CompositionKeyStorage
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.internal.patch.DivPatchApply
import com.yandex.div.internal.patch.DivPatchMap
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import javax.inject.Inject

/**
 * Coordinates patch application with Compose updates: keeps old component aliases until the
 * updated layout is applied, then releases obsolete components and runs success actions.
 */
@DivViewScope
internal class DivPatchCoordinator @Inject constructor(
    private val compositionKeyStorage: CompositionKeyStorage,
    private val localComponentStorage: DivLocalComponentStorage,
    private val states: MutableState<List<DivData.State>>
) {
    var revision: Long by mutableLongStateOf(0L)
        private set

    private val pendingActions = ArrayDeque<PendingAction>()

    private class PendingAction(val revision: Long, val divs: Set<DivBase>, val run: () -> Unit)

    fun applyPatch(patch: DivPatch, rootLocalComponent: DivLocalComponent): Boolean {
        val patchApply = DivPatchApply(
            patch = DivPatchMap(patch),
            errorLogger = { rootLocalComponent.reporter.reportError(it) },
            transformItem = { it.copyForPatch() }
        )
        val updatedStates = patchApply.applyPatch(states.value, rootLocalComponent.expressionResolver)
        if (updatedStates == null) {
            rootLocalComponent.handleActions(patch.onFailedActions.orEmpty(), DivActionSource.PATCH)
            return false
        }

        patchApply.copiedDivs.forEach { (original, updated) ->
            localComponentStorage.alias(original, updated)
            compositionKeyStorage.alias(original, updated)
        }
        val retainedDivs = mutableSetOf<DivBase>()
        updatedStates.forEach { it.div.collectDivs(retainedDivs) }
        states.value = updatedStates
        revision++
        pendingActions.addLast(PendingAction(revision, retainedDivs) {
            rootLocalComponent.handleActions(patch.onAppliedActions.orEmpty(), DivActionSource.PATCH)
        })
        return true
    }

    fun onCompositionApplied(revision: Long) {
        val appliedActions = mutableListOf<PendingAction>()
        while (pendingActions.isNotEmpty() && pendingActions.first().revision <= revision) {
            appliedActions.add(pendingActions.removeFirst())
        }
        val applied = appliedActions.lastOrNull() ?: return
        val retainedDivs = applied.divs.toMutableSet()
        pendingActions.forEach { retainedDivs.addAll(it.divs) }
        localComponentStorage.retain(retainedDivs)
        compositionKeyStorage.retain(retainedDivs)
        appliedActions.forEach { it.run() }
    }
}

@Composable
internal fun DivPatchCoordinator.observe() {
    val revision = revision
    if (revision == 0L) return

    val view = LocalView.current
    DisposableEffect(this, view, revision) {
        val runActions = Runnable { onCompositionApplied(revision) }
        // Lazy layouts compose children during measure; their effects can be queued until after pre-draw.
        val listener = view.doOnPreDraw { view.post(runActions) }
        view.requestLayout()
        onDispose {
            listener.removeListener()
            view.removeCallbacks(runActions)
        }
    }
}
