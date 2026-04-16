package com.yandex.div.compose.views.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import com.yandex.div2.DivVisibility

@Composable
internal fun DivPagerView(
    modifier: Modifier,
    data: DivPager,
) {
    val items = data.items.orEmpty().filter {
        it.value().visibility.observedValue() != DivVisibility.GONE
    }

    if (items.isEmpty()) {
        Box(modifier = modifier)
        return
    }

    // TODO: register pager scroll state

    PagerView(
        modifier = modifier,
        items = items,
        data = data
    )
}

@Composable
private fun PagerView(
    modifier: Modifier,
    items: List<Div>,
    data: DivPager,
) {
    val viewportSizePx = remember { mutableIntStateOf(0) }
    val crossAxisBounded = remember { mutableStateOf(true) }

    val isHorizontal = data.orientation.observedValue() == DivPager.Orientation.HORIZONTAL

    Box(modifier.captureConstraints(isHorizontal, viewportSizePx, crossAxisBounded)) {
        if (viewportSizePx.intValue <= 0) return@Box

        val density = LocalDensity.current
        val viewportSize = with(density) { viewportSizePx.intValue.toDp() }

        PagerContent(
            items = items,
            isHorizontal = isHorizontal,
            itemSpacing = data.itemSpacing.observedValue(),
            paddings = data.paddings.observeInsets(),
            layoutMode = data.layoutMode,
            scrollAxisAlignment = data.scrollAxisAlignment.observedValue(),
            crossAxisAlignment = data.crossAxisAlignment.observedValue(),
            layoutDirection = LocalLayoutDirection.current,
            defaultItem = data.defaultItem.observedValue().toInt().coerceIn(0, items.size - 1),
            viewportSize = viewportSize,
            crossAxisBounded = crossAxisBounded.value,
        )
    }
}

private fun Modifier.captureConstraints(
    isHorizontal: Boolean,
    onViewportSize: MutableState<Int>,
    crossAxisBounded: MutableState<Boolean>,
) = layout { measurable, constraints ->
    onViewportSize.value = if (isHorizontal) constraints.maxWidth else constraints.maxHeight
    crossAxisBounded.value = if (isHorizontal) constraints.hasBoundedHeight else constraints.hasBoundedWidth
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
}
