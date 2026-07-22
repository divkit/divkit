package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.pager.rememberAndStoreState
import com.yandex.div.compose.views.modifiers.constrainUnboundedMax
import com.yandex.div.compose.views.modifiers.onMeasureConstraints
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import com.yandex.div2.DivVisibility

@Composable
internal fun DivPagerView(
    modifier: Modifier,
    data: DivPager,
) {
    if (data.itemBuilder != null) {
        reportError("div-pager.item_builder not supported")
    }

    val items = data.items.orEmpty().filter {
        it.value().visibility.observedValue() != DivVisibility.GONE
    }

    if (items.isEmpty()) {
        Box(modifier = modifier)
        return
    }

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
    val defaultItem = data.defaultItem.observedIntValue().coerceIn(0, items.size - 1)
    val stateStorage = LocalDivViewContext.current.pagerStateStorage

    stateStorage.rememberAndStoreState(
        id = data.id,
        pageCount = items.size,
        listState = null,
        snapPosition = SnapPosition.Start,
        initialPage = defaultItem,
    )

    Box(
        modifier = modifier
            .constrainUnboundedMax(isWidth = isHorizontal)
            .onMeasureConstraints { constraints ->
                viewportSizePx.intValue = if (isHorizontal) constraints.maxWidth else constraints.maxHeight
                crossAxisBounded.value = if (isHorizontal) constraints.hasBoundedHeight else constraints.hasBoundedWidth
            }
    ) {
        if (viewportSizePx.intValue <= 0) return@Box

        val density = LocalDensity.current
        val viewportSize = with(density) { viewportSizePx.intValue.toDp() }

        PagerContent(
            id = data.id,
            items = items,
            isHorizontal = isHorizontal,
            itemSpacing = data.itemSpacing.observedValue(),
            paddings = data.paddings.observeInsets(),
            layoutMode = data.layoutMode,
            scrollAxisAlignment = data.scrollAxisAlignment.observedValue(),
            crossAxisAlignment = data.crossAxisAlignment.observedValue(),
            layoutDirection = LocalLayoutDirection.current,
            defaultItem = defaultItem,
            viewportSize = viewportSize,
            crossAxisBounded = crossAxisBounded.value,
            stateStorage = stateStorage
        )
    }
}
