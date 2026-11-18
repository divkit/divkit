package com.yandex.div.compose.views.state

import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.handleActions
import com.yandex.div.compose.views.transition.LocalStateChangeTransitionContext
import com.yandex.div2.DivState

@Composable
internal fun SwipeOutBox(
    state: DivState.State,
    modifier: Modifier,
    onSwipeOut: () -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    val actions = state.swipeOutActions
    if (actions.isNullOrEmpty() || !LocalStateChangeTransitionContext.current.contentActive) {
        content(modifier)
        return
    }

    val component = LocalComponent.current
    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(),
        modifier = modifier,
        backgroundContent = {},
        onDismiss = {
            onSwipeOut()
            component.handleActions(actions, source = DivActionSource.GESTURE)
        },
    ) {
        content(Modifier)
    }
}
