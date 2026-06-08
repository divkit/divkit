package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div2.DivSize
import com.yandex.div2.DivTabs
import kotlinx.coroutines.launch

@Composable
internal fun DivTabsView(
    modifier: Modifier,
    data: DivTabs
) {
    val items = data.items
    if (items.isEmpty()) {
        Box(modifier = modifier)
        return
    }

    val resolver = expressionResolver
    val state = rememberDivTabsState(
        initialIndex = remember(data.selectedTab, resolver) {
            data.selectedTab.evaluate(resolver).toInt().coerceIn(0, (items.size - 1).coerceAtLeast(0))
        },
        initialTabCount = items.size,
    )

    val externalSelected = data.selectedTab.observedIntValue().coerceIn(0, items.size - 1)
    LaunchedEffect(externalSelected) {
        if (state.currentIndex != externalSelected) {
            state.selectTab(externalSelected)
        }
    }

    val scope = rememberCoroutineScope()
    val style = data.tabTitleStyle ?: DEFAULT_TAB_TITLE_STYLE
    val fixedWidth = (data.width as? DivSize.Fixed)?.value?.observedValue()
    val fixedHeight = (data.height as? DivSize.Fixed)?.value?.observedValue()
    val titlePaddings = data.titlePaddings.observeInsets()
    val contentPaddings = data.paddings.observeInsets()
    val minIntrinsicHeight = style.observeRowHeight() +
        titlePaddings.calculateTopPadding() +
        titlePaddings.calculateBottomPadding() +
        contentPaddings.calculateTopPadding() +
        contentPaddings.calculateBottomPadding()

    Column(
        modifier = Modifier
            .fixedIntrinsics(width = fixedWidth, height = fixedHeight ?: minIntrinsicHeight)
            .then(modifier)
            .padding(contentPaddings)
    ) {
        TabTitlesView(
            items = items,
            pagerState = state.pagerState,
            style = style,
            titleDelimiter = data.tabTitleDelimiter,
            titlePaddings = titlePaddings,
            onTabSelected = { index -> scope.launch { state.selectTab(index) } },
        )

        if (data.hasSeparator.observedValue()) {
            TabSeparator(
                color = data.separatorColor.observedColorValue(),
                paddings = data.separatorPaddings.observeInsets(),
            )
        }

        TabsContent(
            items = items,
            pagerState = state.pagerState,
            isDynamicHeight = data.dynamicHeight.observedValue(),
            isSwipeEnabled = data.switchTabsByContentSwipeEnabled.observedValue(),
        )
    }
}

@Composable
private fun TabSeparator(
    color: Color,
    paddings: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddings)
            .height(1.dp)
            .background(color)
    )
}

@Composable
private fun TabsContent(
    items: List<DivTabs.Item>,
    pagerState: PagerState,
    isDynamicHeight: Boolean,
    isSwipeEnabled: Boolean,
) {
    SubcomposeLayout { constraints ->
        val measureSlot = subcompose(TabsContentSlot.Measure) {
            items.forEach { DivBlockView(it.div) }
        }
        val measureWidth = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            Constraints.Infinity
        }
        val pagePlaceables = measureSlot.map { measurable ->
            val pageConstraints = if (constraints.hasBoundedHeight) {
                constraints
            } else {
                constraints.copy(maxHeight = measurable.maxIntrinsicHeight(measureWidth))
            }
            measurable.measure(pageConstraints)
        }

        val maxHeight = pagePlaceables.maxOf { it.height }
        val logicalPosition = (
            pagerState.currentPage.toFloat() + pagerState.currentPageOffsetFraction
        ).coerceIn(0f, (items.size - 1).toFloat())
        val desiredHeight = computeDesiredHeight(
            placeables = pagePlaceables,
            logicalPosition = logicalPosition,
            maxHeight = maxHeight,
            isDynamicHeight = isDynamicHeight,
        )

        val width = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            pagePlaceables.maxOf { it.width }
        }

        val pagerPlaceable = subcompose(TabsContentSlot.Pager) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = isSwipeEnabled,
                verticalAlignment = Alignment.Top,
            ) { page ->
                DivBlockView(items[page].div)
            }
        }.first().measure(Constraints.fixed(width, desiredHeight))

        layout(pagerPlaceable.width, pagerPlaceable.height) {
            pagerPlaceable.placeRelative(0, 0)
        }
    }
}

private fun computeDesiredHeight(
    placeables: List<androidx.compose.ui.layout.Placeable>,
    logicalPosition: Float,
    maxHeight: Int,
    isDynamicHeight: Boolean,
): Int {
    if (isDynamicHeight) {
        val intPos = logicalPosition.toInt().coerceIn(0, placeables.lastIndex)
        val frac = (logicalPosition - intPos).coerceIn(0f, 1f)
        val source = placeables[intPos].height
        val dest = placeables.getOrNull(intPos + 1)?.height ?: source
        return lerp(source, dest, frac)
    }
    return when {
        logicalPosition <= 0f -> placeables[0].height
        logicalPosition >= 1f -> maxHeight
        else -> lerp(placeables[0].height, maxHeight, logicalPosition)
    }
}

private enum class TabsContentSlot { Measure, Pager }

private val DEFAULT_TAB_TITLE_STYLE = DivTabs.TabTitleStyle()
