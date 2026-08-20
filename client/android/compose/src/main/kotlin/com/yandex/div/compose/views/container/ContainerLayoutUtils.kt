package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivContainer
import com.yandex.div2.DivVisibility

@Composable
internal fun DivContainer.Separator?.resolveSeparatorVisibility(): SeparatorVisibility {
    val separator = this ?: return SeparatorVisibility.None
    val showAtStart = separator.showAtStart.observedValue()
    val showBetween = separator.showBetween.observedValue()
    val showAtEnd = separator.showAtEnd.observedValue()
    if (!showAtStart && !showBetween && !showAtEnd) {
        return SeparatorVisibility.None
    }
    return SeparatorVisibility(
        showAtStart = showAtStart,
        showBetween = showBetween,
        showAtEnd = showAtEnd,
    )
}

internal data class SeparatorVisibility(
    val showAtStart: Boolean,
    val showBetween: Boolean,
    val showAtEnd: Boolean,
) {
    val hasAnySeparator: Boolean
        get() = showAtStart || showBetween || showAtEnd

    companion object {
        val None = SeparatorVisibility(
            showAtStart = false,
            showBetween = false,
            showAtEnd = false,
        )
    }
}

internal fun SeparatorVisibility.effectiveItemSpacing(itemSpacing: Long): Long =
    if (hasAnySeparator) 0L else itemSpacing

@Composable
internal fun DivContainer.visibleItems(): List<Div> {
    return items
        ?.filter { it.value().visibility.observedValue() != DivVisibility.GONE }
        ?: emptyList()
}

@Composable
internal fun RowScope.observeVerticalChildModifier(
    item: Div,
    defaultAlignment: Alignment.Vertical? = null,
    alignByBaseline: Boolean = false,
): Modifier {
    val childAlignment = item.value().alignmentVertical?.observedValue()
    return when {
        childAlignment == DivAlignmentVertical.BASELINE -> Modifier.alignByBaseline()
        childAlignment != null -> Modifier.align(childAlignment.toVerticalAlignment())
        alignByBaseline -> Modifier.alignByBaseline()
        defaultAlignment != null -> Modifier.align(defaultAlignment)
        else -> Modifier
    }
}

@Composable
internal fun Div.observeHorizontalChildAlignment(): Alignment.Horizontal? =
    value().alignmentHorizontal?.observedValue()?.toHorizontalAlignment()

internal fun Modifier.reduceMaxConstraint(
    reductionAmount: Dp,
    isWidth: Boolean,
): Modifier {
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
    val hasItems = items.isNotEmpty()

    if (hasItems && separatorVisibility.showAtStart && separator != null) {
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

    if (hasItems && separatorVisibility.showAtEnd && separator != null) {
        renderSeparator(separator)
    }
}
