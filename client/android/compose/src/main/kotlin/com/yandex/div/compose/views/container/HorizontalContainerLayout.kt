package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.verticalPaddings
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivSize

@Composable
internal fun ContainerHorizontalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val itemSpacing = data.itemSpacing.observedValue()
    val separator = data.separator
    val separatorVisibility = separator.resolveSeparatorVisibility()
    val visibleItems = data.visibleItems()

    val hasWeightedChildren = !data.width.isWrapContent &&
        visibleItems.any { it.value().width.isMatchParent }
    val weightedChildrenMargins = resolveWeightedChildrenMargins(
        items = visibleItems,
        childMainSize = { it.width },
        childMainAxisMargins = { it.observeHorizontalMarginsSum() }
    )

    val needsCrossAxisIntrinsicSize = data.height is DivSize.WrapContent
        && visibleItems.any { it.value().height is DivSize.MatchParent }

    val modifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        .applyIf(needsCrossAxisIntrinsicSize) { height(IntrinsicSize.Max) }

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalAlignment.toHorizontalArrangement(
            separatorVisibility.effectiveItemSpacing(itemSpacing),
        ),
        verticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment(),
    ) {
        LinearContainer(
            items = visibleItems,
            separator = separator,
            separatorVisibility = separatorVisibility,
            renderSeparator = { separatorData ->
                ContainerSeparator(separatorData, Modifier.align(Alignment.CenterVertically))
            },
            renderSpacing = {
                if (itemSpacing > 0) Spacer(Modifier.width(itemSpacing.toDp()))
            },
        ) { childDiv ->
            HorizontalChildItem(childDiv, data.width, hasWeightedChildren, weightedChildrenMargins)
        }
    }
}

@Composable
private fun RowScope.HorizontalChildItem(childDiv: Div, containerMainSize: DivSize, hasWeightedChildren: Boolean, weightedChildrenMargins: Dp) {
    val childBase = childDiv.value()
    val isWeightedChild = childBase.width.isMatchParent && !containerMainSize.isWrapContent

    var childModifier = makeHorizontalChildModifier(
        childBase.width,
        containerMainSize,
        hasWeightedChildren,
        weightedChildrenMargins
    )
    childDiv.observeVerticalChildAlignment()?.let { childModifier = childModifier.align(it) }

    if (isWeightedChild) {
        val (startMargin, endMargin) = childBase.margins.observeHorizontalInsets()
        childBase.margins?.let { childModifier = childModifier.verticalPaddings(it) }

        if (startMargin > 0.dp) Spacer(Modifier.width(startMargin))
        DivBlockView(childDiv, childModifier, applyMargins = false)
        if (endMargin > 0.dp) Spacer(Modifier.width(endMargin))
    } else {
        DivBlockView(childDiv, childModifier)
    }
}

@Composable
private fun RowScope.makeHorizontalChildModifier(
    childSize: DivSize,
    containerSize: DivSize,
    hasWeightedChildren: Boolean,
    weightedChildrenMargins: Dp,
): Modifier = when {
    childSize is DivSize.MatchParent && containerSize.isWrapContent ->
        // TODO: Needs warning match_parent child and wrap_content container.
        Modifier.width(IntrinsicSize.Max)
    childSize is DivSize.MatchParent ->
        Modifier.weight(childSize.value.weight?.observedFloatValue() ?: 1f)
    childSize is DivSize.WrapContent && childSize.value.constrained?.observedValue() == true -> when {
        hasWeightedChildren ->
            Modifier.reduceMaxConstraint(weightedChildrenMargins, isWidth = true)
        containerSize.observeIsConstrained() ->
            Modifier.weight(1f, fill = false)
        else -> Modifier
    }
    else -> Modifier
}
