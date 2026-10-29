package com.yandex.div.compose.views.state

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.transition.LocalStateChangeTransitionContext
import com.yandex.div.compose.views.transition.StateChangeTransitionScene
import com.yandex.div.compose.views.transition.StateChangeTransitionScope
import com.yandex.div.compose.views.transition.StateChangeTransitionState
import com.yandex.div.compose.views.transition.BindStateChangeSnapshots
import com.yandex.div.compose.views.transition.StateChangeSnapshots
import com.yandex.div.compose.views.transition.visibleStateTransitionIds
import com.yandex.div.core.state.DivStatePath
import com.yandex.div2.Div
import com.yandex.div2.DivState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
internal fun TransitioningStateContent(
    modifier: Modifier,
    transitionModifier: Modifier,
    initialState: DivState.State,
    activeState: DivState.State,
    parentPath: DivStatePath,
    divId: String,
    parentContentActive: Boolean,
    contentAlignment: Alignment,
    onTransitionFinished: () -> Unit,
) {
    val entries = remember {
        mutableStateListOf(StateChangeContentEntry(initialState, initiallyVisible = true))
    }
    val scene = remember { StateChangeTransitionScene() }
    UpdateStateChangeEntries(entries, activeState)
    val orderedEntries = entries.sortedBy { it.contentActive }

    StateChangeContentLayout(
        modifier = modifier,
        entries = orderedEntries,
        scene = scene,
    ) {
        orderedEntries.forEach { entry ->
            key(entry) {
                val contentActive = parentContentActive && entry.contentActive &&
                    entry.state.stateId == activeState.stateId
                entry.Content(
                    transitionModifier = transitionModifier,
                    contentActive = contentActive,
                    parentPath = parentPath,
                    divId = divId,
                    scene = scene,
                    contentAlignment = contentAlignment,
                    onRemoved = {
                        entries.remove(entry)
                        if (entries.none { !it.contentActive }) {
                            onTransitionFinished()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun StateChangeContentEntry.Content(
    transitionModifier: Modifier,
    contentActive: Boolean,
    parentPath: DivStatePath,
    divId: String,
    scene: StateChangeTransitionScene,
    contentAlignment: Alignment,
    onRemoved: () -> Unit,
) {
    BindStateChangeSnapshots(transitionState.snapshots)
    // Keep content visible until recomposition registers its outgoing overlay.
    val contentAlpha = if (this@Content.contentActive) 1f else 0f
    Box(
        Modifier.graphicsLayer {
            alpha = contentAlpha
        }
    ) {
        RemoveWhenExitFinished(this@Content, onRemoved)
        AnimatedVisibility(
            modifier = transitionModifier,
            visibleState = exitVisibility,
            enter = EnterTransition.None,
            exit = ExitTransition.None,
        ) exitContent@ {
            AnimatedVisibility(
                modifier = transitionModifier,
                visibleState = enterVisibility,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
            ) enterContent@ {
                val transitionScope = StateChangeTransitionScope(
                    enterScope = this@enterContent,
                    exitScope = this@exitContent,
                    enteringIds = enteringIds,
                    exitingIds = exitingIds,
                    commonIds = commonIds,
                    transitionState = transitionState,
                    scene = scene,
                )
                if (this@Content.contentActive) {
                    Box(
                        modifier = transitionModifier,
                        contentAlignment = contentAlignment,
                    ) {
                        WithStatePath(parentPath, divId, state) {
                            val transitionContext = LocalStateChangeTransitionContext.current
                            CompositionLocalProvider(
                                LocalStateChangeTransitionContext provides transitionContext.copy(
                                    scope = transitionScope,
                                    contentActive = true,
                                    overlayCaptured = false,
                                    snapshotParentId = null,
                                )
                            ) {
                                state.div?.let { DivBlockView(it) }
                            }
                        }
                    }
                } else {
                    StateChangeSnapshots(transitionScope)
                }
            }
        }
    }
}

@Composable
internal fun BoxScope.RetainedStateInteractionBarrier() {
    Box(
        Modifier
            .matchParentSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
    )
}

@Composable
private fun UpdateStateChangeEntries(
    entries: MutableList<StateChangeContentEntry>,
    activeState: DivState.State,
) {
    val divViewContext = LocalDivViewContext.current
    val parentComponent = LocalComponent.current
    val contentVisible = LocalStateChangeTransitionContext.current.contentVisible
    val previousIds = entries
        .lastOrNull { it.contentActive }
        ?.takeIf { it.state.stateId != activeState.stateId }
        ?.transitionState
        ?.snapshotRenderedIds()
    SideEffect {
        entries.updateActiveState(activeState, previousIds) {
            if (contentVisible) {
                it.visibleStateTransitionIds(divViewContext, parentComponent)
            } else {
                emptySet()
            }
        }
    }
}

@Composable
private fun RemoveWhenExitFinished(
    entry: StateChangeContentEntry,
    remove: () -> Unit,
) {
    LaunchedEffect(entry) {
        snapshotFlow {
            entry.exitVisibility.isIdle && !entry.exitVisibility.currentState
        }
            .filter { it }
            .first()
        if (!entry.exitVisibility.targetState) {
            remove()
        }
    }
}

internal fun MutableList<StateChangeContentEntry>.updateActiveState(
    activeState: DivState.State,
    renderedPreviousIds: Set<String>? = null,
    stateTransitionIds: (Div?) -> Set<String>,
) {
    val previousEntry = lastOrNull { it.contentActive }
    if (previousEntry?.state?.stateId == activeState.stateId) {
        previousEntry.state = activeState
    } else {
        val previousIds = renderedPreviousIds
            ?: previousEntry?.transitionState?.snapshotRenderedIds().orEmpty()
        val targetIds = stateTransitionIds(activeState.div)
        val commonIds = previousIds intersect targetIds
        filterNot { it.contentActive }.forEach { entry ->
            entry.transitionState.unregisterOverlays(entry.exitingIds intersect targetIds)
            entry.exitingIds = entry.exitingIds - targetIds
            entry.commonIds = entry.transitionIds intersect targetIds
            entry.transitionState.snapshots.capture(entry.exitingIds, entry.commonIds)
        }
        if (previousEntry != null) {
            previousEntry.transitionIds = previousIds
            previousEntry.exitingIds = previousIds - targetIds
            previousEntry.commonIds = commonIds
            previousEntry.transitionState.captureExitValues(previousEntry.exitingIds)
            previousEntry.transitionState.snapshots.capture(previousEntry.exitingIds, commonIds)
            previousEntry.contentActive = false
        }

        removeAll { !it.contentActive && it.state.stateId == activeState.stateId }
        val targetEntry = StateChangeContentEntry(activeState, initiallyVisible = false)
            .also(::add)
        targetEntry.transitionIds = targetIds
        targetEntry.enteringIds = targetIds - previousIds
        targetEntry.commonIds = commonIds
        targetEntry.contentActive = true
        targetEntry.enterVisibility.targetState = true
        targetEntry.exitVisibility.targetState = true

        filter { !it.contentActive && it.exitVisibility.targetState }
            .forEach { it.exitVisibility.targetState = false }
    }
}

internal class StateChangeContentEntry(
    state: DivState.State,
    initiallyVisible: Boolean,
) {
    var state by mutableStateOf(state)
    var enteringIds by mutableStateOf(emptySet<String>())
    var exitingIds by mutableStateOf(emptySet<String>())
    var commonIds by mutableStateOf(emptySet<String>())
    var transitionIds = emptySet<String>()
    var contentActive by mutableStateOf(initiallyVisible)
    val transitionState = StateChangeTransitionState()
    var layoutWidth = 0
    var layoutHeight = 0
    var hasLayoutSize = false
    val enterVisibility = MutableTransitionState(initiallyVisible).apply {
        targetState = true
    }
    val exitVisibility = MutableTransitionState(true).apply {
        targetState = true
    }
}

internal class StateChangeContentState(initialState: DivState.State) {
    var committedState by mutableStateOf(initialState)
        private set
    private var transitionRunning by mutableStateOf(false)
    private var transitionContentStateId by mutableStateOf<String?>(null)

    fun shouldUseTransitionContent(
        activeState: DivState.State,
        transitionsAvailable: Boolean,
        activeStateHasTransitions: Boolean,
        committedStateHasTransitions: Boolean,
    ): Boolean {
        return transitionsAvailable &&
            (activeStateHasTransitions || transitionRunning ||
                activeState.stateId != committedState.stateId && committedStateHasTransitions ||
                transitionContentStateId == activeState.stateId)
    }

    fun updateAfterComposition(
        activeState: DivState.State,
        transitionContentUsed: Boolean,
    ) {
        val stateChanged = committedState.stateId != activeState.stateId
        committedState = activeState
        transitionContentStateId = activeState.stateId.takeIf { transitionContentUsed }
        if (stateChanged || !transitionContentUsed) {
            transitionRunning = stateChanged && transitionContentUsed
        }
    }

    fun onTransitionFinished() {
        transitionRunning = false
    }
}
