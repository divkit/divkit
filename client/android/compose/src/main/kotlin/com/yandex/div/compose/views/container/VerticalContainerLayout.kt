package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
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
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.horizontalPaddings
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivSize

@Composable
internal fun ContainerVerticalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val itemSpacing = data.itemSpacing.observedValue()
    val separator = data.separator
    val separatorVisibility = separator.resolveSeparatorVisibility()
    val visibleItems = data.visibleItems()

    val hasWeightedChildren = !data.height.isWrapContent &&
        visibleItems.any { it.value().height.isMatchParent }
    val weightedChildrenMargins = resolveWeightedChildrenMargins(
        items = visibleItems,
        childMainSize = { it.height },
        childMainAxisMargins = { it.observeVerticalMarginsSum() },
    )

    val needsCrossAxisIntrinsicSize = data.width is DivSize.WrapContent
        && visibleItems.any { it.value().width is DivSize.MatchParent }

    val modifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        .applyIf(needsCrossAxisIntrinsicSize) { width(IntrinsicSize.Max) }

    Column(
        modifier = modifier,
        verticalArrangement = verticalAlignment.toVerticalArrangement(
            separatorVisibility.effectiveItemSpacing(itemSpacing),
        ),
        horizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment(),
    ) {
        LinearContainer(
            items = visibleItems,
            separator = separator,
            separatorVisibility = separatorVisibility,
            renderSeparator = { separatorData ->
                ContainerSeparator(separatorData, Modifier.align(Alignment.CenterHorizontally))
            },
            renderSpacing = {
                if (itemSpacing > 0) Spacer(Modifier.height(itemSpacing.toDp()))
            },
        ) { childDiv ->
            VerticalChildItem(childDiv, data.height, hasWeightedChildren, weightedChildrenMargins)
        }
    }
}

@Composable
private fun ColumnScope.VerticalChildItem(childDiv: Div, containerMainSize: DivSize, hasWeightedChildren: Boolean, weightedChildrenMargins: Dp) {
    val childBase = childDiv.value()
    val isWeightedChild = childBase.height is DivSize.MatchParent && !containerMainSize.isWrapContent

    var childModifier = makeVerticalChildModifier(
        childBase.height,
        containerMainSize,
        hasWeightedChildren,
        weightedChildrenMargins
    )
    childDiv.observeHorizontalChildAlignment()?.let { childModifier = childModifier.align(it) }

    if (isWeightedChild) {
        val (top, bottom) = childBase.margins.observeVerticalInsets()
        childBase.margins?.let { childModifier = childModifier.horizontalPaddings(it) }

        if (top > 0.dp) Spacer(Modifier.height(top))
        DivBlockView(childDiv, childModifier, applyMargins = false)
        if (bottom > 0.dp) Spacer(Modifier.height(bottom))
    } else {
        DivBlockView(childDiv, childModifier)
    }
}

@Composable
private fun ColumnScope.makeVerticalChildModifier(
    childSize: DivSize,
    containerSize: DivSize,
    hasWeightedChildren: Boolean,
    weightedChildrenMargins: Dp,
): Modifier = when {
    childSize is DivSize.MatchParent && containerSize.isWrapContent ->
        // TODO: Needs warning match_parent child and wrap_content container.
        Modifier.height(IntrinsicSize.Max)
    childSize is DivSize.MatchParent ->
        Modifier.weight(childSize.value.weight?.observedFloatValue() ?: 1f)
    childSize is DivSize.WrapContent && childSize.value.constrained?.observedValue() == true -> when {
        hasWeightedChildren ->
            Modifier.reduceMaxConstraint(weightedChildrenMargins, isWidth = false)
        containerSize.observeIsConstrained() ->
            Modifier.weight(1f, fill = false)
        else -> Modifier
    }
    else -> Modifier
}
