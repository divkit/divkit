package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.dagger.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.VisibleDivBlockView
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivSize
import com.yandex.div2.DivVisibility
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun ContainerOverlapView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val items = data.items.orEmpty()
    val defaultAlignment = combineAlignment(
        horizontalAlignment.toCrossAxisHorizontalAlignment(),
        verticalAlignment.toCrossAxisVerticalAlignment(),
    )
    val containerModifier = modifier.adaptiveContainerPadding(
        data.paddings,
        horizontalAlignment,
        verticalAlignment,
    )

    val isWidthWrapContent = data.width is DivSize.WrapContent
    val isHeightWrapContent = data.height is DivSize.WrapContent
    val needsMatchParentMeasurement = data.aspect == null && items.none { it.hasAspect } &&
        items.any {
            val child = it.value()
            (isWidthWrapContent && child.width.isMatchParent) ||
                (isHeightWrapContent && child.height.isMatchParent)
        }
    if (!needsMatchParentMeasurement) {
        Box(containerModifier, contentAlignment = defaultAlignment) {
            data.visibleItems().forEach { item ->
                DivBlockView(
                    data = item,
                    modifier = Modifier.align(
                        item.resolveOverlapChildAlignment(
                            defaultHorizontal = horizontalAlignment,
                            defaultVertical = verticalAlignment,
                        )
                    )
                )
            }
        }
        return
    }

    MatchParentOverlapLayout(
        modifier = containerModifier,
        items = items,
        isWidthWrapContent = isWidthWrapContent,
        isHeightWrapContent = isHeightWrapContent,
        defaultHorizontal = horizontalAlignment,
        defaultVertical = verticalAlignment,
    )
}

@Suppress("DEPRECATION")
@Composable
private fun MatchParentOverlapLayout(
    modifier: Modifier,
    items: List<Div>,
    isWidthWrapContent: Boolean,
    isHeightWrapContent: Boolean,
    defaultHorizontal: DivContentAlignmentHorizontal,
    defaultVertical: DivContentAlignmentVertical,
) {
    // View's FrameContainerLayout resolves wrap-content size before remeasuring match-parent
    // children. MultiMeasureLayout is intentionally limited to this compatibility path.
    MultiMeasureLayout(
        modifier = modifier,
        content = {
            items.forEach { item ->
                MatchParentOverlapChild(
                    item = item,
                    isWidthWrapContent = isWidthWrapContent,
                    isHeightWrapContent = isHeightWrapContent,
                    defaultHorizontal = defaultHorizontal,
                    defaultVertical = defaultVertical,
                )
            }
        },
        measurePolicy = overlapMeasurePolicy(
            isWidthWrapContent = isWidthWrapContent,
            isHeightWrapContent = isHeightWrapContent,
        ),
    )
}

@Composable
private fun MatchParentOverlapChild(
    item: Div,
    isWidthWrapContent: Boolean,
    isHeightWrapContent: Boolean,
    defaultHorizontal: DivContentAlignmentHorizontal,
    defaultVertical: DivContentAlignmentVertical,
) {
    val divBase = item.value()
    WithLocalComponent(divBase) {
        val visibility = divBase.visibility.observedValue()
        if (visibility == DivVisibility.GONE) return@WithLocalComponent

        val matchParentWidth = divBase.width.isMatchParent
        val matchParentHeight = divBase.height.isMatchParent
        val widthBounds = if (isWidthWrapContent && matchParentWidth) {
            divBase.width.observeMatchParentBounds()
        } else {
            UnboundedMatchParentBounds
        }
        val heightBounds = if (isHeightWrapContent && matchParentHeight) {
            divBase.height.observeMatchParentBounds()
        } else {
            UnboundedMatchParentBounds
        }
        val horizontalMargins = if (isWidthWrapContent && matchParentWidth) {
            divBase.margins.observeHorizontalInsets()
        } else {
            ZeroMargins
        }
        val verticalMargins = if (isHeightWrapContent && matchParentHeight) {
            divBase.margins.observeVerticalInsets()
        } else {
            ZeroMargins
        }
        val child = OverlapChild(
            alignment = item.resolveOverlapChildAlignment(
                defaultHorizontal = defaultHorizontal,
                defaultVertical = defaultVertical,
            ),
            matchParentWidth = matchParentWidth,
            matchParentHeight = matchParentHeight,
            hasPositiveFixedHeight = isWidthWrapContent && matchParentWidth &&
                divBase.height.observeIsPositiveFixedSize(),
            minWidth = widthBounds.minSize,
            maxWidth = widthBounds.maxSize,
            minHeight = heightBounds.minSize,
            maxHeight = heightBounds.maxSize,
            horizontalMarginStart = horizontalMargins.first,
            horizontalMarginEnd = horizontalMargins.second,
            verticalMarginTop = verticalMargins.first,
            verticalMarginBottom = verticalMargins.second,
        )
        // The wrapper keeps one measurable per Div and carries metadata resolved in the
        // same local-variable scope as the child itself.
        Box(
            modifier = Modifier.layoutId(child),
            propagateMinConstraints = true,
        ) {
            VisibleDivBlockView(
                data = item,
                modifier = Modifier,
                applyMargins = true,
                visibility = visibility,
                fillMatchParentWidth = !(isWidthWrapContent && matchParentWidth),
                fillMatchParentHeight = !(isHeightWrapContent && matchParentHeight),
            )
        }
    }
}

private fun overlapMeasurePolicy(
    isWidthWrapContent: Boolean,
    isHeightWrapContent: Boolean,
) = MeasurePolicy { measurables, constraints ->
    val children = measurables.map { measurable ->
        checkNotNull(measurable.layoutId as? OverlapChild)
    }

    val exactWidth = constraints.hasFixedWidth && !isWidthWrapContent
    val exactHeight = constraints.hasFixedHeight && !isHeightWrapContent
    val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = arrayOfNulls<Placeable>(children.size)
    val matchParentChildren = mutableListOf<Int>()
    var maxWidth = 0
    var maxHeight = 0

    fun resolveMatchParentChildSize(
        parentSize: Int,
        minSize: Dp,
        maxSize: Dp?,
        marginStart: Dp,
        marginEnd: Dp,
    ): Int {
        val marginsPx = marginStart.roundToPx() + marginEnd.roundToPx()
        val availableSize = max(0, parentSize - marginsPx)
        val maxSizePx = maxSize?.roundToPx() ?: Constraints.Infinity
        return min(max(availableSize, minSize.roundToPx()), maxSizePx) + marginsPx
    }

    children.forEachIndexed { index, child ->
        val matchDynamicWidth = isWidthWrapContent && !exactWidth && child.matchParentWidth
        val matchDynamicHeight = isHeightWrapContent && !exactHeight && child.matchParentHeight
        val matchDynamicSize = matchDynamicWidth || matchDynamicHeight
        val hasDefinedSize = when {
            exactWidth && exactHeight -> true
            exactHeight -> !matchDynamicWidth
            exactWidth -> !matchDynamicHeight
            !matchDynamicWidth -> true
            child.hasPositiveFixedHeight -> true
            else -> false
        }
        if (!hasDefinedSize) {
            if (matchDynamicSize) {
                matchParentChildren += index
            }
            return@forEachIndexed
        }

        val placeable = measurables[index].measure(looseConstraints)
        placeables[index] = placeable
        if (matchDynamicSize) {
            matchParentChildren += index
        }
        if (!exactWidth && !child.matchParentWidth) {
            maxWidth = max(maxWidth, placeable.width)
        }
        if (!exactHeight && !child.matchParentHeight) {
            maxHeight = max(maxHeight, placeable.height)
        }
    }

    if (matchParentChildren.isNotEmpty() && !(exactWidth && exactHeight)) {
        val needMeasureWidth = !exactWidth && maxWidth == 0
        val needMeasureHeight = !exactHeight && maxHeight == 0
        if (needMeasureWidth || needMeasureHeight) {
            matchParentChildren.forEach { index ->
                val child = children[index]
                var placeable = placeables[index]
                val measureContentWidth = isWidthWrapContent && !exactWidth &&
                    child.matchParentWidth && needMeasureWidth
                val measureContentHeight = isHeightWrapContent && !exactHeight &&
                    child.matchParentHeight && needMeasureHeight
                if (measureContentWidth || measureContentHeight) {
                    placeable = measurables[index].measure(looseConstraints)
                    placeables[index] = placeable
                }
                if (needMeasureWidth && placeable != null) {
                    maxWidth = max(maxWidth, placeable.width)
                }
                if (needMeasureHeight && placeable != null) {
                    maxHeight = max(maxHeight, placeable.height)
                }
            }
        } else {
            matchParentChildren.forEach { index ->
                val child = children[index]
                if (isWidthWrapContent && !exactWidth && child.matchParentWidth) {
                    maxWidth = max(
                        maxWidth,
                        child.minWidth.roundToPx() +
                            child.horizontalMarginStart.roundToPx() +
                            child.horizontalMarginEnd.roundToPx(),
                    )
                }
                if (isHeightWrapContent && !exactHeight && child.matchParentHeight) {
                    maxHeight = max(
                        maxHeight,
                        child.minHeight.roundToPx() +
                            child.verticalMarginTop.roundToPx() +
                            child.verticalMarginBottom.roundToPx(),
                    )
                }
            }
        }
    }

    val width = constraints.constrainWidth(maxWidth)
    val height = constraints.constrainHeight(maxHeight)
    matchParentChildren.forEach { index ->
        val child = children[index]
        val matchParentWidth = isWidthWrapContent && !exactWidth && child.matchParentWidth
        val matchParentHeight = isHeightWrapContent && !exactHeight && child.matchParentHeight
        placeables[index] = measurables[index].measure(
            looseConstraints.withResolvedMatchParentSize(
                width = if (matchParentWidth) {
                    resolveMatchParentChildSize(
                        parentSize = width,
                        minSize = child.minWidth,
                        maxSize = child.maxWidth,
                        marginStart = child.horizontalMarginStart,
                        marginEnd = child.horizontalMarginEnd,
                    )
                } else {
                    width
                },
                height = if (matchParentHeight) {
                    resolveMatchParentChildSize(
                        parentSize = height,
                        minSize = child.minHeight,
                        maxSize = child.maxHeight,
                        marginStart = child.verticalMarginTop,
                        marginEnd = child.verticalMarginBottom,
                    )
                } else {
                    height
                },
                matchParentWidth = matchParentWidth,
                matchParentHeight = matchParentHeight,
            )
        )
    }

    layout(width, height) {
        placeables.forEachIndexed { index, placeable ->
            placeable ?: return@forEachIndexed
            val position = children[index].alignment.align(
                size = IntSize(placeable.width, placeable.height),
                space = IntSize(width, height),
                layoutDirection = layoutDirection,
            )
            placeable.place(position.x, position.y)
        }
    }
}

private fun Constraints.withResolvedMatchParentSize(
    width: Int,
    height: Int,
    matchParentWidth: Boolean,
    matchParentHeight: Boolean,
): Constraints = copy(
    minWidth = if (matchParentWidth) width else minWidth,
    maxWidth = if (matchParentWidth) width else maxWidth,
    minHeight = if (matchParentHeight) height else minHeight,
    maxHeight = if (matchParentHeight) height else maxHeight,
)

@Composable
private fun DivSize.observeMatchParentBounds(): MatchParentBounds {
    if (this !is DivSize.MatchParent) return UnboundedMatchParentBounds
    val minSize = value.minSize?.let { it.value.observedDpValue(it.unit) } ?: 0.dp
    val maxSize = value.maxSize?.let { it.value.observedDpValue(it.unit) }
    return if (maxSize != null && minSize > maxSize) {
        UnboundedMatchParentBounds
    } else {
        MatchParentBounds(minSize = minSize, maxSize = maxSize)
    }
}

@Composable
private fun DivSize.observeIsPositiveFixedSize(): Boolean {
    if (this !is DivSize.Fixed) return false
    return value.value.observedDpValue(value.unit) > 0.dp
}

private data class OverlapChild(
    val alignment: Alignment,
    val matchParentWidth: Boolean,
    val matchParentHeight: Boolean,
    val hasPositiveFixedHeight: Boolean,
    val minWidth: Dp,
    val maxWidth: Dp?,
    val minHeight: Dp,
    val maxHeight: Dp?,
    val horizontalMarginStart: Dp,
    val horizontalMarginEnd: Dp,
    val verticalMarginTop: Dp,
    val verticalMarginBottom: Dp,
)

private data class MatchParentBounds(
    val minSize: Dp,
    val maxSize: Dp?,
)

private val UnboundedMatchParentBounds = MatchParentBounds(minSize = 0.dp, maxSize = null)
private val ZeroMargins = Pair(0.dp, 0.dp)

@Composable
private fun Div.resolveOverlapChildAlignment(
    defaultHorizontal: DivContentAlignmentHorizontal,
    defaultVertical: DivContentAlignmentVertical,
): Alignment {
    val divBase = value()
    return resolveOverlapChildAlignment(
        childHorizontal = divBase.alignmentHorizontal?.observedValue(),
        childVertical = divBase.alignmentVertical?.observedValue(),
        defaultHorizontal = defaultHorizontal,
        defaultVertical = defaultVertical,
    )
}

private fun resolveOverlapChildAlignment(
    childHorizontal: DivAlignmentHorizontal?,
    childVertical: DivAlignmentVertical?,
    defaultHorizontal: DivContentAlignmentHorizontal,
    defaultVertical: DivContentAlignmentVertical,
): Alignment {
    val horizontal = childHorizontal?.toHorizontalAlignment()
        ?: defaultHorizontal.toCrossAxisHorizontalAlignment()
    val vertical = childVertical?.toVerticalAlignment()
        ?: defaultVertical.toCrossAxisVerticalAlignment()
    return combineAlignment(horizontal, vertical)
}

private val Div.hasAspect: Boolean
    get() = when (this) {
        is Div.Container -> value.aspect != null
        is Div.GifImage -> value.aspect != null
        is Div.Image -> value.aspect != null
        is Div.Video -> value.aspect != null
        else -> false
    }
