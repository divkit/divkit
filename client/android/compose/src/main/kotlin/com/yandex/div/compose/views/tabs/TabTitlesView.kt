package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivTabs
import kotlinx.coroutines.flow.first
import kotlin.collections.first
import kotlin.math.abs

@Composable
internal fun TabTitlesView(
    items: List<DivTabs.Item>,
    pagerState: PagerState,
    style: DivTabs.TabTitleStyle,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    titlePaddings: PaddingValues,
    onTabSelected: (Int) -> Unit,
) {
    val itemSpacing = style.itemSpacing.observedIntValue().dp

    val modifier = Modifier
        .padding(titlePaddings)
        .requiredHeight(style.observeRowHeight())

    BoxWithConstraints {
        if (constraints.hasBoundedWidth) {
            ScrollableTitleRow(
                items = items,
                pagerState = pagerState,
                style = style,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                modifier = modifier,
                onTabSelected = onTabSelected,
            )
        } else {
            FixedTitleRow(
                items = items,
                pagerState = pagerState,
                style = style,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                modifier = modifier,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun FixedTitleRow(
    items: List<DivTabs.Item>,
    pagerState: PagerState,
    style: DivTabs.TabTitleStyle,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    itemSpacing: Dp,
    modifier: Modifier,
    onTabSelected: (Int) -> Unit,
) {
    val activeBackground = style.activeBackgroundColor.observedColorValue()
    val inactiveBackground = style.inactiveBackgroundColor?.observedColorValue() ?: Color.Transparent
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = pagerState.currentPage == index
            TabTitleItem(
                index = index,
                item = item,
                isSelected = isSelected,
                background = if (isSelected) activeBackground else inactiveBackground,
                style = style,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                onClick = { onTabSelected(index) },
            )
        }
    }
}

@Composable
private fun ScrollableTitleRow(
    items: List<DivTabs.Item>,
    pagerState: PagerState,
    style: DivTabs.TabTitleStyle,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    itemSpacing: Dp,
    modifier: Modifier,
    onTabSelected: (Int) -> Unit,
) {
    val selectedIndex = pagerState.currentPage
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

    CenterTitleRow(listState, selectedIndex)

    val activeBackground = style.activeBackgroundColor.observedColorValue()
    val inactiveBackground = style.inactiveBackgroundColor?.observedColorValue() ?: Color.Transparent
    val tabShape = style.observeTabShape()
    val titleStartOffsetPx = titleDelimiter.observeTitleStartOffsetPx(itemSpacing)
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val rowModifier = modifier
        .clipToBounds()
        .drawBehind {
            drawInactiveTabBackgrounds(
                listState = listState,
                inactiveBackground = inactiveBackground,
                titleStartOffsetPx = titleStartOffsetPx,
                tabShape = tabShape,
                layoutDirection = layoutDirection,
                density = density,
            )
            drawActiveTabIndicator(
                listState = listState,
                pagerState = pagerState,
                activeBackground = activeBackground,
                titleStartOffsetPx = titleStartOffsetPx,
                tabShape = tabShape,
                layoutDirection = layoutDirection,
                density = density,
            )
        }
    LazyRow(
        state = listState,
        modifier = rowModifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(count = items.size) { index ->
            TabTitleItem(
                index = index,
                item = items[index],
                isSelected = index == selectedIndex,
                background = Color.Transparent,
                style = style,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                onClick = { onTabSelected(index) },
            )
        }
    }
}

@Composable
private fun CenterTitleRow(listState: LazyListState, selectedIndex: Int) {
    LaunchedEffect(selectedIndex) {
        val info = snapshotFlow { listState.layoutInfo }
            .first { it.visibleItemsInfo.isNotEmpty() }
        val viewport = info.viewportEndOffset - info.viewportStartOffset
        val itemSize = info.visibleItemsInfo.firstOrNull { it.index == selectedIndex }?.size
            ?: info.visibleItemsInfo.first().size

        val centeringOffset = (viewport - itemSize) / 2
        listState.animateScrollToItem(selectedIndex, scrollOffset = -centeringOffset)
    }
}

private fun DrawScope.drawInactiveTabBackgrounds(
    listState: LazyListState,
    inactiveBackground: Color,
    titleStartOffsetPx: Int,
    tabShape: Shape,
    layoutDirection: LayoutDirection,
    density: Density,
) {
    if (inactiveBackground == Color.Transparent) return
    listState.layoutInfo.visibleItemsInfo.forEach { item ->
        val titleSize = item.titleSize(titleStartOffsetPx)
        val outline = tabShape.createOutline(
            Size(titleSize.toFloat(), size.height),
            layoutDirection,
            density,
        )
        translate(left = item.titleOffset(titleStartOffsetPx).toFloat(), top = 0f) {
            drawOutline(outline, color = inactiveBackground)
        }
    }
}

private fun DrawScope.drawActiveTabIndicator(
    listState: LazyListState,
    pagerState: PagerState,
    activeBackground: Color,
    titleStartOffsetPx: Int,
    tabShape: Shape,
    layoutDirection: LayoutDirection,
    density: Density,
) {
    val visibleItems = listState.layoutInfo.visibleItemsInfo
    val page = pagerState.currentPage
    val current = visibleItems.firstOrNull { it.index == page } ?: return
    val fraction = pagerState.currentPageOffsetFraction
    val adjacent = when {
        fraction > 0f -> visibleItems.firstOrNull { it.index == page + 1 }
        fraction < 0f -> visibleItems.firstOrNull { it.index == page - 1 }
        else -> null
    }
    val absFraction = abs(fraction)
    val currentOffset = current.titleOffset(titleStartOffsetPx)
    val currentSize = current.titleSize(titleStartOffsetPx)
    val offsetPx = lerp(
        currentOffset,
        adjacent?.titleOffset(titleStartOffsetPx) ?: currentOffset,
        absFraction,
    )
    val sizePx = lerp(
        currentSize,
        adjacent?.titleSize(titleStartOffsetPx) ?: currentSize,
        absFraction,
    )

    val indicatorOutline = tabShape.createOutline(
        Size(sizePx.toFloat(), size.height),
        layoutDirection,
        density,
    )
    translate(left = offsetPx.toFloat(), top = 0f) {
        drawOutline(indicatorOutline, color = activeBackground)
    }
}

@Composable
private fun DivTabs.TabTitleDelimiter?.observeTitleStartOffsetPx(itemSpacing: Dp): Int {
    this ?: return 0
    val width = width.value.observedValue().toDp(width.unit.observedValue())
    return with(LocalDensity.current) { (width + itemSpacing).roundToPx() }
}

private fun LazyListItemInfo.titleOffset(titleStartOffsetPx: Int): Int =
    offset + titleStartOffset(titleStartOffsetPx)

private fun LazyListItemInfo.titleSize(titleStartOffsetPx: Int): Int =
    size - titleStartOffset(titleStartOffsetPx)

private fun LazyListItemInfo.titleStartOffset(titleStartOffsetPx: Int): Int =
    if (index == 0) 0 else titleStartOffsetPx.coerceIn(0, size)
