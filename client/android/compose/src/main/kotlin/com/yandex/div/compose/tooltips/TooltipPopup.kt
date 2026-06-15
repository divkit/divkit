package com.yandex.div.compose.tooltips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.DivAction
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltipMode
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Renders a single tooltip as a [Popup] positioned relative to its registered anchor.
 *
 * The popup observes anchor bounds via [TooltipAnchorEntry.bounds] and is dismissed:
 * - automatically after [DivTooltip.duration] (unless `duration == 0`),
 * - on back press or outside tap in modal mode,
 * - on outside tap when `close_by_tap_outside == true`,
 * - or explicitly via [TooltipStateStorage.hide].
 *
 * Caller owns the rendered/exiting lifecycle: when [isVisible] flips to `false`,
 * the popup plays its exit transition; once it completes [onHidden] is
 * invoked so the caller can dispose this composable.
 */
@Composable
internal fun TooltipPopup(
    anchorEntry: TooltipAnchorEntry,
    isVisible: Boolean,
    onDismissRequested: () -> Unit,
    onHidden: () -> Unit,
) {
    val tooltip = anchorEntry.tooltip
    val duration = tooltip.duration.observedValue()

    LaunchedEffect(tooltip.id, isVisible, duration) {
        if (isVisible && duration > 0L) {
            delay(duration.milliseconds)
            onDismissRequested()
        }
    }

    val exitTransition = tooltip.observedExitTransition()
    val exitTransitionDuration = if (exitTransition == ExitTransition.None) {
        0
    } else {
        tooltip.animationOut?.duration?.observedIntValue() ?: DEFAULT_TOOLTIP_TRANSITION_DURATION_MS
    }
    LaunchedEffect(tooltip.id, isVisible, exitTransitionDuration) {
        if (!isVisible) {
            delay(exitTransitionDuration.milliseconds)
            onHidden()
        }
    }

    val isModal = tooltip.mode is DivTooltipMode.Modal
    val closeByTapOutside = tooltip.closeByTapOutside.observedValue()

    Popup(
        properties = PopupProperties(
            focusable = isModal,
            dismissOnBackPress = true,
            dismissOnClickOutside = closeByTapOutside || isModal,
            clippingEnabled = false
        ),
        popupPositionProvider = TooltipPositionProvider(
            position = tooltip.position.observedValue(),
            anchorBounds = anchorEntry.bounds.value,
            offset = IntOffset(
                x = tooltip.offset?.x?.observedPxValue()?.toInt() ?: 0,
                y = tooltip.offset?.y?.observedPxValue()?.toInt() ?: 0
            )
        ),
        onDismissRequest = rememberTapOutsideHandler(
            actions = tooltip.tapOutsideActions,
            closeByTapOutside = closeByTapOutside,
            onDismiss = onDismissRequested
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = tooltip.observedEnterTransition(),
            exit = exitTransition
        ) {
            DivBlockView(data = tooltip.div)
        }
    }
}

@Composable
private fun rememberTapOutsideHandler(
    actions: List<DivAction>?,
    closeByTapOutside: Boolean,
    onDismiss: () -> Unit
): () -> Unit {
    val localComponent = LocalComponent.current
    return remember(actions, closeByTapOutside, onDismiss) {
        {
            if (!actions.isNullOrEmpty()) {
                localComponent.handleActions(actions = actions, source = DivActionSource.TAP)
            }
            if (closeByTapOutside) {
                onDismiss()
            }
        }
    }
}
