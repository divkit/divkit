package com.yandex.div.compose.views.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

private val neutralStateChangeTransition = StateChangeTransitionModifier()

@Composable
internal fun WithStateChangeTransition(
    data: Div,
    visibility: DivVisibility,
    content: @Composable (StateChangeTransitionModifier) -> Unit,
) {
    val transitionContext = LocalStateChangeTransitionContext.current
    val scope = transitionContext.scope
    if (scope == null) {
        content(neutralStateChangeTransition)
        return
    }
    val contentVisible = transitionContext.contentVisible &&
        visibility == DivVisibility.VISIBLE
    TrackRenderedStateTransitionId(data, contentVisible, scope)
    val stateChangeTransition = Modifier.applyStateChangeTransition(
        data = data.value(),
        visibility = visibility,
        capturesScene = data.hasStateChangeTransitionChildren(),
    )
    CompositionLocalProvider(
        LocalStateChangeTransitionContext provides transitionContext.copy(
            contentVisible = contentVisible,
            overlayCaptured =
                (transitionContext.overlayCaptured && !stateChangeTransition.releasesOverlayCapture) ||
                    stateChangeTransition.capturesOverlay,
            snapshotParentId = stateChangeTransition.snapshotId ?: transitionContext.snapshotParentId,
        )
    ) {
        content(stateChangeTransition)
    }
}

private fun Div.hasStateChangeTransitionChildren(): Boolean = when (this) {
    is Div.Container,
    is Div.Gallery,
    is Div.Grid,
    is Div.Pager,
    is Div.State,
    is Div.Tabs -> true
    is Div.Custom,
    is Div.GifImage,
    is Div.Image,
    is Div.Indicator,
    is Div.Input,
    is Div.Select,
    is Div.Separator,
    is Div.Slider,
    is Div.Switch,
    is Div.Text,
    is Div.Video -> false
}

@Composable
private fun TrackRenderedStateTransitionId(
    data: Div,
    contentVisible: Boolean,
    scope: StateChangeTransitionScope,
) {
    if (!contentVisible) return

    val id = data.value().id ?: return
    val transitionState = scope.transitionState
    val boundaryVisibility = scope.boundaryVisibility

    DisposableEffect(transitionState, id, boundaryVisibility, boundaryVisibility.invalidation) {
        val registered = boundaryVisibility.currentVisibility() == true
        if (registered) transitionState.registerRenderedId(id)
        onDispose {
            if (registered) transitionState.unregisterRenderedId(id)
        }
    }
}
