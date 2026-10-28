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
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
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
    val defaultHorizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment()
    val itemSpacing = data.itemSpacing.observedValue()
    val separator = data.separator
    val separatorVisibility = separator.resolveSeparatorVisibility()
    val items = data.observeItems()
    val visibleItems = items.observeVisible()

    val supportsMainAxisWeight = !data.height.isWrapContent
    val isCrossAxisWrapContent = data.width is DivSize.WrapContent
    var foundWeightedChildren = false
    var totalWeightedChildrenMargins = 0.dp
    var needsCrossAxisIntrinsicSize = false
    var foundCrossAxisSizingChild = false
    for (item in visibleItems) {
        val child = item.value()
        if (supportsMainAxisWeight && child.height.isMatchParent) {
            foundWeightedChildren = true
            totalWeightedChildrenMargins += child.observeVerticalMarginsSum(
                component = items.components?.get(item)
            )
        }
        if (isCrossAxisWrapContent) {
            if (child.width.isMatchParent) {
                needsCrossAxisIntrinsicSize = true
            } else {
                foundCrossAxisSizingChild = true
            }
        }
    }

    val hasWeightedChildren = foundWeightedChildren
    val weightedChildrenMargins = totalWeightedChildrenMargins
    val hasCrossAxisSizingChild = foundCrossAxisSizingChild

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        .applyIf(needsCrossAxisIntrinsicSize) { width(IntrinsicSize.Max) }

    Column(
        modifier = containerModifier,
        verticalArrangement = verticalAlignment.toVerticalArrangement(
            separatorVisibility.effectiveItemSpacing(itemSpacing),
        ),
        horizontalAlignment = defaultHorizontalAlignment,
    ) {
        LinearContainer(
            items = visibleItems,
            components = items.components,
            separator = separator,
            separatorVisibility = separatorVisibility,
            renderSeparator = { separatorData ->
                ContainerSeparator(separatorData, Modifier.align(Alignment.CenterHorizontally))
            },
            renderSpacing = {
                if (itemSpacing > 0) Spacer(Modifier.height(itemSpacing.toDp()))
            },
        ) { item ->
            VerticalChildItem(
                item,
                data.height,
                // A match-parent child must not define a wrap-content cross axis when a sibling
                // can define it. Without such a sibling, legacy DivKit treats it as wrap-content.
                excludeCrossAxisFromIntrinsics = hasCrossAxisSizingChild &&
                    item.value().width.isMatchParent,
                hasWeightedChildren,
                weightedChildrenMargins,
                defaultHorizontalAlignment = defaultHorizontalAlignment,
                defaultVerticalAlignment = defaultVerticalAlignment,
            )
        }
    }
}

@Composable
private fun ColumnScope.VerticalChildItem(
    div: Div,
    containerMainSize: DivSize,
    excludeCrossAxisFromIntrinsics: Boolean,
    hasWeightedChildren: Boolean,
    weightedChildrenMargins: Dp,
    defaultHorizontalAlignment: Alignment.Horizontal,
    defaultVerticalAlignment: Alignment.Vertical,
) {
    val divBase = div.value()
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
    div.observeHorizontalChildAlignment()?.let { modifier = modifier.align(it) }

    if (isWeightedChild) {
        val (top, bottom) = divBase.margins.observeVerticalInsets()
        divBase.margins?.let { modifier = modifier.horizontalPaddings(it) }

        if (top > 0.dp) Spacer(Modifier.height(top))
        DivBlockView(
            data = div,
            modifier = modifier,
            applyMargins = false,
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        )
        if (bottom > 0.dp) Spacer(Modifier.height(bottom))
    } else {
        DivBlockView(
            data = div,
            modifier = modifier,
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
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
