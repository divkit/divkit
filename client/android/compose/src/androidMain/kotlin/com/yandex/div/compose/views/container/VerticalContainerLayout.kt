package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div.compose.views.modifiers.padding
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
    val visibleCollection = items.observeVisible()
    // An aspect height is definite like a fixed one: match-parent children share it and
    // constrained children shrink to it. Only a positive ratio applies, as in DivContainerView.
    val hasAspect = data.observedAspectRatio() != null

    val sizing = observeVerticalChildrenSizing(data, visibleCollection.items, hasAspect)
    val canAlignMainAxisOverflow = canAlignMainAxisOverflow(
        data.height,
        verticalAlignment,
        visibleCollection,
    )

    val containerModifier = modifier
        .padding(data.paddings)
        .alignContainerOverflow(horizontalAlignment, verticalAlignment)
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
                collection = visibleCollection,
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
            collection = visibleCollection,
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
 * Whether the container can switch between a Column and a [WeightedLinearLayout]. The layout follows
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
        WeightedLinearLayout(LinearAxis.Vertical(verticalArrangement), modifier) {
            remember(horizontalAlignment) { WeightedVerticalScope(horizontalAlignment) }.content()
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
    collection: VisibleCollectionItems,
    separatorVisibility: SeparatorVisibility,
    itemSpacing: Long,
    hasAspect: Boolean,
    hasCrossAxisSizingChild: Boolean,
    defaultHorizontalAlignment: Alignment.Horizontal,
    defaultVerticalAlignment: Alignment.Vertical,
) {
    LinearContainer(
        collection = collection,
        separator = data.separator,
        separatorVisibility = separatorVisibility,
        renderSeparator = { separatorData ->
            ContainerSeparator(separatorData, Modifier.verticalChild(alignment = Alignment.CenterHorizontally))
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

/** The child modifier a Column and a [WeightedLinearLayout] provide. */
private interface VerticalScope {
    /**
     * Lays a child out: [alignment] replaces the alignment of the container and [spec] is the share
     * of a match_parent child. A constrained child of a definite height shrinks: without match_parent
     * siblings it shares the height with the other children, with them it leaves them their min_size.
     */
    fun Modifier.verticalChild(
        alignment: Alignment.Horizontal? = null,
        spec: MatchParentSpec? = null,
        isConstrained: Boolean = false,
    ): Modifier
}

private class ColumnVerticalScope(private val scope: ColumnScope) : VerticalScope {
    // A Column lays out no match_parent children along a definite height.
    override fun Modifier.verticalChild(
        alignment: Alignment.Horizontal?,
        spec: MatchParentSpec?,
        isConstrained: Boolean,
    ): Modifier = with(scope) {
        applyIf(isConstrained) { weight(1f, fill = false) }.applyIfNotNull(alignment) { align(it) }
    }
}

private class WeightedVerticalScope(private val defaultAlignment: Alignment.Horizontal) : VerticalScope {
    override fun Modifier.verticalChild(
        alignment: Alignment.Horizontal?,
        spec: MatchParentSpec?,
        isConstrained: Boolean,
    ): Modifier = then(
        WeightedLinearChild(
            alignment = combineAlignment(alignment ?: defaultAlignment, Alignment.Top),
            spec = spec,
            isConstrained = isConstrained,
        )
    )
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
    collection: VisibleCollectionItems,
): Boolean {
    if (containerSize is DivSize.WrapContent) return false
    if (collection.items.isEmpty()) return false
    when (alignment) {
        DivContentAlignmentVertical.CENTER,
        DivContentAlignmentVertical.BOTTOM -> Unit
        else -> return false
    }
    return collection.items.all { item ->
        item.value().height.canMeasureMainAxisUnbounded(collection.components?.get(item))
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
    val modifier = Modifier
        .applyIf(excludeCrossAxisFromIntrinsics) {
            fixedIntrinsics(width = divBase.observeHorizontalMarginsSum())
        }
        .verticalChild(
            alignment = div.observeHorizontalChildAlignment(),
            spec = divBase.observeMatchParentHeightSpec(containerMainSize, hasAspect),
            isConstrained = divBase.height.observeShrinks(containerMainSize, hasAspect),
        )

    DivBlockView(
        data = div,
        modifier = modifier,
        layoutParams = DivBlockLayoutParams(
            defaultHorizontalAlignment = defaultHorizontalAlignment,
            defaultVerticalAlignment = defaultVerticalAlignment,
        ),
    )
}

/** The share of a match_parent child of a definite height. */
@Composable
private fun DivBase.observeMatchParentHeightSpec(containerSize: DivSize, hasAspect: Boolean): MatchParentSpec? {
    val size = height as? DivSize.MatchParent ?: return null
    // TODO: Needs warning match_parent child and wrap_content container.
    if (containerSize.isWrapContent && !hasAspect) return null
    val (topMargin, bottomMargin) = margins.observeVerticalInsets()
    return size.value.observeMatchParentSpec(topMargin, bottomMargin)
}

@Composable
private fun DivSize.observeShrinks(containerSize: DivSize, hasAspect: Boolean): Boolean =
    this is DivSize.WrapContent && value.constrained?.observedValue() == true &&
        (hasAspect || containerSize.observeIsConstrained())
