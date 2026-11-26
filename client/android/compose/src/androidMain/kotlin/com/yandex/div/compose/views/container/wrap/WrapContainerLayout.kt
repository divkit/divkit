@file:Suppress("DEPRECATION")

package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowColumnOverflow
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.isRtlLayout
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.container.alignContainerOverflow
import com.yandex.div.compose.views.container.observeHorizontalChildAlignment
import com.yandex.div.compose.views.container.observeVerticalChildModifier
import com.yandex.div.compose.views.container.toCrossAxisHorizontalAlignment
import com.yandex.div.compose.views.container.toCrossAxisVerticalAlignment
import com.yandex.div.compose.views.container.toHorizontalArrangement
import com.yandex.div.compose.views.container.toVerticalArrangement
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

/**
 * A horizontal or vertical container that wraps its children into lines. The content alignment
 * arranges the items along a line and the lines across the container; inside a line an item starts
 * at the line's edge unless it has an alignment of its own.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ContainerWrapView(modifier: Modifier, data: DivContainer, isHorizontal: Boolean) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val itemSeparator = data.separator.observeWrapSeparator()
    val lineSeparator = data.lineSeparator.observeWrapSeparator()
    // A separator between the children takes the place of the spacing.
    val itemSpacing = itemSeparator?.betweenSize(alongWidth = isHorizontal) ?: data.itemSpacing.observedDpValue()
    val lineSpacing = lineSeparator?.betweenSize(alongWidth = !isHorizontal) ?: data.lineSpacing.observedDpValue()
    val items = data.observeItems()
    val (visibleItems, components) = items.observeVisible()
    val separatorGeometry = rememberSeparatorGeometry(
        enabled = (itemSeparator != null || lineSeparator != null) && visibleItems.isNotEmpty(),
        childCount = visibleItems.size,
        isHorizontal = isHorizontal,
    )
    val containerModifier = modifier
        .padding(data.paddings)
        .alignContainerOverflow(horizontalAlignment, verticalAlignment)
        .separatorEdgePadding(
            horizontal = if (isHorizontal) itemSeparator else lineSeparator,
            vertical = if (isHorizontal) lineSeparator else itemSeparator,
        )
        .drawWrapSeparators(separatorGeometry, isHorizontal, itemSeparator, lineSeparator, isRtlLayout)

    // Children keep being laid out outside a constrained cross axis. An item larger than its line
    // overflows it the way the line is aligned.
    if (isHorizontal) {
        FlowRow(
            modifier = containerModifier,
            horizontalArrangement = horizontalAlignment.toHorizontalArrangement(itemSpacing),
            verticalArrangement = verticalAlignment.toVerticalArrangement(lineSpacing),
            overflow = FlowRowOverflow.Visible,
        ) {
            val layoutParams = DivBlockLayoutParams(defaultHorizontalAlignment = horizontalAlignment.toLineAlignment())
            WrapItems(visibleItems, components, separatorGeometry, layoutParams) { observeVerticalChildModifier(it) }
        }
    } else {
        FlowColumn(
            modifier = containerModifier,
            verticalArrangement = verticalAlignment.toVerticalArrangement(itemSpacing),
            horizontalArrangement = horizontalAlignment.toHorizontalArrangement(lineSpacing),
            overflow = FlowColumnOverflow.Visible,
        ) {
            val layoutParams = DivBlockLayoutParams(defaultVerticalAlignment = verticalAlignment.toLineAlignment())
            WrapItems(visibleItems, components, separatorGeometry, layoutParams) { observeLineAlignment(it) }
        }
    }
}

@Composable
private inline fun WrapItems(
    items: List<Div>,
    components: Map<Div, DivLocalComponent>?,
    separatorGeometry: WrapSeparatorGeometry?,
    layoutParams: DivBlockLayoutParams,
    crossinline alignment: @Composable (Div) -> Modifier,
) {
    items.forEachIndexed { index, item ->
        WithDivKey(item) {
            WithLocalComponent(components?.get(item)) {
                DivBlockView(
                    data = item,
                    modifier = alignment(item).trackChildPlacement(separatorGeometry, index),
                    layoutParams = layoutParams,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.observeLineAlignment(item: Div): Modifier {
    val alignment = item.observeHorizontalChildAlignment() ?: return Modifier
    return Modifier.align(alignment)
}

// Space-around and space-evenly center a line that has no free space; space-between starts it.
private fun DivContentAlignmentHorizontal.toLineAlignment(): Alignment.Horizontal = when (this) {
    DivContentAlignmentHorizontal.SPACE_AROUND,
    DivContentAlignmentHorizontal.SPACE_EVENLY -> Alignment.CenterHorizontally
    else -> toCrossAxisHorizontalAlignment()
}

private fun DivContentAlignmentVertical.toLineAlignment(): Alignment.Vertical = when (this) {
    DivContentAlignmentVertical.SPACE_AROUND,
    DivContentAlignmentVertical.SPACE_EVENLY -> Alignment.CenterVertically
    else -> toCrossAxisVerticalAlignment()
}

/** Separators at the edges take space around the lines. */
private fun Modifier.separatorEdgePadding(horizontal: SeparatorDrawInfo?, vertical: SeparatorDrawInfo?): Modifier {
    if (horizontal == null && vertical == null) return this
    return padding(
        start = horizontal?.edgeSize(atStart = true, alongWidth = true) ?: 0.dp,
        end = horizontal?.edgeSize(atStart = false, alongWidth = true) ?: 0.dp,
        top = vertical?.edgeSize(atStart = true, alongWidth = false) ?: 0.dp,
        bottom = vertical?.edgeSize(atStart = false, alongWidth = false) ?: 0.dp,
    )
}

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

private fun Modifier.drawWrapSeparators(
    geometry: WrapSeparatorGeometry?,
    isHorizontal: Boolean,
    itemSeparator: SeparatorDrawInfo?,
    lineSeparator: SeparatorDrawInfo?,
    isRtl: Boolean,
): Modifier {
    geometry ?: return this
    return drawWithCache {
        val itemSeparatorPx = itemSeparator?.toPx(this)
        val lineSeparatorPx = lineSeparator?.toPx(this)
        onDrawWithContent {
            drawContent()
            drawWrapSeparators(geometry, isHorizontal, itemSeparatorPx, lineSeparatorPx, isRtl)
        }
    }
}
