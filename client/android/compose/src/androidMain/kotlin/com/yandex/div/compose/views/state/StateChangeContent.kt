package com.yandex.div.compose.views.state

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.onGloballyPositioned
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.DivVisibilityState
import com.yandex.div.compose.views.transition.BindStateChangeSnapshots
import com.yandex.div.compose.views.transition.LocalStateChangeTransitionContext
import com.yandex.div.compose.views.transition.StateChangeBoundsMatching
import com.yandex.div.compose.views.transition.StateChangeBoundsStart
import com.yandex.div.compose.views.transition.StateChangeSnapshots
import com.yandex.div.compose.views.transition.StateChangeTransitionScene
import com.yandex.div.compose.views.transition.StateChangeTransitionScope
import com.yandex.div.compose.views.transition.StateChangeTransitionState
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
    lookaheadScope: LookaheadScope?,
    initialState: DivState.State,
    activeState: DivState.State,
    parentPath: DivStatePath,
    divId: String,
    boundaryVisibility: DivVisibilityState,
    contentAlignment: Alignment,
    onTransitionFinished: () -> Unit,
) {
    val entries = remember {
        mutableStateListOf(StateChangeContentEntry(initialState, initiallyVisible = true))
    }
    val scene = remember(lookaheadScope) { StateChangeTransitionScene(lookaheadScope) }
    UpdateStateChangeEntries(entries, activeState, boundaryVisibility, scene)
    val orderedEntries = entries.sortedBy { it.contentActive }

    StateChangeContentLayout(
        modifier = modifier,
        entries = orderedEntries,
        scene = scene,
    ) {
        orderedEntries.forEach { entry ->
            key(entry) {
                entry.Content(
                    transitionModifier = transitionModifier,
                    parentPath = parentPath,
                    divId = divId,
                    scene = scene,
                    boundaryVisibility = boundaryVisibility,
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
    parentPath: DivStatePath,
    divId: String,
    scene: StateChangeTransitionScene,
    boundaryVisibility: DivVisibilityState,
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
                val transitionScope = rememberTransitionScope(
                    enterScope = this@enterContent,
                    exitScope = this@exitContent,
                    scene = scene,
                    boundaryVisibility = boundaryVisibility,
                )
                if (this@Content.contentActive) {
                    Box(
                        modifier = transitionModifier.applyIf(transitionState.boundsMatching?.isFinished == false) {
                            onGloballyPositioned { transitionState.finishBoundsMatching() }
                        },
                        contentAlignment = contentAlignment,
                    ) {
                        WithStatePath(parentPath, divId, state) {
                            val transitionContext = LocalStateChangeTransitionContext.current
                            CompositionLocalProvider(
                                LocalStateChangeTransitionContext provides transitionContext.copy(
                                    scope = transitionScope,
                                    contentActive = true,
                                    overlayCaptured = false,
                                    snapshotParent = null,
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
private fun StateChangeContentEntry.rememberTransitionScope(
    enterScope: AnimatedVisibilityScope,
    exitScope: AnimatedVisibilityScope,
    scene: StateChangeTransitionScene,
    boundaryVisibility: DivVisibilityState,
): StateChangeTransitionScope = remember(
    enterScope, exitScope, enteringIds, exitingIds, commonIds,
    transitionState, scene, boundaryVisibility,
) {
    StateChangeTransitionScope(
        enterScope = enterScope,
        exitScope = exitScope,
        enteringIds = enteringIds,
        exitingIds = exitingIds,
        commonIds = commonIds,
        transitionState = transitionState,
        scene = scene,
        boundaryVisibility = boundaryVisibility,
    )
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
    boundaryVisibility: DivVisibilityState,
    scene: StateChangeTransitionScene,
) {
    val divViewContext = LocalDivViewContext.current
    val parentComponent = LocalComponent.current
    val contentVisible = LocalStateChangeTransitionContext.current.contentVisible
    boundaryVisibility.invalidation
    val previous = entries
        .lastOrNull { it.contentActive }
        ?.takeIf { it.state.stateId != activeState.stateId }
        ?.transitionState
    val previousIds = previous?.renderedIds
    val bounds = if (scene.lookaheadScope != null && previous != null) {
        previous.snapshots.captureBounds(previous.uniqueRenderedIds, scene.coordinates)
    } else null
    SideEffect {
        val boundaryVisible = boundaryVisibility.currentVisibility() ?: return@SideEffect
        val visible = boundaryVisible && contentVisible
        entries.updateActiveState(
            activeState,
            renderedPreviousIds = if (visible) previousIds else emptySet(),
            initialBounds = if (visible) bounds else null,
        ) {
            if (visible) {
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
    initialBounds: Map<String, StateChangeBoundsStart>? = null,
    stateTransitionIds: (Div?) -> Set<String>,
) {
    val previousEntry = lastOrNull { it.contentActive }
    if (previousEntry?.state?.stateId == activeState.stateId) {
        previousEntry.state = activeState
    } else {
        val previousIds = renderedPreviousIds
            ?: previousEntry?.transitionState?.renderedIds.orEmpty()
        val targetIds = stateTransitionIds(activeState.div)
        val candidates = initialBounds?.keys ?: previousIds
        val commonIds = candidates intersect targetIds
        if (initialBounds == null) {
            filterNot { it.contentActive }.forEach { it.matchTargetIds(targetIds) }
        }
        previousEntry?.beginExit(previousIds, commonIds, initialBounds?.keys)

        removeAll { !it.contentActive && it.state.stateId == activeState.stateId }
        val boundsMatching = initialBounds?.let { sources ->
            StateChangeBoundsMatching(sources) { matched ->
                filterNot { it.contentActive }.forEach { it.confirmBoundsMatches(matched) }
            }
        }
        val targetEntry = StateChangeContentEntry(activeState, initiallyVisible = false, boundsMatching)
            .also(::add)
        targetEntry.transitionIds = targetIds
        targetEntry.enteringIds = targetIds - candidates
        targetEntry.commonIds = commonIds
        targetEntry.contentActive = true
        targetEntry.enterVisibility.targetState = true
        targetEntry.exitVisibility.targetState = true

        filter { !it.contentActive && it.exitVisibility.targetState }
            .forEach { it.exitVisibility.targetState = false }
    }
}

private fun StateChangeContentEntry.beginExit(
    previousIds: Set<String>,
    predictedCommon: Set<String>,
    boundsIds: Set<String>?,
) {
    transitionIds = previousIds
    matchableIds = boundsIds.orEmpty()
    // A declared ID in a gallery can be offscreen, or repeated by a template. Keep the source
    // available until the first placed tree confirms a match, without freezing speculative exits.
    commonIds = if (boundsIds == null) predictedCommon else emptySet()
    exitingIds = previousIds - commonIds
    transitionState.captureExitValues(exitingIds)
    transitionState.snapshots.capture(exitingIds, commonIds, pendingIds = predictedCommon - commonIds)
    contentActive = false
}

/** The first placed incoming tree settles the predicted matches: the confirmed ones stay, the others exit. */
private fun StateChangeContentEntry.confirmBoundsMatches(incomingIds: Set<String>) {
    val matched = exitingIds intersect incomingIds intersect matchableIds
    if (matched.isNotEmpty() || transitionState.snapshots.pendingIds.isNotEmpty()) {
        matchTargetIds(commonIds + matched, matched)
    }
}

private fun StateChangeContentEntry.matchTargetIds(targetIds: Set<String>, runtimeMatches: Set<String>? = null) {
    transitionState.unregisterOverlays(exitingIds intersect targetIds)
    exitingIds -= targetIds
    commonIds = transitionIds intersect targetIds
    transitionState.snapshots.capture(exitingIds, commonIds, runtimeMatches)
}

internal class StateChangeContentEntry(
    state: DivState.State,
    initiallyVisible: Boolean,
    boundsMatching: StateChangeBoundsMatching? = null,
) {
    var state by mutableStateOf(state)
    var enteringIds by mutableStateOf(emptySet<String>())
    var exitingIds by mutableStateOf(emptySet<String>())
    var commonIds by mutableStateOf(emptySet<String>())
    var transitionIds = emptySet<String>()
    var matchableIds = emptySet<String>()
    var contentActive by mutableStateOf(initiallyVisible)
    val transitionState = StateChangeTransitionState(boundsMatching)
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
