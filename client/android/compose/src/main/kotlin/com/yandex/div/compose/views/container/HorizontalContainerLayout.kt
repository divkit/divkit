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
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div.compose.views.modifiers.verticalPaddings
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivSize

@Composable
internal fun ContainerHorizontalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val itemSpacing = data.itemSpacing.observedValue()
    val separator = data.separator
    val separatorVisibility = separator.resolveSeparatorVisibility()
    val visibleItems = data.visibleItems()

    val supportsMainAxisWeight = !data.width.isWrapContent
    val isCrossAxisWrapContent = data.height is DivSize.WrapContent
    var hasWeightedChildren = false
    var weightedChildrenMargins = 0.dp
    var needsCrossAxisIntrinsicSize = false
    var hasCrossAxisSizingChild = false
    for (item in visibleItems) {
        val child = item.value()
        if (supportsMainAxisWeight && child.width.isMatchParent) {
            hasWeightedChildren = true
            weightedChildrenMargins += child.observeHorizontalMarginsSum()
        }
        if (isCrossAxisWrapContent) {
            if (child.height.isMatchParent) {
                needsCrossAxisIntrinsicSize = true
            } else {
                hasCrossAxisSizingChild = true
            }
        }
    }

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        // Row's intrinsic height does not include baseline offsets. Let Row calculate those
        // offsets itself when baseline alignment and a match-parent child are combined.
        .applyIf(
            needsCrossAxisIntrinsicSize && verticalAlignment != DivContentAlignmentVertical.BASELINE
        ) {
            height(IntrinsicSize.Max)
        }

    Row(
        modifier = containerModifier,
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
            HorizontalChildItem(
                childDiv,
                data.width,
                alignByBaseline = verticalAlignment == DivContentAlignmentVertical.BASELINE &&
                    !(isCrossAxisWrapContent && childDiv.value().height.isMatchParent),
                // A match-parent child must not define a wrap-content cross axis when a sibling
                // can define it. Without such a sibling, legacy DivKit treats it as wrap-content.
                excludeCrossAxisFromIntrinsics = hasCrossAxisSizingChild &&
                    childDiv.value().height.isMatchParent,
                hasWeightedChildren,
                weightedChildrenMargins,
            )
        }
    }
}

@Composable
private fun RowScope.HorizontalChildItem(
    item: Div,
    containerMainSize: DivSize,
    alignByBaseline: Boolean,
    excludeCrossAxisFromIntrinsics: Boolean,
    hasWeightedChildren: Boolean,
    weightedChildrenMargins: Dp
) {
    val divBase = item.value()
    val isWeightedChild = divBase.width.isMatchParent && !containerMainSize.isWrapContent

    var modifier = Modifier
        .applyIf(excludeCrossAxisFromIntrinsics) {
            fixedIntrinsics(height = divBase.observeVerticalMarginsSum())
        }
        .then(
            makeHorizontalChildModifier(
                divBase.width,
                containerMainSize,
                hasWeightedChildren,
                weightedChildrenMargins
            )
        )
        .then(observeVerticalChildModifier(item, alignByBaseline = alignByBaseline))

    if (isWeightedChild) {
        val (startMargin, endMargin) = divBase.margins.observeHorizontalInsets()
        divBase.margins?.let { modifier = modifier.verticalPaddings(it) }

        if (startMargin > 0.dp) Spacer(Modifier.width(startMargin))
        DivBlockView(
            data = item,
            modifier = modifier,
            applyMargins = false
        )
        if (endMargin > 0.dp) Spacer(Modifier.width(endMargin))
    } else {
        DivBlockView(
            data = item,
            modifier = modifier
        )
    }
}

@Composable
private fun RowScope.makeHorizontalChildModifier(
    childSize: DivSize,
    containerSize: DivSize,
    hasWeightedChildren: Boolean,
    weightedChildrenMargins: Dp,
): Modifier = when {
    childSize.isMatchParent && containerSize.isWrapContent ->
        // TODO: Needs warning match_parent child and wrap_content container.
        Modifier
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
