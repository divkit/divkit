package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

internal enum class CrossAxisAlignment {
    START, CENTER, END;

    fun toVerticalAlignment(): Alignment.Vertical = when (this) {
        START -> Alignment.Top
        CENTER -> Alignment.CenterVertically
        END -> Alignment.Bottom
    }

    fun toHorizontalAlignment(): Alignment.Horizontal = when (this) {
        START -> Alignment.Start
        CENTER -> Alignment.CenterHorizontally
        END -> Alignment.End
    }

    fun toBoxAlignment(isHorizontal: Boolean): Alignment =
        if (isHorizontal) {
            when (this) {
                START -> Alignment.TopStart
                CENTER -> Alignment.CenterStart
                END -> Alignment.BottomStart
            }
        } else {
            when (this) {
                START -> Alignment.TopStart
                CENTER -> Alignment.TopCenter
                END -> Alignment.TopEnd
            }
        }
}

internal fun PaddingValues.getScrollAxisPaddings(
    isHorizontal: Boolean,
    layoutDirection: LayoutDirection,
): Pair<Dp, Dp> {
    return if (isHorizontal) {
        Pair(calculateStartPadding(layoutDirection), calculateEndPadding(layoutDirection))
    } else {
        Pair(calculateTopPadding(), calculateBottomPadding())
    }
}

internal fun BoxWithConstraintsScope.isScrollAxisBounded(isHorizontal: Boolean): Boolean =
    if (isHorizontal) constraints.hasBoundedWidth else constraints.hasBoundedHeight

/** Size available to a match_parent item along the scroll axis: the viewport without [contentPadding]. */
internal fun BoxWithConstraintsScope.scrollAxisContentSize(
    contentPadding: PaddingValues,
    isHorizontal: Boolean,
    layoutDirection: LayoutDirection,
): Dp {
    val (startPadding, endPadding) = contentPadding.getScrollAxisPaddings(isHorizontal, layoutDirection)
    val viewportSize = if (isHorizontal) maxWidth else maxHeight
    return (viewportSize - startPadding - endPadding).coerceAtLeast(0.dp)
}
