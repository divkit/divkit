package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivSize
import com.yandex.div2.DivVisibility

/**
 * A horizontal or vertical container without wrapping. The children are laid out by a Row or a
 * Column; match_parent children along a definite main axis need [WeightedLinearLayout] instead.
 */
@Composable
internal fun ContainerLinearView(modifier: Modifier, data: DivContainer, isHorizontal: Boolean) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val itemSpacing = data.itemSpacing.observedValue()
    val separatorVisibility = data.separator.resolveSeparatorVisibility()
    val items = data.observeItems()
    val visibleCollection = items.observeVisible()
    val container = observeLinearContainerParams(data, visibleCollection.items, isHorizontal)
    val overflowsAligned = container.mainSize.observeOverflowsAligned(
        isCenterOrEnd = if (isHorizontal) horizontalAlignment.isCenterOrEnd else verticalAlignment.isCenterOrEnd,
        collection = visibleCollection,
        isHorizontal = isHorizontal,
    )

    val content: @Composable LinearScope.() -> Unit = {
        LinearItems(
            collection = visibleCollection,
            separator = data.separator,
            separatorVisibility = separatorVisibility,
            itemSpacing = itemSpacing,
            container = container,
        )
    }
    // The children move between the two layouts, so a change of the layout, e.g. a match_parent
    // child becoming gone, keeps their state.
    val layoutContent = if (data.canChangeLayout(items.items, isHorizontal)) {
        val currentContent by rememberUpdatedState(content)
        remember { movableContentWithReceiverOf<LinearScope> { currentContent() } }
    } else {
        content
    }
    val spacing = separatorVisibility.effectiveItemSpacing(itemSpacing)
    LinearLayout(
        axis = if (isHorizontal) {
            LinearAxis.Horizontal(horizontalAlignment.toHorizontalArrangement(spacing))
        } else {
            LinearAxis.Vertical(verticalAlignment.toVerticalArrangement(spacing))
        },
        modifier = modifier
            .padding(data.paddings)
            .alignContainerOverflow(horizontalAlignment, verticalAlignment)
            // Measure unbounded children as one group so the overflowing group keeps its alignment.
            .applyIf(overflowsAligned) {
                if (isHorizontal) {
                    wrapContentWidth(align = container.defaultHorizontalAlignment, unbounded = true)
                } else {
                    wrapContentHeight(align = container.defaultVerticalAlignment, unbounded = true)
                }
            }
            .applyIf(container.wrapsCrossByIntrinsicSize) {
                if (isHorizontal) height(IntrinsicSize.Max) else width(IntrinsicSize.Max)
            },
        container = container,
        content = layoutContent,
    )
}

/** What the children of a linear container need to know about it. */
@Immutable
private data class LinearContainerParams(
    val isHorizontal: Boolean,
    val mainSize: DivSize,
    /** match_parent children share the main size, see [WeightedLinearLayout]. */
    val hasWeightedChildren: Boolean,
    /** Constrained children shrink to the main size. */
    val isMainBounded: Boolean,
    val alignByBaseline: Boolean,
    /** The cross size wraps the children, of which [crossMatchParentCount] are match_parent. */
    val wrapsCross: Boolean,
    private val crossMatchParentCount: Int,
    private val childCount: Int,
    val defaultHorizontalAlignment: Alignment.Horizontal,
    val defaultVerticalAlignment: Alignment.Vertical,
) {
    /**
     * match_parent children fill the cross size the layout wraps, so it has to be known before they
     * are measured. A Row's intrinsic height does not include baseline offsets, so a baseline row
     * calculates them itself.
     */
    val wrapsCrossByIntrinsicSize: Boolean
        get() = crossMatchParentCount > 0 && !alignByBaseline

    /**
     * A match_parent child must not define a wrap_content cross size when a sibling can define it.
     * Without such a sibling, the largest match_parent child defines it.
     */
    val hasCrossSizingChild: Boolean
        get() = wrapsCross && crossMatchParentCount < childCount

    val layoutParams = DivBlockLayoutParams(
        defaultHorizontalAlignment = defaultHorizontalAlignment,
        defaultVerticalAlignment = defaultVerticalAlignment,
    )
}

@Composable
private fun observeLinearContainerParams(
    data: DivContainer,
    visibleItems: List<Div>,
    isHorizontal: Boolean,
): LinearContainerParams {
    // A positive aspect ratio makes the height definite, like a fixed one.
    val hasAspect = data.observedAspectRatio() != null
    val mainSize = data.size(alongWidth = isHorizontal)
    val isMainDefinite = !mainSize.isWrapContent || (!isHorizontal && hasAspect)
    val wrapsCross = data.size(alongWidth = !isHorizontal).isWrapContent && !(isHorizontal && hasAspect)
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    return LinearContainerParams(
        isHorizontal = isHorizontal,
        mainSize = mainSize,
        hasWeightedChildren = isMainDefinite &&
            visibleItems.any { it.value().size(alongWidth = isHorizontal).isMatchParent },
        isMainBounded = mainSize.observeIsConstrained() || (!isHorizontal && hasAspect),
        alignByBaseline = isHorizontal && verticalAlignment == DivContentAlignmentVertical.BASELINE,
        wrapsCross = wrapsCross,
        crossMatchParentCount = if (wrapsCross) {
            visibleItems.count { it.value().size(alongWidth = !isHorizontal).isMatchParent }
        } else {
            0
        },
        childCount = visibleItems.size,
        defaultHorizontalAlignment = data.contentAlignmentHorizontal.observedValue().toCrossAxisHorizontalAlignment(),
        defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment(),
    )
}

/** The layout the children of a linear container are placed in. */
private sealed interface LinearScope {
    class InRow(val scope: RowScope) : LinearScope
    class InColumn(val scope: ColumnScope) : LinearScope
    data object Weighted : LinearScope
}

@Composable
@NonRestartableComposable
private fun LinearLayout(
    axis: LinearAxis,
    modifier: Modifier,
    container: LinearContainerParams,
    content: @Composable LinearScope.() -> Unit,
) {
    when {
        container.hasWeightedChildren -> WeightedLinearLayout(axis, modifier) { LinearScope.Weighted.content() }
        axis is LinearAxis.Horizontal -> Row(modifier, axis.arrangement, container.defaultVerticalAlignment) {
            remember(this) { LinearScope.InRow(this) }.content()
        }
        axis is LinearAxis.Vertical -> Column(modifier, axis.arrangement, container.defaultHorizontalAlignment) {
            remember(this) { LinearScope.InColumn(this) }.content()
        }
    }
}

/**
 * Whether the container can switch between a Row or a Column and a [WeightedLinearLayout]. The
 * layout follows the visible match_parent children along a definite main axis, so it stays the same
 * when the container has no such children or one of them is always visible. The items of an
 * item_builder come and go with its data, so there a match_parent prototype is enough.
 */
private fun DivContainer.canChangeLayout(items: List<Div>, isHorizontal: Boolean): Boolean {
    val mainSize = size(alongWidth = isHorizontal)
    val aspectRatio = if (isHorizontal) null else aspect?.ratio
    if (mainSize.isWrapContent && aspectRatio == null) return false
    itemBuilder?.let { builder ->
        return builder.prototypes.any { it.div.value().size(alongWidth = isHorizontal).isMatchParent }
    }
    if (items.none { it.value().size(alongWidth = isHorizontal).isMatchParent }) return false
    // A wrap_content height is definite only while the aspect ratio is valid.
    if (mainSize.isWrapContent && aspectRatio !is Expression.ConstantExpression) return true
    return items.none { item ->
        val child = item.value()
        val visibility = (child.visibility as? Expression.ConstantExpression)?.value
        child.size(alongWidth = isHorizontal).isMatchParent && visibility != null && visibility != DivVisibility.GONE
    }
}

private val DivContentAlignmentHorizontal.isCenterOrEnd: Boolean
    get() = when (this) {
        DivContentAlignmentHorizontal.CENTER,
        DivContentAlignmentHorizontal.RIGHT,
        DivContentAlignmentHorizontal.END -> true
        else -> false
    }

private val DivContentAlignmentVertical.isCenterOrEnd: Boolean
    get() = this == DivContentAlignmentVertical.CENTER || this == DivContentAlignmentVertical.BOTTOM

/**
 * Whether children overflowing this main size keep the container's center or end alignment as a
 * group: every child has to take its own size whatever the container offers.
 */
@Composable
private fun DivSize.observeOverflowsAligned(
    isCenterOrEnd: Boolean,
    collection: VisibleCollectionItems,
    isHorizontal: Boolean,
): Boolean {
    if (isWrapContent || !isCenterOrEnd || collection.items.isEmpty()) return false
    return collection.items.all { item ->
        when (val size = item.value().size(alongWidth = isHorizontal)) {
            is DivSize.Fixed -> true
            is DivSize.MatchParent -> false
            is DivSize.WrapContent -> {
                val component = collection.components?.get(item) ?: LocalComponent.current
                size.value.constrained?.observedValue(component = component) != true
            }
        }
    }
}

@Composable
@NonRestartableComposable
private fun LinearScope.LinearItems(
    collection: VisibleCollectionItems,
    separator: DivContainer.Separator?,
    separatorVisibility: SeparatorVisibility,
    itemSpacing: Long,
    container: LinearContainerParams,
) {
    val items = collection.items
    if (items.isEmpty()) return
    // Separators are children of the layout, so the items are spaced by children as well.
    val spacerSize = if (separatorVisibility.hasAnySeparator && !separatorVisibility.showBetween) itemSpacing else 0L
    Separator(separator, isShown = separatorVisibility.showAtStart)
    items.forEachIndexed { index, item ->
        // The child slot includes layout wrappers outside DivBlockView.
        WithDivKey(item) {
            WithLocalComponent(collection.components?.get(item)) {
                LinearItem(item, container)
            }
        }
        if (index < items.lastIndex) {
            Separator(separator, isShown = separatorVisibility.showBetween)
            if (spacerSize > 0) {
                val size = spacerSize.toDp()
                Spacer(if (container.isHorizontal) Modifier.width(size) else Modifier.height(size))
            }
        }
    }
    Separator(separator, isShown = separatorVisibility.showAtEnd)
}

@Composable
private fun LinearScope.Separator(separator: DivContainer.Separator?, isShown: Boolean) {
    if (separator == null || !isShown) return
    val modifier = when (this) {
        is LinearScope.InRow -> with(scope) { Modifier.align(Alignment.CenterVertically) }
        is LinearScope.InColumn -> with(scope) { Modifier.align(Alignment.CenterHorizontally) }
        LinearScope.Weighted -> WeightedLinearChild(alignment = Alignment.Center)
    }
    ContainerSeparator(separator, modifier)
}

@Composable
private fun LinearScope.LinearItem(div: Div, container: LinearContainerParams) {
    val child = div.value()
    val isHorizontal = container.isHorizontal
    val modifier = Modifier
        .applyIf(container.hasCrossSizingChild && child.size(alongWidth = !isHorizontal).isMatchParent) {
            if (isHorizontal) {
                fixedIntrinsics(height = child.observeVerticalMarginsSum())
            } else {
                fixedIntrinsics(width = child.observeHorizontalMarginsSum())
            }
        }
        .then(observeChildModifier(child, container))

    DivBlockView(
        data = div,
        modifier = modifier,
        layoutParams = container.layoutParams,
    )
}

@Composable
private fun LinearScope.observeChildModifier(child: DivBase, container: LinearContainerParams): Modifier {
    val mainSize = child.size(alongWidth = container.isHorizontal)
    val shrinks = container.isMainBounded &&
        (mainSize as? DivSize.WrapContent)?.value?.constrained?.observedValue() == true
    val alignment = child.observeCrossAlignment(container)
    return when (this) {
        is LinearScope.InRow -> with(scope) {
            val shrinkModifier = Modifier.applyIf(shrinks) { weight(1f, fill = false) }
            when {
                alignment.byBaseline -> shrinkModifier.alignByBaseline()
                alignment.vertical != null -> shrinkModifier.align(alignment.vertical)
                else -> shrinkModifier
            }
        }
        is LinearScope.InColumn -> with(scope) {
            Modifier.applyIf(shrinks) { weight(1f, fill = false) }.applyIfNotNull(alignment.horizontal) { align(it) }
        }
        LinearScope.Weighted -> WeightedLinearChild(
            alignment = combineAlignment(
                alignment.horizontal ?: container.defaultHorizontalAlignment,
                alignment.vertical ?: container.defaultVerticalAlignment,
            ),
            alignByBaseline = alignment.byBaseline,
            spec = (mainSize as? DivSize.MatchParent)?.value?.let { size ->
                val (beforeMargin, afterMargin) = if (container.isHorizontal) {
                    child.margins.observeHorizontalInsets()
                } else {
                    child.margins.observeVerticalInsets()
                }
                size.observeMatchParentSpec(beforeMargin, afterMargin)
            },
            isConstrained = shrinks,
        )
    }
}

/** The alignment of a child across the container, when the child or a baseline row sets one. */
private class CrossAlignment(
    val horizontal: Alignment.Horizontal?,
    val vertical: Alignment.Vertical?,
    val byBaseline: Boolean,
)

@Composable
private fun DivBase.observeCrossAlignment(container: LinearContainerParams): CrossAlignment {
    if (!container.isHorizontal) {
        return CrossAlignment(
            horizontal = alignmentHorizontal?.observedValue()?.toHorizontalAlignment(),
            vertical = null,
            byBaseline = false,
        )
    }
    val alignment = alignmentVertical?.observedValue()
    // A match_parent child fills a wrap_content height instead of following the baseline.
    val inheritsBaseline = container.alignByBaseline && !(container.wrapsCross && height.isMatchParent)
    return CrossAlignment(
        horizontal = null,
        vertical = alignment?.takeUnless { it == DivAlignmentVertical.BASELINE }?.toVerticalAlignment(),
        byBaseline = alignment == DivAlignmentVertical.BASELINE || (alignment == null && inheritsBaseline),
    )
}

private fun DivBase.size(alongWidth: Boolean): DivSize = if (alongWidth) width else height
