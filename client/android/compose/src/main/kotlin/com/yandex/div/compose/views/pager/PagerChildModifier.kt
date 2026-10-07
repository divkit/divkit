package com.yandex.div.compose.views.pager

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
internal fun childModifier(
    isHorizontal: Boolean,
    viewportSize: Dp,
    crossAxisBounded: Boolean,
    listState: LazyListState,
    paddings: PaddingValues,
    pageSize: Dp?,
    startPadding: Dp,
    endPadding: Dp,
    layoutDirection: LayoutDirection,
    density: Density,
): Modifier {
    val measuredViewportSize = remember(listState) { derivedStateOf { listState.layoutInfo.viewportSize } }
    val scrollAxisModifier = scrollAxisSizeModifier(pageSize, isHorizontal, viewportSize, startPadding, endPadding)
    val crossModifier = crossAxisSizeModifier(
        isHorizontal = isHorizontal,
        crossAxisBounded = crossAxisBounded,
        viewportSize = measuredViewportSize,
        paddings = paddings,
        layoutDirection = layoutDirection,
        density = density,
    )
    return crossModifier.then(scrollAxisModifier)
}

private fun crossAxisSizeModifier(
    isHorizontal: Boolean,
    crossAxisBounded: Boolean,
    viewportSize: State<IntSize>,
    paddings: PaddingValues,
    layoutDirection: LayoutDirection,
    density: Density,
): Modifier {
    if (crossAxisBounded) {
        return if (isHorizontal) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
    }

    val crossAxisPaddingPx = with(density) {
        if (isHorizontal) {
            (paddings.calculateTopPadding() + paddings.calculateBottomPadding()).roundToPx()
        } else {
            (paddings.calculateStartPadding(layoutDirection) + paddings.calculateEndPadding(layoutDirection)).roundToPx()
        }
    }

    return Modifier.layout { measurable, constraints ->
        val crossAxisSize = viewportSize.value.crossAxisSize(isHorizontal)
        val childConstraints = if (crossAxisSize > 0) {
            constraints.withCrossAxisSize((crossAxisSize - crossAxisPaddingPx).coerceAtLeast(0), isHorizontal)
        } else {
            constraints
        }
        val placeable = measurable.measure(childConstraints)
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }
}

private fun scrollAxisSizeModifier(pageSize: Dp?, isHorizontal: Boolean, viewportSize: Dp, startPadding: Dp, endPadding: Dp): Modifier {
    if (pageSize != null) {
        return if (isHorizontal) Modifier.width(pageSize) else Modifier.height(pageSize)
    }
    val maxSize = (viewportSize - startPadding - endPadding).coerceAtLeast(0.dp)
    return if (isHorizontal) Modifier.widthIn(max = maxSize) else Modifier.heightIn(max = maxSize)
}

private fun IntSize.crossAxisSize(isHorizontal: Boolean): Int =
    if (isHorizontal) height else width

private fun Constraints.withCrossAxisSize(size: Int, isHorizontal: Boolean): Constraints = if (isHorizontal) {
    val height = constrainHeight(size)
    copy(minHeight = height, maxHeight = height)
} else {
    val width = constrainWidth(size)
    copy(minWidth = width, maxWidth = width)
}
