package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.actions.actions
import com.yandex.div.compose.layoutprovider.layoutProvider
import com.yandex.div.compose.tooltips.tooltipAnchors
import com.yandex.div.compose.tooltips.tooltipBringToTop
import com.yandex.div.compose.tooltips.tooltipInput
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.divVisibility
import com.yandex.div.compose.views.transition.StateChangeTransitionModifier
import com.yandex.div.compose.visibility.visibilityActions
import com.yandex.div.internal.util.isConstantlyEmpty
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.apply(
    div: Div,
    layoutParams: DivBlockLayoutParams,
    actions: DivActions?,
    margins: PaddingValues?,
    visibility: DivVisibility,
    focusState: MutableState<Boolean>?,
    stateChangeTransition: StateChangeTransitionModifier = StateChangeTransitionModifier(),
): Modifier {
    val divBase = div.value()
    val isFocused = focusState?.value == true
    val focus = divBase.focus
    val border = focus?.border?.takeIf { isFocused && !it.isConstantlyEmpty() }
        ?: divBase.border
    val background = focus?.background?.takeIf { isFocused && it.isNotEmpty() }
        ?: divBase.background
    return this
        .tooltipBringToTop(divBase.id)
        .divVisibility(visibility)
        .applyIfNotNull(margins) { padding(it) }
        .size(div, layoutParams, margins)
        .then(stateChangeTransition.modifier)
        .visibilityActions(divBase)
        .then(stateChangeTransition.snapshotModifier)
        .applyIfNotNull(divBase.layoutProvider) { layoutProvider(it) }
        .focus(div, focusState, hasActions = actions != null)
        .applyIfNotNull(divBase.transform) { transform(it) }
        .appearance(divBase, visibility, border, stateChangeTransition.appearanceAlpha)
        .applyIf(layoutParams.blockTapPropagation) { tooltipInput() }
        // The actions must be applied AFTER the transformations and the border clipping in order
        // to have correct touch and animation area.
        // The actions must be applied BEFORE the background so that the action animation is applied
        // to the background.
        .applyIfNotNull(actions) { actions(it, isFocusable = focus != null) }
        .background(background)
        .applyIfNotNull(divBase.id) { testTag(it) }
        .accessibility(divBase)
        .tooltipAnchors(divBase.tooltips)
}

@Composable
internal fun Modifier.applyPaddings(data: Div): Modifier {
    return padding(data.value().paddings)
}

@Composable
internal fun Modifier.padding(value: DivEdgeInsets?): Modifier {
    if (value == null) return this
    val insets = value.observeInsets()
    return padding(
        start = insets.calculateStartPadding(LayoutDirection.Ltr),
        end = insets.calculateEndPadding(LayoutDirection.Ltr),
        top = insets.calculateTopPadding(),
        bottom = insets.calculateBottomPadding(),
    )
}

@Composable
internal fun Modifier.verticalPaddings(value: DivEdgeInsets): Modifier {
    val (top, bottom) = value.observeVerticalInsets()
    return padding(top = top, bottom = bottom)
}
