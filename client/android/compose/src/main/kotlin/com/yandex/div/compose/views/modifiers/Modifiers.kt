package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.tooltips.tooltipAnchors
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets

@Composable
internal fun Modifier.apply(
    div: Div,
    actions: DivActions?,
    applyMargins: Boolean
): Modifier {
    val divBase = div.value()
    return this
        .applyIf(applyMargins) { padding(divBase.margins) }
        .size(div)
        .visibilityActions(divBase)
        .applyIfNotNull(divBase.transform) { transform(it) }
        .appearance(divBase)
        // The actions must be applied AFTER the transformations and the border clipping in order
        // to have correct touch and animation area.
        // The actions must be applied BEFORE the background so that the action animation is applied
        // to the background.
        .applyIfNotNull(actions) { actions(it) }
        .background(divBase)
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

@Composable
internal fun Modifier.horizontalPaddings(value: DivEdgeInsets): Modifier {
    val (start, end) = value.observeHorizontalInsets()
    return padding(start = start, end = end)
}
