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
import androidx.compose.ui.Modifier
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
    val scrollAxisModifier = scrollAxisSizeModifier(pageSize, isHorizontal, viewportSize, startPadding, endPadding)
    val crossModifier = crossAxisSizeModifier(
        isHorizontal = isHorizontal,
        crossAxisBounded = crossAxisBounded,
        viewportSize = listState.layoutInfo.viewportSize.crossAxisSize(isHorizontal),
        paddings = paddings,
        layoutDirection = layoutDirection,
        density = density,
    )
    return crossModifier.then(scrollAxisModifier)
}

private fun crossAxisSizeModifier(
    isHorizontal: Boolean,
    crossAxisBounded: Boolean,
    viewportSize: Int,
    paddings: PaddingValues,
    layoutDirection: LayoutDirection,
    density: Density,
): Modifier {
    if (crossAxisBounded || viewportSize <= 0) {
        return if (isHorizontal) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
    }

    val crossAxisPaddingPx = with(density) {
        if (isHorizontal) {
            (paddings.calculateTopPadding() + paddings.calculateBottomPadding()).roundToPx()
        } else {
            (paddings.calculateStartPadding(layoutDirection) + paddings.calculateEndPadding(layoutDirection)).roundToPx()
        }
    }

    val contentSizePx = (viewportSize - crossAxisPaddingPx).coerceAtLeast(0)
    return with(density) {
        if (isHorizontal) Modifier.height(contentSizePx.toDp()) else Modifier.width(contentSizePx.toDp())
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
