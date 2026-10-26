package com.yandex.div.compose.actions

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
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
    val pressState = remember { PressState() }
    modifier = modifier.hoverActions(handler, pressState)

    val hasTapActions = actions.tapActions.isNotEmpty() ||
            !actions.doubleTapActions.isNullOrEmpty() ||
            !actions.longTapActions.isNullOrEmpty()
    if (!hasTapActions && actions.pressStartActions == null && actions.pressEndActions == null) {
        return modifier.focusable(enabled = isFocusable)
    }

    val interactionSource = remember { MutableInteractionSource() }

    modifier = modifier.actionAnimation(
        animation = actions.animation,
        interactionSource = interactionSource,
    )

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
        pressState = pressState,
    )
}

@Composable
private fun Modifier.hoverActions(handler: GestureHandler, pressState: PressState): Modifier {
    if (handler.onHoverStart == null && handler.onHoverEnd == null) return this
    val currentHandler by rememberUpdatedState(handler)
    val coordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }
    return onGloballyPositioned { coordinates.value = it }.pointerInput(Unit) {
        val hoverState = HoverState()
        awaitPointerEventScope {
            while (true) {
                val hovered = awaitHoverState(pressState, coordinates, hoverState) ?: continue
                if (hoverState.isHovered != hovered) {
                    hoverState.isHovered = hovered
                    val onHover = if (hovered) currentHandler.onHoverStart else currentHandler.onHoverEnd
                    onHover?.invoke()
                }
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitHoverState(
    pressState: PressState,
    coordinates: State<LayoutCoordinates?>,
    hoverState: HoverState,
): Boolean? {
    // Compose can suppress stationary hover re-entry after release. Capture it before consumption,
    // then finish tap and press callbacks before restoring hover.
    val event = awaitPointerEvent(PointerEventPass.Initial)
    if (event.type == PointerEventType.Press) return false
    val hoverRelease = event.changes.firstOrNull {
        (it.type == PointerType.Mouse || it.id == hoverState.pointerId) &&
                it.changedToUp() && !it.isOutOfBounds(size, Size.Zero)
    }
    awaitPointerEvent(PointerEventPass.Main)
    return when (event.type) {
        PointerEventType.Enter, PointerEventType.Move -> {
            val hovering = event.changes.none { it.pressed }
            if (hovering) hoverState.pointerId = event.changes.firstOrNull()?.id
            hovering
        }
        PointerEventType.Exit -> {
            if (event.changes.none { it.pressed }) hoverState.pointerId = null
            false
        }
        PointerEventType.Release -> {
            awaitPointerEvent(PointerEventPass.Final)
            val currentCoordinates = coordinates.value?.takeIf { it.isAttached }
            val hoverReleasedInside = hoverRelease != null && currentCoordinates != null &&
                    currentCoordinates.boundsInWindow().contains(
                        currentCoordinates.localToWindow(hoverRelease.position)
                    )
            if (!hoverReleasedInside && event.changes.none { it.id == hoverState.pointerId && it.pressed }) {
                hoverState.pointerId = null
            }
            if (hoverReleasedInside) pressState.finish(true)
            hoverReleasedInside
        }
        else -> null
    }
}

private class HoverState {
    var isHovered = false
    var pointerId: PointerId? = null
}

@Composable
private fun Modifier.gestures(
    handler: GestureHandler,
    interactionSource: MutableInteractionSource,
    pressState: PressState,
): Modifier {
    val currentHandler by rememberUpdatedState(handler)
    val hasDoubleTap = handler.onDoubleClick != null
    val hasLongTap = handler.onLongClick != null
    return pointerInput(interactionSource, hasDoubleTap, hasLongTap) {
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
                pressState.finish = ::finish
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
                pressState.finish(true)
                currentHandler.onClick()
            },
            onDoubleTap = if (hasDoubleTap) {
                {
                    pressState.finish(true)
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

private class PressState {
    var finish: (Boolean) -> Unit = {}
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

    val onHoverStart = createHandler(
        actions = actions.hoverStartActions,
        source = DivActionSource.GESTURE,
        requestFocus = false,
    )

    val onHoverEnd = createHandler(
        actions = actions.hoverEndActions,
        source = DivActionSource.GESTURE,
        requestFocus = false,
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
