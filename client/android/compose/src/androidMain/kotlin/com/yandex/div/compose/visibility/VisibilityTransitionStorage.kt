package com.yandex.div.compose.visibility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivBase
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import dev.zacsweers.metro.Inject

@DivViewScope
internal class VisibilityTransitionStorage @Inject constructor() {
    private val states = mutableMapOf<DivBase, VisibilityTransitionState>()

    fun get(data: DivBase, initialVisibility: DivVisibility): VisibilityTransitionState =
        states.getOrPut(data) { VisibilityTransitionState(initialVisibility) }

    fun alias(original: DivBase, updated: DivBase) {
        states[original]?.let { states[updated] = it }
    }

    fun retain(divs: Set<DivBase>) {
        states.keys.retainAll(divs)
    }

    fun remove(divs: Set<DivBase>) {
        states.keys.removeAll(divs)
    }
}

internal class VisibilityTransitionState(initialVisibility: DivVisibility) {
    var renderedVisibility by mutableStateOf(initialVisibility)
    private var attachmentCount by mutableIntStateOf(0)
    private var observerCount = 0

    val isAttached: Boolean get() = attachmentCount > 0

    /**
     * Whether a composition keeps [renderedVisibility] up to date. A value left by a disposed
     * composition does not describe what the next one renders.
     */
    val isObserved: Boolean get() = observerCount > 0

    fun startObserving(visibility: DivVisibility) {
        if (observerCount++ == 0) renderedVisibility = visibility
    }

    fun stopObserving() {
        observerCount--
    }

    fun attach(visibility: DivVisibility) {
        startObserving(visibility)
        attachmentCount++
    }

    fun detach() {
        attachmentCount--
        stopObserving()
    }

    fun renderedVisibility(requestedVisibility: DivVisibility): DivVisibility =
        if (requestedVisibility == DivVisibility.GONE && isAttached &&
            renderedVisibility == DivVisibility.VISIBLE
        ) {
            DivVisibility.VISIBLE
        } else {
            requestedVisibility
        }
}

internal fun DivBase.hasVisibilityTransition(): Boolean =
    (transitionIn != null || transitionOut != null) &&
        transitionTriggers?.contains(DivTransitionTrigger.VISIBILITY_CHANGE) != false

@Composable
internal inline fun DivBase.observeRenderedVisibility(
    component: DivLocalComponent = LocalComponent.current,
): DivVisibility {
    val visibility = visibility.observedValue(component = component)
    if (!hasVisibilityTransition()) return visibility

    val state = component.visibilityTransitionStorage.get(this, visibility)
    DisposableEffect(state) {
        state.startObserving(visibility)
        onDispose { state.stopObserving() }
    }
    if (visibility != DivVisibility.GONE) return visibility

    // Content is retained only for an exit animation that is going to run.
    val retainsExit = transitionOut != null && component.animationConfiguration.isEnabledAsState()
    val renderedVisibility = if (retainsExit) state.renderedVisibility(visibility) else visibility
    if (renderedVisibility == DivVisibility.GONE) {
        // The content leaves composition here, so it cannot record its own disappearance.
        SideEffect { state.renderedVisibility = DivVisibility.GONE }
    }
    return renderedVisibility
}
