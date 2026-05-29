package com.yandex.div.compose.views.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div2.DivIndicator

@Composable
internal fun DivIndicatorView(modifier: Modifier, data: DivIndicator) {
    val style = data.observeIndicatorStyle()
    val pagerState = data.pagerId?.let { LocalDivViewContext.current.pagerStateStorage.get(it) }
    val itemsCount = pagerState?.pageCount ?: 0

    Layout(
        content = {},
        modifier = modifier.drawBehind { drawIndicators(style, pagerState) },
    ) { _, constraints ->
        val (desiredWidth, desiredHeight) = style.desiredSize(itemsCount)
        layout(
            width = constraints.constrainWidth(desiredWidth),
            height = constraints.constrainHeight(desiredHeight),
        ) {}
    }
}

private fun IndicatorStyle.desiredSize(itemsCount: Int): Pair<Int, Int> {
    val height = maxOf(activeShape.outerHeight, inactiveShape.outerHeight, minimumShape.outerHeight)
    val width = when {
        isStretch -> 0f
        itemsCount > 0 -> spaceBetweenCenters * itemsCount + activeShape.outerWidth
        else -> activeShape.outerWidth
    }
    return width.toInt() to height.toInt()
}
