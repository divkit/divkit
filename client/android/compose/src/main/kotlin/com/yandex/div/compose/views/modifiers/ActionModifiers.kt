package com.yandex.div.compose.views.modifiers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation

@Composable
internal fun Modifier.actions(actions: DivActions): Modifier {
    if (actions.tapActions.isEmpty()
        && actions.doubleTapActions.isEmpty()
        && actions.longTapActions.isEmpty()
    ) {
        return this
    }

    return when (val name = actions.animation.name.observedValue()) {
        DivAnimation.Name.FADE ->
            clickableWithFade(actions)

        DivAnimation.Name.NATIVE ->
            clickable(
                actions = actions,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )

        DivAnimation.Name.NO_ANIMATION ->
            clickable(actions)

        DivAnimation.Name.SCALE,
        DivAnimation.Name.SET,
        DivAnimation.Name.TRANSLATE -> {
            reportError("Animation not supported: $name")
            clickable(actions)
        }
    }
}

private fun DivLocalComponent.createHandler(
    actions: List<DivAction>,
    source: DivActionSource
): (() -> Unit)? {
    if (actions.isEmpty()) {
        return null
    }
    return { handleActions(actions, source) }
}

@Composable
private fun Modifier.clickable(
    actions: DivActions,
    indication: Indication? = null,
    interactionSource: MutableInteractionSource? = null
): Modifier {
    val localComponent = LocalComponent.current
    return combinedClickable(
        indication = indication,
        interactionSource = interactionSource,
        onClick = localComponent.createHandler(
            actions = actions.tapActions,
            source = DivActionSource.TAP
        ) ?: {},
        onDoubleClick = localComponent.createHandler(
            actions = actions.doubleTapActions,
            source = DivActionSource.DOUBLE_TAP
        ),
        onLongClick = localComponent.createHandler(
            actions = actions.longTapActions,
            source = DivActionSource.LONG_TAP
        )
    )
}

@Composable
private fun Modifier.clickableWithFade(actions: DivActions): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animation = actions.animation
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) animation.endValue.observedFloatValue(1f) else 1f,
        animationSpec = tween(durationMillis = animation.duration.observedIntValue()),
        label = "alphaAnimation"
    )
    return graphicsLayer(alpha = animatedAlpha)
        .clickable(
            interactionSource = interactionSource,
            actions = actions
        )
}
