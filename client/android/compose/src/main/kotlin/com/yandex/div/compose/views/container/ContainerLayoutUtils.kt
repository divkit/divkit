package com.yandex.div.compose.views.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivSize
import com.yandex.div2.DivVisibility

@Composable
internal fun DivContainer.Separator?.resolveSeparatorVisibility() = SeparatorVisibility(
    showAtStart = this?.showAtStart?.observedValue() == true,
    showBetween = this?.showBetween?.observedValue() == true,
    showAtEnd = this?.showAtEnd?.observedValue() == true,
)

internal data class SeparatorVisibility(
    val showAtStart: Boolean,
    val showBetween: Boolean,
    val showAtEnd: Boolean,
) {
    val hasAnySeparator: Boolean
        get() = showAtStart || showBetween || showAtEnd
}

internal fun SeparatorVisibility.effectiveItemSpacing(itemSpacing: Long): Long =
    if (hasAnySeparator) 0L else itemSpacing

@Composable
internal fun DivContainer.visibleItems(): List<Div> =
    items.orEmpty().filter { it.value().visibility.observedValue() != DivVisibility.GONE }

@Composable
internal fun Div.observeVerticalChildAlignment(): Alignment.Vertical? =
    value().alignmentVertical?.observedValue()?.toVerticalAlignment()

@Composable
internal fun Div.observeHorizontalChildAlignment(): Alignment.Horizontal? =
    value().alignmentHorizontal?.observedValue()?.toHorizontalAlignment()

@Composable
internal fun resolveWeightedChildrenMargins(
    items: List<Div>,
    childMainSize: (DivBase) -> DivSize,
    childMainAxisMargins: @Composable (DivBase) -> Dp,
): Dp {
    var total = 0.dp
    for (item in items) {
        val childBase = item.value()
        if (childMainSize(childBase).isMatchParent) {
            total += childMainAxisMargins(childBase)
        }
    }
    return total
}

internal fun Modifier.reduceMaxConstraint(reductionAmount: Dp, isWidth: Boolean): Modifier {
    if (reductionAmount <= 0.dp) return this
    return layout { measurable, constraints ->
        val reductionPx = reductionAmount.roundToPx()
        val adjustedConstraints = if (isWidth) {
            constraints.copy(maxWidth = (constraints.maxWidth - reductionPx).coerceAtLeast(0))
        } else {
            constraints.copy(maxHeight = (constraints.maxHeight - reductionPx).coerceAtLeast(0))
        }
        val placeable = measurable.measure(adjustedConstraints)
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }
}

@Composable
internal inline fun LinearContainer(
    items: List<Div>,
    separator: DivContainer.Separator?,
    separatorVisibility: SeparatorVisibility,
    renderSeparator: @Composable (DivContainer.Separator) -> Unit,
    renderSpacing: @Composable () -> Unit,
    renderItem: @Composable (Div) -> Unit,
) {
    if (separatorVisibility.showAtStart && separator != null) {
        renderSeparator(separator)
    }

    items.forEachIndexed { index, childDiv ->
        renderItem(childDiv)

        val isNotLastItem = index < items.lastIndex
        if (isNotLastItem) {
            when {
                separatorVisibility.showBetween && separator != null -> renderSeparator(separator)
                separatorVisibility.hasAnySeparator -> renderSpacing()
            }
        }
    }

    if (separatorVisibility.showAtEnd && separator != null) {
        renderSeparator(separator)
    }
}
