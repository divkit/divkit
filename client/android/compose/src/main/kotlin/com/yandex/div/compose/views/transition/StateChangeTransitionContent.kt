package com.yandex.div.compose.views.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

@Composable
internal fun WithStateChangeTransition(
    data: Div,
    visibility: DivVisibility,
    content: @Composable (StateChangeTransitionModifier) -> Unit,
) {
    val transitionContext = LocalStateChangeTransitionContext.current
    val contentVisible = transitionContext.contentVisible &&
        visibility == DivVisibility.VISIBLE
    TrackRenderedStateTransitionId(data, contentVisible)
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
) {
    if (!contentVisible) return

    val id = data.value().id ?: return
    val transitionState = LocalStateChangeTransitionContext.current.scope?.transitionState ?: return

    DisposableEffect(transitionState, id) {
        transitionState.registerRenderedId(id)
        onDispose { transitionState.unregisterRenderedId(id) }
    }
}
