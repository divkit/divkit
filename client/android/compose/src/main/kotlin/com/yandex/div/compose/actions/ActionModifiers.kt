package com.yandex.div.compose.actions

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div2.DivAction

@Composable
internal fun Modifier.actions(actions: DivActions, isFocusable: Boolean): Modifier {
    val focusRequester = if (isFocusable) remember { FocusRequester() } else null
    var modifier = applyIfNotNull(focusRequester) { focusRequester(it) }

    val localComponent = LocalComponent.current
    val handler = remember(actions, localComponent, focusRequester) {
        GestureHandler(actions, localComponent, focusRequester)
    }

    val interactionSource = remember { MutableInteractionSource() }

    modifier = modifier.actionAnimation(
        animation = actions.animation,
        interactionSource = interactionSource,
    )

    val hasTapActions = actions.tapActions.isNotEmpty() ||
            !actions.doubleTapActions.isNullOrEmpty() ||
            !actions.longTapActions.isNullOrEmpty()
    modifier = if (hasTapActions) {
        // The inner detector consumes touch; combinedClickable provides semantics and keys.
        modifier.combinedClickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = handler.onClick,
            onDoubleClick = handler.onDoubleClick,
            onLongClick = handler.onLongClick,
        )
    } else {
        modifier.focusable(
            enabled = isFocusable,
            interactionSource = interactionSource,
        )
    }
    return modifier.gestures(
        handler = handler,
        interactionSource = interactionSource,
    )
}

@Composable
private fun Modifier.gestures(
    handler: GestureHandler,
    interactionSource: MutableInteractionSource,
): Modifier {
    val currentHandler by rememberUpdatedState(handler)
    val hasDoubleTap = handler.onDoubleClick != null
    val hasLongTap = handler.onLongClick != null
    return pointerInput(interactionSource, hasDoubleTap, hasLongTap) {
        var finishPress: (Boolean) -> Unit = {}
        detectTapGestures(
            onPress = { position ->
                val press = PressInteraction.Press(position)
                var finished = false
                fun finish(released: Boolean) {
                    if (finished) {
                        return
                    }
                    finished = true
                    interactionSource.tryEmit(
                        if (released) {
                            PressInteraction.Release(press)
                        } else {
                            PressInteraction.Cancel(press)
                        }
                    )
                    currentHandler.onPressEnd?.invoke()
                }
                finishPress = ::finish
                var released = false
                try {
                    interactionSource.emit(press)
                    currentHandler.onPressStart?.invoke()
                    released = tryAwaitRelease()
                } finally {
                    finish(released)
                }
            },
            onTap = {
                // onPress may resume after onTap; finish synchronously to preserve action order.
                finishPress(true)
                currentHandler.onClick()
            },
            onDoubleTap = if (hasDoubleTap) {
                {
                    finishPress(true)
                    currentHandler.onDoubleClick?.invoke()
                }
            } else {
                null
            },
            onLongPress = if (hasLongTap) {
                {
                    currentHandler.onLongClick?.invoke()
                }
            } else {
                null
            },
        )
    }
}

private class GestureHandler(
    private val actions: DivActions,
    private val localComponent: DivLocalComponent,
    private val focusRequester: FocusRequester?,
) {
    val onClick: (() -> Unit) = createHandler(
        actions = actions.tapActions,
        source = DivActionSource.TAP,
        requestFocus = actions.tapActions.isNotEmpty() || actions.doubleTapActions == null,
    ) ?: {}

    val onDoubleClick = createHandler(
        actions = actions.doubleTapActions,
        source = DivActionSource.DOUBLE_TAP
    )

    val onLongClick = createHandler(
        actions = actions.longTapActions,
        source = DivActionSource.LONG_TAP
    )

    val onPressStart = createHandler(
        actions = actions.pressStartActions,
        source = DivActionSource.GESTURE,
    )

    val onPressEnd = createHandler(
        actions = actions.pressEndActions,
        source = DivActionSource.GESTURE,
    )

    private fun createHandler(
        actions: List<DivAction>?,
        source: DivActionSource,
        requestFocus: Boolean = true,
    ): (() -> Unit)? {
        if (actions == null) {
            return null
        }
        return {
            if (requestFocus && focusRequester != null &&
                this.actions.captureFocusOnAction.evaluate(localComponent.expressionResolver)
            ) {
                focusRequester.requestFocus()
            }
            localComponent.handleActions(actions, source)
        }
    }
}
