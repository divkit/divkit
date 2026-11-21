package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div.compose.views.modifiers.verticalPaddings
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivSize

@Composable
internal fun ContainerHorizontalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val defaultHorizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment()
    val itemSpacing = data.itemSpacing.observedValue()
    val separator = data.separator
    val separatorVisibility = separator.resolveSeparatorVisibility()
    val items = data.observeItems()
    val visibleCollection = items.observeVisible()
    val (visibleItems, components) = visibleCollection

    val supportsMainAxisWeight = !data.width.isWrapContent
    // A positive aspect ratio makes the height definite: the cross axis does not wrap the children.
    val isCrossAxisWrapContent = data.height is DivSize.WrapContent && data.observedAspectRatio() == null
    val horizontalArrangement = horizontalAlignment.toHorizontalArrangement(
        separatorVisibility.effectiveItemSpacing(itemSpacing),
    )
    var foundWeightedChildren = false
    var foundWeightedChildMargins = false
    var totalWeightedChildrenReservedWidth = 0
    var hasMatchParentMinimum = false
    var needsCrossAxisIntrinsicSize = false
    var foundCrossAxisSizingChild = false
    val canAlignMainAxisOverflow = canAlignMainAxisOverflow(
        data.width,
        horizontalAlignment,
        visibleCollection,
    )
    for (item in visibleItems) {
        val child = item.value()
        val childWidth = child.width
        if (supportsMainAxisWeight && childWidth is DivSize.MatchParent) {
            foundWeightedChildren = true
            foundWeightedChildMargins = foundWeightedChildMargins || child.margins != null
            hasMatchParentMinimum = hasMatchParentMinimum || childWidth.value.minSize != null
            totalWeightedChildrenReservedWidth += child.observeReservedWidth(
                spacing = horizontalArrangement.spacing,
                component = components?.get(item) ?: LocalComponent.current,
            )
        }
        if (isCrossAxisWrapContent) {
            if (child.height.isMatchParent) {
                needsCrossAxisIntrinsicSize = true
            } else {
                foundCrossAxisSizingChild = true
            }
        }
    }

    val hasWeightedChildren = foundWeightedChildren
    val hasConstrainedChildren = hasWeightedChildren && visibleItems.any { item ->
        val size = item.value().width as? DivSize.WrapContent
        size?.value?.constrained?.observedValue(
            component = components?.get(item) ?: LocalComponent.current,
        ) == true
    }
    val useMatchParentRow = hasConstrainedChildren && hasMatchParentMinimum
    val weightedChildrenReservedWidth = totalWeightedChildrenReservedWidth
    val hasCrossAxisSizingChild = foundCrossAxisSizingChild
    // The margins of match_parent children are children of the Row, so the arrangement would
    // space them out as well. Spacers between the items take the place of its spacing.
    val spacingAsItems = !useMatchParentRow && foundWeightedChildMargins && horizontalArrangement.spacing > 0.dp

    val containerModifier = modifier
        .padding(data.paddings)
        .alignContainerOverflow(horizontalAlignment, verticalAlignment)
        // Measure unbounded children as one row so the overflowing group keeps its alignment.
        .applyIf(canAlignMainAxisOverflow) {
            wrapContentWidth(
                align = defaultHorizontalAlignment,
                unbounded = true,
            )
        }
        // Row's intrinsic height does not include baseline offsets. Let Row calculate those
        // offsets itself when baseline alignment and a match-parent child are combined.
        .applyIf(
            needsCrossAxisIntrinsicSize && verticalAlignment != DivContentAlignmentVertical.BASELINE
        ) {
            height(IntrinsicSize.Max)
        }

    HorizontalLayout(
        useMatchParentRow = useMatchParentRow,
        modifier = containerModifier,
        horizontalArrangement = if (spacingAsItems) {
            horizontalAlignment.toHorizontalArrangement(itemSpacing = 0.dp)
        } else {
            horizontalArrangement
        },
        verticalAlignment = defaultVerticalAlignment,
    ) { rowScope ->
        LinearContainer(
            collection = visibleCollection,
            separator = separator,
            separatorVisibility = separatorVisibility,
            spacingAsItems = spacingAsItems,
            renderSeparator = { separatorData ->
                val separatorModifier = rowScope?.run { Modifier.align(Alignment.CenterVertically) }
                    ?: MatchParentRowChild(alignment = Alignment.CenterVertically)
                ContainerSeparator(separatorData, separatorModifier)
            },
            renderSpacing = { if (itemSpacing > 0) Spacer(Modifier.width(itemSpacing.toDp())) },
        ) { item ->
            rowScope.HorizontalChildItem(
                item,
                data.width,
                alignByBaseline = verticalAlignment == DivContentAlignmentVertical.BASELINE,
                isCrossAxisWrapContent = isCrossAxisWrapContent,
                hasCrossAxisSizingChild = hasCrossAxisSizingChild,
                hasWeightedChildren = hasWeightedChildren,
                weightedChildrenReservedWidth = weightedChildrenReservedWidth,
                defaultHorizontalAlignment = defaultHorizontalAlignment,
                defaultVerticalAlignment = defaultVerticalAlignment,
            )
        }
    }
}

@Composable
@NonRestartableComposable
private fun HorizontalLayout(
    useMatchParentRow: Boolean,
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    verticalAlignment: Alignment.Vertical,
    content: @Composable (RowScope?) -> Unit,
) {
    // A patch can add the first bounded child. Keep its composition when switching layouts.
    val currentContent by rememberUpdatedState(content)
    val movableContent = remember { movableContentOf<RowScope?> { currentContent(it) } }
    if (useMatchParentRow) {
        MatchParentRow(modifier, horizontalArrangement, verticalAlignment) { movableContent(null) }
    } else {
        Row(modifier, horizontalArrangement, verticalAlignment) { movableContent(this) }
    }
}

/**
 * Reserves match_parent margins and item spacing for constrained siblings measured by Row.
 */
@Composable
private fun DivBase.observeReservedWidth(spacing: Dp, component: DivLocalComponent): Int {
    val (startMargin, endMargin) = margins.observeHorizontalInsets(component)
    // Each length is rounded on its own, as the Row rounds it.
    return with(LocalDensity.current) {
        startMargin.roundToPx() + endMargin.roundToPx() + spacing.roundToPx()
    }
}

@Composable
private fun canAlignMainAxisOverflow(
    containerSize: DivSize,
    alignment: DivContentAlignmentHorizontal,
    collection: VisibleCollectionItems,
): Boolean {
    if (containerSize is DivSize.WrapContent) return false
    if (collection.items.isEmpty()) return false
    when (alignment) {
        DivContentAlignmentHorizontal.CENTER,
        DivContentAlignmentHorizontal.RIGHT,
        DivContentAlignmentHorizontal.END -> Unit
        else -> return false
    }
    return collection.items.all { item ->
        item.value().width.canMeasureMainAxisUnbounded(collection.components?.get(item))
    }
}

@Composable
private fun DivSize.canMeasureMainAxisUnbounded(component: DivLocalComponent?): Boolean = when (this) {
    is DivSize.Fixed -> true
    is DivSize.MatchParent -> false
    is DivSize.WrapContent -> (component?.let {
        value.constrained?.observedValue(component = it)
    } ?: value.constrained?.observedValue()) != true
}

@Composable
private fun RowScope?.HorizontalChildItem(
    div: Div,
    containerMainSize: DivSize,
    alignByBaseline: Boolean,
    isCrossAxisWrapContent: Boolean,
    hasCrossAxisSizingChild: Boolean,
    hasWeightedChildren: Boolean,
    weightedChildrenReservedWidth: Int,
    defaultHorizontalAlignment: Alignment.Horizontal,
    defaultVerticalAlignment: Alignment.Vertical,
) {
    val divBase = div.value()
    val useBaseline = alignByBaseline && !(isCrossAxisWrapContent && divBase.height.isMatchParent)
    val separateMargins = this != null && divBase.width.isMatchParent && !containerMainSize.isWrapContent
    var modifier = Modifier
        .applyIf(hasCrossAxisSizingChild && divBase.height.isMatchParent) {
            fixedIntrinsics(height = divBase.observeVerticalMarginsSum())
        }
        .then(
            if (this == null) {
                observeMatchParentRowChild(div, alignByBaseline = useBaseline)
            } else {
                makeHorizontalChildModifier(
                    divBase.width,
                    containerMainSize,
                    hasWeightedChildren,
                    weightedChildrenReservedWidth,
                ).then(observeVerticalChildModifier(div, alignByBaseline = useBaseline))
            }
        )

    val (startMargin, endMargin) = if (separateMargins) divBase.margins.observeHorizontalInsets() else 0.dp to 0.dp
    if (separateMargins) divBase.margins?.let { modifier = modifier.verticalPaddings(it) }
    if (startMargin > 0.dp) Spacer(Modifier.width(startMargin))
    DivBlockView(
        data = div,
        modifier = modifier,
        layoutParams = DivBlockLayoutParams(
            applyMargins = !separateMargins,
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        ),
    )
    if (endMargin > 0.dp) Spacer(Modifier.width(endMargin))
}

@Composable
private fun RowScope.makeHorizontalChildModifier(
    childSize: DivSize,
    containerSize: DivSize,
    hasWeightedChildren: Boolean,
    weightedChildrenReservedWidth: Int,
): Modifier = when {
    childSize.isMatchParent && containerSize.isWrapContent ->
        Modifier
    childSize is DivSize.MatchParent ->
        Modifier.weight(childSize.value.weight?.observedFloatValue() ?: 1f)
    childSize is DivSize.WrapContent && childSize.value.constrained?.observedValue() == true -> when {
        hasWeightedChildren ->
            Modifier.reduceMaxConstraint(weightedChildrenReservedWidth, isWidth = true)
        containerSize.observeIsConstrained() ->
            Modifier.weight(1f, fill = false)
        else -> Modifier
    }
    else -> Modifier
}
