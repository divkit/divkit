package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowColumnOverflow
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.container.SeparatorVisibility
import com.yandex.div.compose.views.container.adaptiveContainerPadding
import com.yandex.div.compose.views.container.observeHorizontalChildAlignment
import com.yandex.div.compose.views.container.resolveSeparatorVisibility
import com.yandex.div.compose.views.container.observeVerticalChildAlignment
import com.yandex.div.compose.views.container.toContainerInsets
import com.yandex.div.compose.views.container.toCrossAxisHorizontalAlignment
import com.yandex.div.compose.views.container.toCrossAxisVerticalAlignment
import com.yandex.div.compose.views.container.toHorizontalArrangement
import com.yandex.div.compose.views.container.toVerticalArrangement
import com.yandex.div.compose.views.container.visibleItems
import com.yandex.div.compose.views.observedValue
import com.yandex.div2.DivContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ContainerWrapHorizontalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val wrapLayoutState = resolveWrapLayoutState(data, isHorizontal = true)
    val visibleItems = data.visibleItems()
    val childRects = rememberChildRects(visibleItems.size)

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings.toContainerInsets(), horizontalAlignment, verticalAlignment)
        .padding(
            start = wrapLayoutState.mainAxisStartPadding,
            end = wrapLayoutState.mainAxisEndPadding,
            top = wrapLayoutState.crossAxisStartPadding,
            bottom = wrapLayoutState.crossAxisEndPadding,
        )
        .drawWithContent {
            drawContent()
            drawHorizontalWrapSeparators(
                childRects,
                wrapLayoutState.itemSeparatorDrawInfo,
                wrapLayoutState.itemSeparatorVisibility,
                wrapLayoutState.lineSeparatorDrawInfo,
                wrapLayoutState.lineSeparatorVisibility,
                wrapLayoutState.mainAxisStartPadding.toPx(),
                wrapLayoutState.mainAxisEndPadding.toPx(),
                wrapLayoutState.crossAxisStartPadding.toPx(),
                wrapLayoutState.crossAxisEndPadding.toPx(),
            )
        }

    FlowRow(
        modifier = containerModifier,
        horizontalArrangement = horizontalAlignment.toHorizontalArrangement(wrapLayoutState.effectiveItemSpacing),
        verticalArrangement = verticalAlignment.toVerticalArrangement(wrapLayoutState.effectiveLineSpacing),
        overflow = FlowRowOverflow.Visible,
    ) {
        val defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment()
        visibleItems.forEachIndexed { index, childDiv ->
            val childModifier = childDiv.observeVerticalChildAlignment()?.let { Modifier.align(it) }
                ?: Modifier.align(defaultVerticalAlignment)
            DivBlockView(childDiv, childModifier.trackChildPlacement(childRects, index))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ContainerWrapVerticalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val wrapLayoutState = resolveWrapLayoutState(data, isHorizontal = false)
    val visibleItems = data.visibleItems()
    val childRects = rememberChildRects(visibleItems.size)

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings.toContainerInsets(), horizontalAlignment, verticalAlignment)
        .padding(
            top = wrapLayoutState.mainAxisStartPadding,
            bottom = wrapLayoutState.mainAxisEndPadding,
            start = wrapLayoutState.crossAxisStartPadding,
            end = wrapLayoutState.crossAxisEndPadding,
        )
        .drawWithContent {
            drawContent()
            drawVerticalWrapSeparators(
                childRects,
                wrapLayoutState.itemSeparatorDrawInfo,
                wrapLayoutState.itemSeparatorVisibility,
                wrapLayoutState.lineSeparatorDrawInfo,
                wrapLayoutState.lineSeparatorVisibility,
                wrapLayoutState.mainAxisStartPadding.toPx(),
                wrapLayoutState.mainAxisEndPadding.toPx(),
                wrapLayoutState.crossAxisStartPadding.toPx(),
                wrapLayoutState.crossAxisEndPadding.toPx(),
            )
        }

    FlowColumn(
        modifier = containerModifier,
        verticalArrangement = verticalAlignment.toVerticalArrangement(wrapLayoutState.effectiveItemSpacing),
        horizontalArrangement = horizontalAlignment.toHorizontalArrangement(wrapLayoutState.effectiveLineSpacing),
        overflow = FlowColumnOverflow.Visible,
    ) {
        val defaultHorizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment()
        visibleItems.forEachIndexed { index, childDiv ->
            val childModifier = childDiv.observeHorizontalChildAlignment()?.let { Modifier.align(it) }
                ?: Modifier.align(defaultHorizontalAlignment)
            DivBlockView(childDiv, childModifier.trackChildPlacement(childRects, index))
        }
    }
}

private class WrapLayoutState(
    val itemSeparatorDrawInfo: SeparatorDrawInfo?,
    val lineSeparatorDrawInfo: SeparatorDrawInfo?,
    val itemSeparatorVisibility: SeparatorVisibility,
    val lineSeparatorVisibility: SeparatorVisibility,
    val effectiveItemSpacing: Long,
    val effectiveLineSpacing: Long,
    val mainAxisStartPadding: Dp,
    val mainAxisEndPadding: Dp,
    val crossAxisStartPadding: Dp,
    val crossAxisEndPadding: Dp,
)

@Composable
private fun resolveWrapLayoutState(data: DivContainer, isHorizontal: Boolean): WrapLayoutState {
    val itemSeparatorVisibility = data.separator.resolveSeparatorVisibility()
    val lineSeparatorVisibility = data.lineSeparator.resolveSeparatorVisibility()
    val itemSeparatorDrawInfo = data.separator.resolveDrawInfo()
    val lineSeparatorDrawInfo = data.lineSeparator.resolveDrawInfo()

    val effectiveItemSpacing = when {
        itemSeparatorVisibility.showBetween && itemSeparatorDrawInfo != null ->
            itemSeparatorDrawInfo.mainAxisTotalDp(isHorizontal).value.toLong()
        else -> data.itemSpacing.observedValue()
    }

    val effectiveLineSpacing = when {
        lineSeparatorVisibility.showBetween && lineSeparatorDrawInfo != null ->
            lineSeparatorDrawInfo.crossAxisTotalDp(isHorizontal).value.toLong()
        else -> data.lineSpacing.observedValue()
    }

    return WrapLayoutState(
        itemSeparatorDrawInfo = itemSeparatorDrawInfo,
        lineSeparatorDrawInfo = lineSeparatorDrawInfo,
        itemSeparatorVisibility = itemSeparatorVisibility,
        lineSeparatorVisibility = lineSeparatorVisibility,
        effectiveItemSpacing = effectiveItemSpacing,
        effectiveLineSpacing = effectiveLineSpacing,
        mainAxisStartPadding = separatorEdgePadding(
            itemSeparatorVisibility.showAtStart, itemSeparatorDrawInfo,
        ) { it.mainAxisTotalDp(isHorizontal) },
        mainAxisEndPadding = separatorEdgePadding(
            itemSeparatorVisibility.showAtEnd, itemSeparatorDrawInfo,
        ) { it.mainAxisTotalDp(isHorizontal) },
        crossAxisStartPadding = separatorEdgePadding(
            lineSeparatorVisibility.showAtStart, lineSeparatorDrawInfo,
        ) { it.crossAxisTotalDp(isHorizontal) },
        crossAxisEndPadding = separatorEdgePadding(
            lineSeparatorVisibility.showAtEnd, lineSeparatorDrawInfo,
        ) { it.crossAxisTotalDp(isHorizontal) },
    )
}

private inline fun separatorEdgePadding(
    isVisible: Boolean,
    drawInfo: SeparatorDrawInfo?,
    totalSize: (SeparatorDrawInfo) -> Dp,
): Dp = if (isVisible && drawInfo != null) totalSize(drawInfo) else 0.dp

@Composable
private fun rememberChildRects(count: Int): MutableList<Rect> {
    val rects = remember { mutableListOf<Rect>() }
    SideEffect {
        val currentSize = rects.size
        when {
            currentSize < count -> repeat(count - currentSize) { rects.add(Rect.Zero) }
            currentSize > count -> rects.subList(count, currentSize).clear()
        }
    }
    return rects
}

private fun Modifier.trackChildPlacement(rects: MutableList<Rect>, index: Int): Modifier =
    onPlaced { layoutCoordinates ->
        val position = layoutCoordinates.positionInParent()
        if (index < rects.size) {
            rects[index] = Rect(
                left = position.x,
                top = position.y,
                right = position.x + layoutCoordinates.size.width,
                bottom = position.y + layoutCoordinates.size.height,
            )
        }
    }
