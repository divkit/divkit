package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.layoutprovider.layoutProvider
import com.yandex.div.compose.tooltips.tooltipAnchors
import com.yandex.div.compose.tooltips.tooltipInput
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.views.divVisibility
import com.yandex.div.internal.util.isConstantlyEmpty
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.apply(
    div: Div,
    actions: DivActions?,
    applyMargins: Boolean,
    visibility: DivVisibility,
    fillMatchParentWidth: Boolean = true,
    fillMatchParentHeight: Boolean = true,
    matchParentMaxWidth: Dp = Dp.Unspecified,
    matchParentMaxHeight: Dp = Dp.Unspecified,
    defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
    suppressMatchParentIntrinsics: Boolean = false,
    blockTapPropagation: Boolean = false,
): Modifier {
    val divBase = div.value()
    val margins = if (applyMargins) divBase.margins?.observeInsets() else null
    val focusState = rememberFocusState(divBase)
    val isFocused = focusState?.value == true
    val focus = divBase.focus
    val border = focus?.border?.takeIf { isFocused && !it.isConstantlyEmpty() }
        ?: divBase.border
    val background = focus?.background?.takeIf { isFocused && it.isNotEmpty() }
        ?: divBase.background
    return this
        .divVisibility(visibility)
        .applyIfNotNull(margins) { padding(it) }
        .applyIf(suppressMatchParentIntrinsics) { suppressMatchParentIntrinsics(divBase) }
        .size(
            div,
            fillMatchParentWidth = fillMatchParentWidth,
            fillMatchParentHeight = fillMatchParentHeight,
            matchParentMaxWidth = matchParentMaxWidth.excludeMargins(margins, isWidth = true),
            matchParentMaxHeight = matchParentMaxHeight.excludeMargins(margins, isWidth = false),
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        )
        .visibilityActions(divBase)
        .applyIfNotNull(divBase.layoutProvider) { layoutProvider(it) }
        .focus(div, focusState, hasActions = actions != null)
        .applyIfNotNull(divBase.transform) { transform(it) }
        .appearance(divBase, visibility, border)
        .applyIf(blockTapPropagation) { tooltipInput() }
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

// A match_parent bound covers the element together with its margins.
private fun Dp.excludeMargins(margins: PaddingValues?, isWidth: Boolean): Dp {
    if (isUnspecified || margins == null) return this
    val marginsSum = if (isWidth) {
        margins.calculateStartPadding(LayoutDirection.Ltr) + margins.calculateEndPadding(LayoutDirection.Ltr)
    } else {
        margins.calculateTopPadding() + margins.calculateBottomPadding()
    }
    return (this - marginsSum).coerceAtLeast(0.dp)
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

@Composable
internal fun Modifier.horizontalPaddings(value: DivEdgeInsets): Modifier {
    val (start, end) = value.observeHorizontalInsets()
    return padding(start = start, end = end)
}
