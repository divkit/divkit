package com.yandex.div.compose.views.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.state.LocalDivStatePath
import com.yandex.div.compose.state.findState
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div.compose.variables.mutableStateFromStringVariable
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.DivContentDecorator
import com.yandex.div.compose.views.DivVisibilityState
import com.yandex.div.compose.views.modifiers.clipToBounds
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div.compose.views.observeDivVisibility
import com.yandex.div.compose.views.transition.LocalStateChangeTransitionContext
import com.yandex.div.compose.views.transition.containsStateChangeTransitions
import com.yandex.div.core.state.DivStatePath
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivState
import com.yandex.div2.DivTransitionSelector

@Composable
internal fun DivStateView(
    modifier: Modifier,
    data: DivState,
    decorator: DivContentDecorator,
) {
    val boundaryVisibility = remember { DivVisibilityState() }
    ResolveDivState(data) { parentPath, divId, activeState ->
        val component = LocalComponent.current
        val swipeOutState = component.stateStorage.getSwipeOutState(
            compositionKey = component.compositionKeyStorage.get(data),
            state = activeState,
        )
        if (swipeOutState?.value == true) {
            return@ResolveDivState
        }

        val transitionContentState = remember { StateChangeContentState(activeState) }

        SwipeOutBox(
            state = activeState,
            modifier = modifier,
            onSwipeOut = { swipeOutState?.value = true },
        ) { parentModifier ->
            decorator.Content(parentModifier) { contentModifier ->
                StateContent(
                    contentModifier,
                    data,
                    parentPath,
                    divId,
                    activeState,
                    transitionContentState,
                    boundaryVisibility,
                )
            }
        }
    }
}

@Composable
private fun StateContent(
    modifier: Modifier,
    data: DivState,
    parentPath: DivStatePath,
    divId: String,
    activeState: DivState.State,
    transitionContentState: StateChangeContentState,
    boundaryVisibility: DivVisibilityState,
) {
    val component = LocalComponent.current
    val contentModifier = modifier
        .applyIf(data.clipToBounds.observedValue()) {
            clipToBounds(
                data = data,
                hasWildcardExtensions = component.wildcardExtensions.isNotEmpty(),
            )
        }
        .observeDivVisibility(boundaryVisibility)
        .padding(data.paddings)
    val parentContentActive = LocalStateChangeTransitionContext.current.contentActive
    val activeStateData = activeState.div?.value()
    val contentAlignment = toAlignment(
        activeStateData?.alignmentHorizontal.observedValue(DivAlignmentHorizontal.START),
        activeStateData?.alignmentVertical.observedValue(DivAlignmentVertical.TOP),
    )
    val transitionsEnabled = data.transitionAnimationSelector.observedValue().allowsStateChange()
    val animationsEnabled = component.animationConfiguration.isEnabledAsState()
    val committedState = transitionContentState.committedState
    val transitionsAvailable = transitionsEnabled && animationsEnabled
    val (activeStateHasTransitions, committedStateHasTransitions) = rememberTransitionPresence(
        transitionsAvailable,
        activeState = activeState,
        committedState = committedState,
    )
    val useTransitionContent = transitionContentState.shouldUseTransitionContent(
        activeState = activeState,
        transitionsAvailable = transitionsAvailable,
        activeStateHasTransitions = activeStateHasTransitions,
        committedStateHasTransitions = committedStateHasTransitions,
    )

    if (useTransitionContent) {
        val transitionModifier = Modifier
            .applyIf(!data.width.isWrapContent) { fillMaxWidth() }
            .applyIf(!data.height.isWrapContent) { fillMaxHeight() }
        TransitioningStateContent(
            modifier = contentModifier,
            transitionModifier = transitionModifier,
            initialState = committedState,
            activeState = activeState,
            parentPath = parentPath,
            divId = divId,
            parentContentActive = parentContentActive,
            boundaryVisibility = boundaryVisibility,
            contentAlignment = contentAlignment,
            onTransitionFinished = transitionContentState::onTransitionFinished,
        )
    } else {
        RegularStateContent(contentModifier, contentAlignment, parentPath, divId, activeState)
    }
    SideEffect {
        transitionContentState.updateAfterComposition(activeState, useTransitionContent)
    }
}

@Composable
private fun RegularStateContent(
    modifier: Modifier,
    contentAlignment: Alignment,
    parentPath: DivStatePath,
    divId: String,
    activeState: DivState.State,
) {
    val transitionContext = LocalStateChangeTransitionContext.current
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        WithStatePath(parentPath, divId, activeState) {
            CompositionLocalProvider(
                LocalStateChangeTransitionContext provides transitionContext.copy(
                    scope = null,
                )
            ) {
                activeState.div?.let { DivBlockView(it) }
            }
        }
    }
}

@Composable
private fun rememberTransitionPresence(
    transitionsAvailable: Boolean,
    activeState: DivState.State,
    committedState: DivState.State,
): Pair<Boolean, Boolean> = remember(transitionsAvailable, activeState.div, committedState.div) {
    if (!transitionsAvailable) {
        false to false
    } else {
        val activeHasTransitions = activeState.div.containsStateChangeTransitions()
        val committedHasTransitions = if (activeState.div === committedState.div) {
            activeHasTransitions
        } else {
            committedState.div.containsStateChangeTransitions()
        }
        activeHasTransitions to committedHasTransitions
    }
}

@Composable
internal fun WithDivState(
    divState: DivState,
    content: @Composable (DivState.State) -> Unit,
) {
    ResolveDivState(divState) { parentPath, divId, activeState ->
        WithStatePath(parentPath, divId, activeState) {
            content(activeState)
        }
    }
}

@Composable
private fun ResolveDivState(
    divState: DivState,
    content: @Composable (DivStatePath, String, DivState.State) -> Unit,
) {
    val parentPath = LocalDivStatePath.current
    val divId = divState.id
    if (divId == null) {
        reportError("DivState id is missing at path '$parentPath'")
    }

    val activeState = divState.rememberActiveState(parentPath) ?: return
    content(parentPath, divId.orEmpty(), activeState)
}

@Composable
internal fun WithStatePath(
    parentPath: DivStatePath,
    divId: String,
    state: DivState.State,
    content: @Composable () -> Unit,
) {
    val childPath = parentPath.append(
        divId,
        state.stateId,
        state.stateId
    )
    CompositionLocalProvider(LocalDivStatePath provides childPath) {
        content()
    }
}

private fun DivTransitionSelector.allowsStateChange(): Boolean {
    return this == DivTransitionSelector.STATE_CHANGE || this == DivTransitionSelector.ANY_CHANGE
}

@Composable
private fun DivState.rememberActiveState(
    parentPath: DivStatePath
): DivState.State? {
    val component = LocalComponent.current
    val stateStorage = component.stateStorage
    val divId = id
    val variableState = stateIdVariable?.let { mutableStateFromStringVariable(it) }
    val storedStateId = divId?.let { stateStorage.getActiveState(parentPath, it) }

    if (divId != null && variableState != null) {
        DisposableEffect(parentPath, divId, variableState) {
            stateStorage.bindStateIdVariable(parentPath, divId) { variableState.value = it }
            onDispose { stateStorage.unbindStateIdVariable(parentPath, divId) }
        }
    }

    val expressionResolver = component.expressionResolver
    val activeStateId = variableState?.value
        ?: storedStateId
        ?: remember(defaultStateId, expressionResolver) {
            defaultStateId?.evaluate(expressionResolver)
        }
    return resolveState(activeStateId)
}

@Composable
private fun DivState.resolveState(activeStateId: String?): DivState.State? {
    val matched = activeStateId?.let { id -> states.find { it.stateId == id } }
    if (matched == null && activeStateId != null) {
        reportError("State with id '$activeStateId' not found")
    }
    return findState(activeStateId) ?: run {
        reportError("DivState has no states")
        null
    }
}
