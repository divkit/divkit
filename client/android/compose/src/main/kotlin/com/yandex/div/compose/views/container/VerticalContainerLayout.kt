package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivSize
import com.yandex.div2.DivVisibility

@Composable
internal fun ContainerVerticalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val defaultHorizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment()
    val itemSpacing = data.itemSpacing.observedValue()
    val separatorVisibility = data.separator.resolveSeparatorVisibility()
    val items = data.observeItems()
    val visibleItems = items.observeVisible()
    // An aspect height is definite like a fixed one: match-parent children share it and
    // constrained children shrink to it. Only a positive ratio applies, as in DivContainerView.
    val hasAspect = data.observedAspectRatio() != null

    val sizing = observeVerticalChildrenSizing(data, visibleItems, hasAspect)
    val canAlignMainAxisOverflow = canAlignMainAxisOverflow(
        data.height,
        verticalAlignment,
        visibleItems,
        items.components,
    )

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        // Measure unbounded children as one column so the overflowing group keeps its alignment.
        .applyIf(canAlignMainAxisOverflow) {
            wrapContentHeight(
                align = defaultVerticalAlignment,
                unbounded = true,
            )
        }
        .applyIf(sizing.needsCrossAxisIntrinsicSize) { width(IntrinsicSize.Max) }

    val verticalArrangement = verticalAlignment.toVerticalArrangement(
        separatorVisibility.effectiveItemSpacing(itemSpacing),
    )
    if (!data.canChangeLayout(items.items)) {
        VerticalLayout(
            hasWeightedChildren = sizing.hasWeightedChildren,
            modifier = containerModifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = defaultHorizontalAlignment,
        ) {
            VerticalContainerItems(
                data = data,
                visibleItems = visibleItems,
                components = items.components,
                separatorVisibility = separatorVisibility,
                itemSpacing = itemSpacing,
                hasAspect = hasAspect,
                hasCrossAxisSizingChild = sizing.hasCrossAxisSizingChild,
                defaultHorizontalAlignment = defaultHorizontalAlignment,
                defaultVerticalAlignment = defaultVerticalAlignment,
            )
        }
        return
    }

    val content: @Composable VerticalScope.() -> Unit = {
        VerticalContainerItems(
            data = data,
            visibleItems = visibleItems,
            components = items.components,
            separatorVisibility = separatorVisibility,
            itemSpacing = itemSpacing,
            hasAspect = hasAspect,
            hasCrossAxisSizingChild = sizing.hasCrossAxisSizingChild,
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        )
    }
    // The children move between the two layouts, so a change of the measure policy, e.g. a
    // match_parent child becoming gone, keeps their state.
    val currentContent by rememberUpdatedState(content)
    val movableContent = remember { movableContentWithReceiverOf<VerticalScope> { currentContent() } }
    VerticalLayout(
        hasWeightedChildren = sizing.hasWeightedChildren,
        modifier = containerModifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = defaultHorizontalAlignment,
    ) {
        movableContent()
    }
}

/**
 * Whether the container can switch between a Column and a [MatchParentColumn]. The layout follows
 * the visible match_parent children of a definite height, so it stays the same when the container
 * has no such children or one of them is always visible.
 */
private fun DivContainer.canChangeLayout(items: List<Div>): Boolean {
    val aspectRatio = aspect?.ratio
    if (height.isWrapContent && aspectRatio == null) return false
    if (items.none { it.value().height.isMatchParent }) return false
    if (itemBuilder != null) return true
    // A wrap_content height is definite only while the aspect ratio is valid.
    if (height.isWrapContent && aspectRatio !is Expression.ConstantExpression) return true
    return items.none { item ->
        val child = item.value()
        val visibility = (child.visibility as? Expression.ConstantExpression)?.value
        child.height.isMatchParent && visibility != null && visibility != DivVisibility.GONE
    }
}

@Composable
private inline fun VerticalLayout(
    hasWeightedChildren: Boolean,
    modifier: Modifier,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    crossinline content: @Composable VerticalScope.() -> Unit,
) {
    if (hasWeightedChildren) {
        MatchParentColumn(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            MatchParentColumnScope.content()
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            remember(this) { ColumnVerticalScope(this) }.content()
        }
    }
}

@Composable
@NonRestartableComposable
private fun VerticalScope.VerticalContainerItems(
    data: DivContainer,
    visibleItems: List<Div>,
    components: Map<Div, DivLocalComponent>?,
    separatorVisibility: SeparatorVisibility,
    itemSpacing: Long,
    hasAspect: Boolean,
    hasCrossAxisSizingChild: Boolean,
    defaultHorizontalAlignment: Alignment.Horizontal,
    defaultVerticalAlignment: Alignment.Vertical,
) {
    LinearContainer(
        items = visibleItems,
        components = components,
        separator = data.separator,
        separatorVisibility = separatorVisibility,
        renderSeparator = { separatorData ->
            ContainerSeparator(separatorData, Modifier.alignInColumn(Alignment.CenterHorizontally))
        },
        renderSpacing = {
            if (itemSpacing > 0) Spacer(Modifier.height(itemSpacing.toDp()))
        },
    ) { item ->
        VerticalChildItem(
            item,
            data.height,
            hasAspect = hasAspect,
            // A match-parent child must not define a wrap-content cross axis when a sibling
            // can define it. Without such a sibling, legacy DivKit treats it as wrap-content.
            excludeCrossAxisFromIntrinsics = hasCrossAxisSizingChild && item.value().width.isMatchParent,
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        )
    }
}

/** The child modifiers a Column and a [MatchParentColumn] provide. */
private interface VerticalScope {
    fun Modifier.alignInColumn(alignment: Alignment.Horizontal): Modifier

    /** A share of the height for a constrained child when the children have no match_parent sibling. */
    fun Modifier.shareHeight(): Modifier
}

private class ColumnVerticalScope(private val scope: ColumnScope) : VerticalScope {
    override fun Modifier.alignInColumn(alignment: Alignment.Horizontal): Modifier = with(scope) { align(alignment) }
    override fun Modifier.shareHeight(): Modifier = with(scope) { weight(1f, fill = false) }
}

private object MatchParentColumnScope : VerticalScope {
    override fun Modifier.alignInColumn(alignment: Alignment.Horizontal): Modifier = alignInMatchParentColumn(alignment)
    override fun Modifier.shareHeight(): Modifier = this
}

private class VerticalChildrenSizing(
    val hasWeightedChildren: Boolean,
    val needsCrossAxisIntrinsicSize: Boolean,
    val hasCrossAxisSizingChild: Boolean,
)

@Composable
private fun observeVerticalChildrenSizing(
    data: DivContainer,
    visibleItems: List<Div>,
    hasAspect: Boolean,
): VerticalChildrenSizing {
    val supportsMainAxisWeight = !data.height.isWrapContent || hasAspect
    val isCrossAxisWrapContent = data.width is DivSize.WrapContent
    var hasWeightedChildren = false
    var needsCrossAxisIntrinsicSize = false
    var hasCrossAxisSizingChild = false
    for (item in visibleItems) {
        val child = item.value()
        if (supportsMainAxisWeight && child.height.isMatchParent) {
            hasWeightedChildren = true
        }
        if (isCrossAxisWrapContent) {
            if (child.width.isMatchParent) {
                needsCrossAxisIntrinsicSize = true
            } else {
                hasCrossAxisSizingChild = true
            }
        }
    }
    return VerticalChildrenSizing(
        hasWeightedChildren = hasWeightedChildren,
        needsCrossAxisIntrinsicSize = needsCrossAxisIntrinsicSize,
        hasCrossAxisSizingChild = hasCrossAxisSizingChild,
    )
}

@Composable
private fun canAlignMainAxisOverflow(
    containerSize: DivSize,
    alignment: DivContentAlignmentVertical,
    items: List<Div>,
    components: Map<Div, DivLocalComponent>?,
): Boolean {
    if (containerSize is DivSize.WrapContent) return false
    if (items.isEmpty()) return false
    when (alignment) {
        DivContentAlignmentVertical.CENTER,
        DivContentAlignmentVertical.BOTTOM -> Unit
        else -> return false
    }
    return items.all { item ->
        item.value().height.canMeasureMainAxisUnbounded(components?.get(item))
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
private fun VerticalScope.VerticalChildItem(
    div: Div,
    containerMainSize: DivSize,
    hasAspect: Boolean,
    excludeCrossAxisFromIntrinsics: Boolean,
    defaultHorizontalAlignment: Alignment.Horizontal,
    defaultVerticalAlignment: Alignment.Vertical,
) {
    val divBase = div.value()
    var modifier = Modifier
        .applyIf(excludeCrossAxisFromIntrinsics) {
            fixedIntrinsics(width = divBase.observeHorizontalMarginsSum())
        }
        .then(makeVerticalChildModifier(divBase, containerMainSize, hasAspect))
    div.observeHorizontalChildAlignment()?.let { modifier = modifier.alignInColumn(it) }

    DivBlockView(
        data = div,
        modifier = modifier,
        layoutParams = DivBlockLayoutParams(
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        ),
    )
}

@Composable
private fun VerticalScope.makeVerticalChildModifier(
    child: DivBase,
    containerSize: DivSize,
    hasAspect: Boolean,
): Modifier {
    val childSize = child.height
    return when {
        childSize.isMatchParent && containerSize.isWrapContent && !hasAspect ->
            // TODO: Needs warning match_parent child and wrap_content container.
            Modifier
        childSize is DivSize.MatchParent ->
            Modifier.matchParentHeight(childSize.value, margins = child.observeVerticalMarginsSum())
        childSize is DivSize.WrapContent && childSize.value.constrained?.observedValue() == true -> when {
            hasAspect || containerSize.observeIsConstrained() -> Modifier.shareHeight()
            else -> Modifier
        }
        else -> Modifier
    }
}
