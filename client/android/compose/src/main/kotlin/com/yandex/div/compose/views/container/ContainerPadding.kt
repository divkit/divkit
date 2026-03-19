package com.yandex.div.compose.views.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivEdgeInsets

internal data class ContainerInsets(
    val start: Dp,
    val top: Dp,
    val end: Dp,
    val bottom: Dp,
)

@Composable
internal fun DivEdgeInsets?.toContainerInsets(): ContainerInsets =
    ContainerInsets(
        start = this?.run { (start ?: left).observedValue().toDp() } ?: 0.dp,
        top = this?.top.observedValue(0L).toDp(),
        end = this?.run { (end ?: right).observedValue().toDp() } ?: 0.dp,
        bottom = this?.bottom.observedValue(0L).toDp(),
    )

internal fun Modifier.adaptiveContainerPadding(
    insets: ContainerInsets,
    horizontalAlignment: DivContentAlignmentHorizontal,
    verticalAlignment: DivContentAlignmentVertical,
): Modifier = layout { measurable, constraints ->
    val padding = ResolvedPadding.from(insets, this)

    val initialConstraints = constraints.shrinkBy(padding.horizontal, padding.vertical)
    val initialPlaceable = measurable.measure(initialConstraints)

    val adaptedPadding = padding.adaptForOverflow(
        constraints, initialPlaceable, horizontalAlignment, verticalAlignment,
    )

    val placeable = if (adaptedPadding != padding) {
        measurable.measure(constraints.shrinkBy(adaptedPadding.horizontal, adaptedPadding.vertical))
    } else {
        initialPlaceable
    }

    val width = if (constraints.hasFixedWidth) {
        constraints.maxWidth
    } else {
        constraints.constrainWidth(placeable.width + adaptedPadding.horizontal)
    }
    val height = if (constraints.hasFixedHeight) {
        constraints.maxHeight
    } else {
        constraints.constrainHeight(placeable.height + adaptedPadding.vertical)
    }

    val overflowX = placeable.horizontalOverflowCompensation(horizontalAlignment)
    val overflowY = placeable.verticalOverflowCompensation(verticalAlignment)

    layout(width, height) {
        placeable.placeRelative(
            x = adaptedPadding.startPx + overflowX,
            y = adaptedPadding.topPx + overflowY,
        )
    }
}

private data class ResolvedPadding(
    val startPx: Int,
    val topPx: Int,
    val endPx: Int,
    val bottomPx: Int,
) {
    val horizontal: Int get() = startPx + endPx
    val vertical: Int get() = topPx + bottomPx

    companion object {
        fun from(insets: ContainerInsets, density: androidx.compose.ui.unit.Density) = ResolvedPadding(
            startPx = with(density) { insets.start.roundToPx() },
            topPx = with(density) { insets.top.roundToPx() },
            endPx = with(density) { insets.end.roundToPx() },
            bottomPx = with(density) { insets.bottom.roundToPx() },
        )
    }
}

private fun ResolvedPadding.adaptForOverflow(
    constraints: Constraints,
    placeable: Placeable,
    horizontalAlignment: DivContentAlignmentHorizontal,
    verticalAlignment: DivContentAlignmentVertical,
): ResolvedPadding {
    val shrunkConstraints = constraints.shrinkBy(horizontal, vertical)
    val hasHorizontalOverflow = constraints.hasBoundedWidth &&
        placeable.measuredWidth > shrunkConstraints.maxWidth
    val hasVerticalOverflow = constraints.hasBoundedHeight &&
        placeable.measuredHeight > shrunkConstraints.maxHeight

    if (!hasHorizontalOverflow && !hasVerticalOverflow) return this

    val (adaptedStart, adaptedEnd) = if (hasHorizontalOverflow) {
        adaptPaddingForOverflow(horizontalAlignment.overflowBehavior, startPx, endPx)
    } else {
        startPx to endPx
    }
    val (adaptedTop, adaptedBottom) = if (hasVerticalOverflow) {
        adaptPaddingForOverflow(verticalAlignment.overflowBehavior, topPx, bottomPx)
    } else {
        topPx to bottomPx
    }

    return ResolvedPadding(adaptedStart, adaptedTop, adaptedEnd, adaptedBottom)
}

private enum class OverflowBehavior {
    KEEP_START,
    KEEP_END,
    REMOVE_BOTH,
}

private val DivContentAlignmentHorizontal.overflowBehavior: OverflowBehavior
    get() = when (this) {
        DivContentAlignmentHorizontal.RIGHT,
        DivContentAlignmentHorizontal.END -> OverflowBehavior.KEEP_END
        DivContentAlignmentHorizontal.CENTER,
        DivContentAlignmentHorizontal.SPACE_AROUND,
        DivContentAlignmentHorizontal.SPACE_BETWEEN,
        DivContentAlignmentHorizontal.SPACE_EVENLY -> OverflowBehavior.REMOVE_BOTH
        else -> OverflowBehavior.KEEP_START
    }

private val DivContentAlignmentVertical.overflowBehavior: OverflowBehavior
    get() = when (this) {
        DivContentAlignmentVertical.BOTTOM -> OverflowBehavior.KEEP_END
        DivContentAlignmentVertical.CENTER,
        DivContentAlignmentVertical.SPACE_AROUND,
        DivContentAlignmentVertical.SPACE_BETWEEN,
        DivContentAlignmentVertical.SPACE_EVENLY -> OverflowBehavior.REMOVE_BOTH
        else -> OverflowBehavior.KEEP_START
    }

private fun adaptPaddingForOverflow(
    behavior: OverflowBehavior,
    startPx: Int,
    endPx: Int,
): Pair<Int, Int> = when (behavior) {
    OverflowBehavior.KEEP_START -> startPx to 0
    OverflowBehavior.KEEP_END -> 0 to endPx
    OverflowBehavior.REMOVE_BOTH -> 0 to 0
}

private fun Placeable.horizontalOverflowCompensation(alignment: DivContentAlignmentHorizontal): Int {
    val offset = (measuredWidth - width).coerceAtLeast(0) / 2
    return when (alignment) {
        DivContentAlignmentHorizontal.RIGHT,
        DivContentAlignmentHorizontal.END -> -offset
        DivContentAlignmentHorizontal.CENTER,
        DivContentAlignmentHorizontal.SPACE_AROUND,
        DivContentAlignmentHorizontal.SPACE_BETWEEN,
        DivContentAlignmentHorizontal.SPACE_EVENLY -> 0
        else -> offset
    }
}

private fun Placeable.verticalOverflowCompensation(alignment: DivContentAlignmentVertical): Int {
    val offset = (measuredHeight - height).coerceAtLeast(0) / 2
    return when (alignment) {
        DivContentAlignmentVertical.BOTTOM -> -offset
        DivContentAlignmentVertical.CENTER,
        DivContentAlignmentVertical.SPACE_AROUND,
        DivContentAlignmentVertical.SPACE_BETWEEN,
        DivContentAlignmentVertical.SPACE_EVENLY -> 0
        else -> offset
    }
}

private fun Constraints.shrinkBy(
    horizontalPadding: Int,
    verticalPadding: Int,
): Constraints = Constraints(
    minWidth = (minWidth - horizontalPadding).coerceAtLeast(0),
    maxWidth = if (hasBoundedWidth) (maxWidth - horizontalPadding).coerceAtLeast(0) else Constraints.Infinity,
    minHeight = (minHeight - verticalPadding).coerceAtLeast(0),
    maxHeight = if (hasBoundedHeight) (maxHeight - verticalPadding).coerceAtLeast(0) else Constraints.Infinity,
)
