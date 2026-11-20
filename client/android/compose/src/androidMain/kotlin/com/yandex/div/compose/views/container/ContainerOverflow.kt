package com.yandex.div.compose.views.container

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

/**
 * Aligns content that is larger than the container the way the container aligns its children.
 * Compose centers a layout that reports a size beyond its constraints, e.g. a Row whose children
 * are shifted down to a common baseline, so the start of the content would move out of the
 * container.
 */
internal fun Modifier.alignContainerOverflow(
    horizontalAlignment: DivContentAlignmentHorizontal,
    verticalAlignment: DivContentAlignmentVertical,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    // The offsets undo the centering: half of the overflow is already outside on each side.
    val centeredX = (placeable.measuredWidth - placeable.width) / 2
    val centeredY = (placeable.measuredHeight - placeable.height) / 2
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(
            x = -centeredX * horizontalAlignment.overflowBias(layoutDirection),
            y = -centeredY * verticalAlignment.overflowBias,
        )
    }
}

/** -1 keeps the start of the content in place, 1 keeps its end, 0 keeps it centered. */
private fun DivContentAlignmentHorizontal.overflowBias(layoutDirection: LayoutDirection): Int = when (this) {
    DivContentAlignmentHorizontal.START -> -1
    DivContentAlignmentHorizontal.END -> 1
    DivContentAlignmentHorizontal.LEFT -> if (layoutDirection == LayoutDirection.Ltr) -1 else 1
    DivContentAlignmentHorizontal.RIGHT -> if (layoutDirection == LayoutDirection.Ltr) 1 else -1
    DivContentAlignmentHorizontal.CENTER,
    DivContentAlignmentHorizontal.SPACE_AROUND,
    DivContentAlignmentHorizontal.SPACE_BETWEEN,
    DivContentAlignmentHorizontal.SPACE_EVENLY -> 0
}

private val DivContentAlignmentVertical.overflowBias: Int
    get() = when (this) {
        DivContentAlignmentVertical.TOP,
        DivContentAlignmentVertical.BASELINE -> -1
        DivContentAlignmentVertical.BOTTOM -> 1
        DivContentAlignmentVertical.CENTER,
        DivContentAlignmentVertical.SPACE_AROUND,
        DivContentAlignmentVertical.SPACE_BETWEEN,
        DivContentAlignmentVertical.SPACE_EVENLY -> 0
    }
