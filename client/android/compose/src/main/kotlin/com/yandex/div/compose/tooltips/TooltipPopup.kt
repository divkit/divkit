package com.yandex.div.compose.tooltips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.context.animationsEnabled
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.internal.tooltip.hasTooltipActions
import com.yandex.div.internal.tooltip.TouchEventDispatcher
import com.yandex.div2.DivAction
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltipMode
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

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
    val context = LocalContext.current
    val underlyingView = LocalView.current.rootView
    val tooltip = anchorEntry.tooltip
    TooltipDurationEffect(tooltip, isVisible, onDismissRequested)

    val enterTransition = if (animationsEnabled) tooltip.observedEnterTransition() else EnterTransition.None
    val exitTransition = if (animationsEnabled) tooltip.observedExitTransition() else ExitTransition.None
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
    val touchDispatcher = remember(underlyingView, isModal) {
        if (isModal) null else TouchEventDispatcher { underlyingView }::dispatch
    }
    val closeByTapOutside = tooltip.closeByTapOutside.observedValue()
    val positionProvider = anchorEntry.observedPositionProvider(fullScreen = tooltip.substrateDiv != null)
    val onTapOutside = rememberTapOutsideHandler(
        actions = tooltip.tapOutsideActions,
        closeByTapOutside = closeByTapOutside,
        onDismiss = onDismissRequested
    )

    Popup(
        properties = PopupProperties(
            focusable = isModal,
            dismissOnBackPress = true,
            dismissOnClickOutside = closeByTapOutside || isModal,
            clippingEnabled = false
        ),
        popupPositionProvider = positionProvider,
        onDismissRequest = onTapOutside,
    ) {
        CompositionLocalProvider(
            LocalContext provides context,
            LocalTooltipContent provides true,
            LocalTooltipTouchDispatcher provides touchDispatcher,
        ) {
            TooltipPopupContent(
                tooltip = tooltip,
                positionProvider = positionProvider,
                isVisible = isVisible,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                onTapOutside = onTapOutside,
            )
        }
    }
}

@Composable
private fun TooltipDurationEffect(
    tooltip: DivTooltip,
    isVisible: Boolean,
    onDismissRequested: () -> Unit,
) {
    val duration = tooltip.duration.observedValue()
    LaunchedEffect(tooltip.id, isVisible, duration) {
        if (isVisible && duration > 0L) {
            delay(duration.milliseconds)
            onDismissRequested()
        }
    }
}

@Composable
private fun TooltipAnchorEntry.observedPositionProvider(fullScreen: Boolean) = TooltipPositionProvider(
    position = tooltip.position.observedValue(),
    anchorBounds = bounds.value,
    offset = IntOffset(
        x = tooltip.offset?.x?.observedPxValue()?.toInt() ?: 0,
        y = tooltip.offset?.y?.observedPxValue()?.toInt() ?: 0,
    ),
    fullScreen = fullScreen,
)

@Composable
private fun TooltipPopupContent(
    tooltip: DivTooltip,
    positionProvider: TooltipPositionProvider,
    isVisible: Boolean,
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    onTapOutside: () -> Unit,
) {
    val substrate = tooltip.substrateDiv
    if (substrate == null) {
        AnimatedVisibility(
            visible = isVisible,
            enter = enterTransition,
            exit = exitTransition,
        ) {
            DivBlockView(data = tooltip.div)
        }
        return
    }

    val visibilityState = remember { MutableTransitionState(false) }
    visibilityState.targetState = isVisible
    val transition = rememberTransition(visibilityState, label = "Tooltip with substrate")
    val handlesSubstrateTaps = remember(substrate) { substrate.hasTooltipActions() }
    Box(Modifier.fillMaxSize().tooltipInput(onTapOutside)) {
        transition.AnimatedVisibility(
            visible = { it },
            enter = if (enterTransition == EnterTransition.None) {
                EnterTransition.None
            } else {
                tooltip.observedSubstrateEnterTransition()
            },
            exit = if (exitTransition == ExitTransition.None) {
                ExitTransition.None
            } else {
                tooltip.observedSubstrateExitTransition()
            },
        ) {
            DivBlockView(
                data = substrate,
                blockTapPropagation = handlesSubstrateTaps,
            )
        }
        BringToTop(tooltip.bringToTopId, Modifier.matchParentSize())
        transition.AnimatedVisibility(
            modifier = Modifier.tooltipPosition(positionProvider),
            visible = { it },
            enter = enterTransition,
            exit = exitTransition,
        ) {
            DivBlockView(data = tooltip.div, blockTapPropagation = true)
        }
    }
}

private fun Modifier.tooltipPosition(provider: TooltipPositionProvider): Modifier = layout {
    measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
    val windowSize = IntSize(constraints.maxWidth, constraints.maxHeight)
    layout(windowSize.width, windowSize.height) {
        val position = provider.calculateTooltipPosition(
            windowSize = windowSize,
            popupContentSize = IntSize(placeable.width, placeable.height),
        )
        placeable.place(position.x, position.y)
    }
}

@Composable
private fun rememberTapOutsideHandler(
    actions: List<DivAction>?,
    closeByTapOutside: Boolean,
    onDismiss: () -> Unit
): () -> Unit {
    val localComponent = LocalComponent.current
    return remember(actions, closeByTapOutside, onDismiss, localComponent) {
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
