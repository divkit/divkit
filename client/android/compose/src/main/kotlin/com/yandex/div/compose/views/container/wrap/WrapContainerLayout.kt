@file:Suppress("DEPRECATION")

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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.container.SeparatorVisibility
import com.yandex.div.compose.views.container.adaptiveContainerPadding
import com.yandex.div.compose.views.container.observeHorizontalChildAlignment
import com.yandex.div.compose.views.container.observeVerticalChildModifier
import com.yandex.div.compose.views.container.resolveSeparatorVisibility
import com.yandex.div.compose.views.container.toCrossAxisHorizontalAlignment
import com.yandex.div.compose.views.container.toCrossAxisVerticalAlignment
import com.yandex.div.compose.views.container.toHorizontalArrangement
import com.yandex.div.compose.views.container.toVerticalArrangement
import com.yandex.div.compose.views.container.visibleItems
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentVertical

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ContainerWrapHorizontalView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val wrapLayoutState = resolveWrapLayoutState(data, isHorizontal = true)
    val visibleItems = data.visibleItems()
    val separatorGeometry = rememberSeparatorGeometry(
        enabled = wrapLayoutState.hasSeparatorsToDraw && visibleItems.isNotEmpty(),
        childCount = visibleItems.size,
        isHorizontal = true,
    )
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        .applyIf(wrapLayoutState.hasEdgePadding) {
            padding(
                start = wrapLayoutState.mainAxisStartPadding,
                end = wrapLayoutState.mainAxisEndPadding,
                top = wrapLayoutState.crossAxisStartPadding,
                bottom = wrapLayoutState.crossAxisEndPadding,
            )
        }
        .drawHorizontalWrapSeparators(separatorGeometry, wrapLayoutState, isRtl)

    FlowRow(
        modifier = containerModifier,
        horizontalArrangement = horizontalAlignment
            .toHorizontalArrangement(wrapLayoutState.effectiveItemSpacing),
        verticalArrangement = verticalAlignment
            .toVerticalArrangement(wrapLayoutState.effectiveLineSpacing),
        // DivKit keeps laying out children outside a constrained cross axis.
        overflow = FlowRowOverflow.Visible,
    ) {
        val defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment()
        visibleItems.forEachIndexed { index, item ->
            DivBlockView(
                data = item,
                modifier = observeVerticalChildModifier(
                    item = item,
                    defaultAlignment = defaultVerticalAlignment,
                    alignByBaseline = verticalAlignment == DivContentAlignmentVertical.BASELINE,
                ).trackChildPlacement(separatorGeometry, index),
            )
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
    val separatorGeometry = rememberSeparatorGeometry(
        enabled = wrapLayoutState.hasSeparatorsToDraw && visibleItems.isNotEmpty(),
        childCount = visibleItems.size,
        isHorizontal = false,
    )
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val containerModifier = modifier
        .adaptiveContainerPadding(data.paddings, horizontalAlignment, verticalAlignment)
        .applyIf(wrapLayoutState.hasEdgePadding) {
            padding(
                top = wrapLayoutState.mainAxisStartPadding,
                bottom = wrapLayoutState.mainAxisEndPadding,
                start = wrapLayoutState.crossAxisStartPadding,
                end = wrapLayoutState.crossAxisEndPadding,
            )
        }
        .drawVerticalWrapSeparators(separatorGeometry, wrapLayoutState, isRtl)

    FlowColumn(
        modifier = containerModifier,
        verticalArrangement = verticalAlignment
            .toVerticalArrangement(wrapLayoutState.effectiveItemSpacing),
        horizontalArrangement = horizontalAlignment
            .toHorizontalArrangement(wrapLayoutState.effectiveLineSpacing),
        overflow = FlowColumnOverflow.Visible,
    ) {
        val defaultHorizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment()
        visibleItems.forEachIndexed { index, item ->
            DivBlockView(
                data = item,
                modifier = Modifier
                    .align(item.observeHorizontalChildAlignment() ?: defaultHorizontalAlignment)
                    .trackChildPlacement(separatorGeometry, index),
            )
        }
    }
}

private class WrapLayoutState(
    val itemSeparatorDrawInfo: SeparatorDrawInfo?,
    val lineSeparatorDrawInfo: SeparatorDrawInfo?,
    val itemSeparatorVisibility: SeparatorVisibility,
    val lineSeparatorVisibility: SeparatorVisibility,
    val effectiveItemSpacing: Dp,
    val effectiveLineSpacing: Dp,
    val mainAxisStartPadding: Dp,
    val mainAxisEndPadding: Dp,
    val crossAxisStartPadding: Dp,
    val crossAxisEndPadding: Dp,
) {
    val hasSeparatorsToDraw =
        (itemSeparatorDrawInfo != null && itemSeparatorVisibility.hasAnySeparator) ||
            (lineSeparatorDrawInfo != null && lineSeparatorVisibility.hasAnySeparator)

    val hasEdgePadding =
        mainAxisStartPadding != 0.dp ||
            mainAxisEndPadding != 0.dp ||
            crossAxisStartPadding != 0.dp ||
            crossAxisEndPadding != 0.dp
}

@Composable
private fun resolveWrapLayoutState(data: DivContainer, isHorizontal: Boolean): WrapLayoutState {
    val itemSeparatorVisibility = data.separator.resolveSeparatorVisibility()
    val lineSeparatorVisibility = data.lineSeparator.resolveSeparatorVisibility()
    val itemSeparatorDrawInfo = if (itemSeparatorVisibility.hasAnySeparator) {
        data.separator?.resolveDrawInfo()
    } else {
        null
    }
    val lineSeparatorDrawInfo = if (lineSeparatorVisibility.hasAnySeparator) {
        data.lineSeparator?.resolveDrawInfo()
    } else {
        null
    }

    val effectiveItemSpacing = when {
        itemSeparatorVisibility.showBetween && itemSeparatorDrawInfo != null ->
            itemSeparatorDrawInfo.mainAxisTotalDp(isHorizontal)
        else -> data.itemSpacing.observedDpValue()
    }

    val effectiveLineSpacing = when {
        lineSeparatorVisibility.showBetween && lineSeparatorDrawInfo != null ->
            lineSeparatorDrawInfo.crossAxisTotalDp(isHorizontal)
        else -> data.lineSpacing.observedDpValue()
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
private fun rememberSeparatorGeometry(
    enabled: Boolean,
    childCount: Int,
    isHorizontal: Boolean,
): WrapSeparatorGeometry? {
    if (!enabled) return null
    val geometry = remember(isHorizontal) { WrapSeparatorGeometry(isHorizontal) }
    SideEffect {
        geometry.resize(childCount)
    }
    return geometry
}

private fun Modifier.trackChildPlacement(
    geometry: WrapSeparatorGeometry?,
    index: Int,
): Modifier {
    geometry ?: return this
    return onPlaced { layoutCoordinates ->
        val position = layoutCoordinates.positionInParent()
        geometry.updateChild(
            index = index,
            rect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + layoutCoordinates.size.width,
                bottom = position.y + layoutCoordinates.size.height,
            ),
        )
    }
}

private fun Modifier.drawHorizontalWrapSeparators(
    geometry: WrapSeparatorGeometry?,
    state: WrapLayoutState,
    isRtl: Boolean,
): Modifier {
    geometry ?: return this
    return drawWithCache {
        val itemSeparatorDrawInfo = state.itemSeparatorDrawInfo?.toPx(this)
        val lineSeparatorDrawInfo = state.lineSeparatorDrawInfo?.toPx(this)
        val mainAxisStartPadding = state.mainAxisStartPadding.toPx()
        val mainAxisEndPadding = state.mainAxisEndPadding.toPx()
        val crossAxisStartPadding = state.crossAxisStartPadding.toPx()
        val crossAxisEndPadding = state.crossAxisEndPadding.toPx()

        onDrawWithContent {
            drawContent()
            drawHorizontalWrapSeparators(
                geometry,
                itemSeparatorDrawInfo,
                state.itemSeparatorVisibility,
                lineSeparatorDrawInfo,
                state.lineSeparatorVisibility,
                mainAxisStartPadding,
                mainAxisEndPadding,
                crossAxisStartPadding,
                crossAxisEndPadding,
                isRtl = isRtl,
            )
        }
    }
}

private fun Modifier.drawVerticalWrapSeparators(
    geometry: WrapSeparatorGeometry?,
    state: WrapLayoutState,
    isRtl: Boolean,
): Modifier {
    geometry ?: return this
    return drawWithCache {
        val itemSeparatorDrawInfo = state.itemSeparatorDrawInfo?.toPx(this)
        val lineSeparatorDrawInfo = state.lineSeparatorDrawInfo?.toPx(this)
        val mainAxisStartPadding = state.mainAxisStartPadding.toPx()
        val mainAxisEndPadding = state.mainAxisEndPadding.toPx()
        val crossAxisStartPadding = state.crossAxisStartPadding.toPx()
        val crossAxisEndPadding = state.crossAxisEndPadding.toPx()

        onDrawWithContent {
            drawContent()
            drawVerticalWrapSeparators(
                geometry,
                itemSeparatorDrawInfo,
                state.itemSeparatorVisibility,
                lineSeparatorDrawInfo,
                state.lineSeparatorVisibility,
                mainAxisStartPadding,
                mainAxisEndPadding,
                crossAxisStartPadding,
                crossAxisEndPadding,
                isRtl = isRtl,
            )
        }
    }
}
