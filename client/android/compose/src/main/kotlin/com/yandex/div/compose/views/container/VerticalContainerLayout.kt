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
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div.compose.views.modifiers.horizontalPaddings
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

    val supportsMainAxisWeight = !data.height.isWrapContent
    val isCrossAxisWrapContent = data.width is DivSize.WrapContent
    var hasWeightedChildren = false
    var weightedChildrenMargins = 0.dp
    var needsCrossAxisIntrinsicSize = false
    var hasCrossAxisSizingChild = false
    for (item in visibleItems) {
        val child = item.value()
        if (supportsMainAxisWeight && child.height.isMatchParent) {
            hasWeightedChildren = true
            weightedChildrenMargins += child.observeVerticalMarginsSum()
        }
        if (isCrossAxisWrapContent) {
            if (child.width.isMatchParent) {
                needsCrossAxisIntrinsicSize = true
            } else {
                hasCrossAxisSizingChild = true
            }
        }
    }

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        .applyIf(needsCrossAxisIntrinsicSize) { width(IntrinsicSize.Max) }

    Column(
        modifier = containerModifier,
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
            VerticalChildItem(
                childDiv,
                data.height,
                // A match-parent child must not define a wrap-content cross axis when a sibling
                // can define it. Without such a sibling, legacy DivKit treats it as wrap-content.
                excludeCrossAxisFromIntrinsics = hasCrossAxisSizingChild &&
                    childDiv.value().width.isMatchParent,
                hasWeightedChildren,
                weightedChildrenMargins,
            )
        }
    }
}

@Composable
private fun ColumnScope.VerticalChildItem(
    item: Div,
    containerMainSize: DivSize,
    excludeCrossAxisFromIntrinsics: Boolean,
    hasWeightedChildren: Boolean,
    weightedChildrenMargins: Dp
) {
    val divBase = item.value()
    val isWeightedChild = divBase.height is DivSize.MatchParent && !containerMainSize.isWrapContent

    var modifier = Modifier
        .applyIf(excludeCrossAxisFromIntrinsics) {
            fixedIntrinsics(width = divBase.observeHorizontalMarginsSum())
        }
        .then(
            makeVerticalChildModifier(
                divBase.height,
                containerMainSize,
                hasWeightedChildren,
                weightedChildrenMargins
            )
        )
    item.observeHorizontalChildAlignment()?.let { modifier = modifier.align(it) }

    if (isWeightedChild) {
        val (top, bottom) = divBase.margins.observeVerticalInsets()
        divBase.margins?.let { modifier = modifier.horizontalPaddings(it) }

        if (top > 0.dp) Spacer(Modifier.height(top))
        DivBlockView(
            data = item,
            modifier = modifier,
            applyMargins = false
        )
        if (bottom > 0.dp) Spacer(Modifier.height(bottom))
    } else {
        DivBlockView(
            data = item,
            modifier = modifier
        )
    }
}

@Composable
private fun ColumnScope.makeVerticalChildModifier(
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
            Modifier.reduceMaxConstraint(weightedChildrenMargins, isWidth = false)
        containerSize.observeIsConstrained() ->
            Modifier.weight(1f, fill = false)
        else -> Modifier
    }
    else -> Modifier
}
