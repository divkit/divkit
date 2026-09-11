package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.context.animationsEnabled
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.mirrorHorizontallyIfRtl
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div2.DivTabs
import kotlinx.coroutines.flow.first
import kotlin.collections.first
import kotlin.math.roundToInt

@Composable
internal fun TabTitlesView(
    items: List<DivTabs.Item>,
    state: DivTabsState,
    style: DivTabs.TabTitleStyle,
    animationType: DivTabs.TabTitleStyle.AnimationType,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    titlePaddings: PaddingValues,
    onTabSelected: (Int) -> Unit,
) {
    val itemSpacing = style.itemSpacing.observedIntValue().dp
    val rowHeight = style.observeRowHeight()
    val intrinsicWidth = observeIntrinsicWidth(items, style, titleDelimiter, itemSpacing)

    val modifier = Modifier
        .padding(titlePaddings)
        .requiredHeight(rowHeight)

    BoxWithConstraints(modifier = Modifier.fixedIntrinsics(width = intrinsicWidth, height = rowHeight).then(modifier)) {
        if (constraints.hasBoundedWidth) {
            ScrollableTitleRow(
                items = items,
                state = state,
                style = style,
                animationType = animationType,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                modifier = Modifier,
                onTabSelected = onTabSelected,
            )
        } else {
            FixedTitleRow(
                items = items,
                state = state,
                style = style,
                animationType = animationType,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                modifier = Modifier,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun observeIntrinsicWidth(
    items: List<DivTabs.Item>,
    style: DivTabs.TabTitleStyle,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    itemSpacing: Dp,
): Dp {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textStyle = style.observeTextStyle(isSelected = false)
    val horizontalTabPaddings = (style.paddings.left.observedIntValue() + style.paddings.right.observedIntValue()).dp
    val delimiterWidth = titleDelimiter?.width?.observedValue() ?: 0.dp

    val textWidthPx = items.sumOf { item ->
        textMeasurer.measure(
            text = item.title.observedValue(),
            style = textStyle,
        ).size.width
    }
    val extraWidth = horizontalTabPaddings * items.size +
        itemSpacing * (items.size - 1).coerceAtLeast(0) +
        (delimiterWidth + itemSpacing) * (items.size - 1).coerceAtLeast(0)

    return with(density) { textWidthPx.toDp() + extraWidth }
}

@Composable
private fun FixedTitleRow(
    items: List<DivTabs.Item>,
    state: DivTabsState,
    style: DivTabs.TabTitleStyle,
    animationType: DivTabs.TabTitleStyle.AnimationType,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    itemSpacing: Dp,
    modifier: Modifier,
    onTabSelected: (Int) -> Unit,
) {
    val selectedIndex = state.selectedIndex
    val titleStartOffsetPx = titleDelimiter?.observeTitleStartOffsetPx(itemSpacing) ?: 0
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val itemBounds = remember(items) { mutableStateMapOf<Int, TitleBounds>() }
    val placedTitles = RowTitles(itemBounds, items.size, titleStartOffsetPx)

    Row(
        modifier = modifier.drawTitleBackgrounds(placedTitles, state, style, animationType),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            TabTitleItem(
                modifier = Modifier.onPlaced { coordinates ->
                    coordinates.boundsFromRowStart(isRtl)?.let { itemBounds[index] = it }
                },
                index = index,
                item = item,
                isSelected = index == selectedIndex,
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
    state: DivTabsState,
    style: DivTabs.TabTitleStyle,
    animationType: DivTabs.TabTitleStyle.AnimationType,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    itemSpacing: Dp,
    modifier: Modifier,
    onTabSelected: (Int) -> Unit,
) {
    val selectedIndex = state.selectedIndex
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    var activeTitleTaps by remember { mutableIntStateOf(0) }

    CenterTitleRow(listState, selectedIndex, activeTitleTaps)

    val titleStartOffsetPx = titleDelimiter?.observeTitleStartOffsetPx(itemSpacing) ?: 0
    val placedTitles = LazyRowTitles(listState, titleStartOffsetPx)

    LazyRow(
        state = listState,
        modifier = modifier.drawTitleBackgrounds(placedTitles, state, style, animationType),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(count = items.size) { index ->
            TabTitleItem(
                index = index,
                item = items[index],
                isSelected = index == selectedIndex,
                style = style,
                titleDelimiter = titleDelimiter,
                itemSpacing = itemSpacing,
                onClick = {
                    if (index == selectedIndex) {
                        activeTitleTaps++
                    }
                    onTabSelected(index)
                },
            )
        }
    }
}

@Composable
private fun CenterTitleRow(listState: LazyListState, selectedIndex: Int, activeTitleTaps: Int) {
    val animated = animationsEnabled
    LaunchedEffect(selectedIndex, activeTitleTaps) {
        val info = snapshotFlow { listState.layoutInfo }
            .first { it.visibleItemsInfo.isNotEmpty() }
        val viewport = info.viewportEndOffset - info.viewportStartOffset
        val itemSize = info.visibleItemsInfo.firstOrNull { it.index == selectedIndex }?.size
            ?: info.visibleItemsInfo.first().size

        val centeringOffset = (viewport - itemSize) / 2
        if (animated) {
            listState.animateScrollToItem(selectedIndex, scrollOffset = -centeringOffset)
        } else {
            listState.scrollToItem(selectedIndex, scrollOffset = -centeringOffset)
        }
    }
}

@Composable
private fun Modifier.drawTitleBackgrounds(
    placedTitles: PlacedTitles,
    state: DivTabsState,
    style: DivTabs.TabTitleStyle,
    animationType: DivTabs.TabTitleStyle.AnimationType,
): Modifier {
    val activeBackground = style.activeBackgroundColor.observedColorValue()
    val inactiveBackground = style.inactiveBackgroundColor?.observedColorValue() ?: Color.Transparent
    val tabShape = style.observeTabShape()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    return clipToBounds().drawBehind {
        mirrorHorizontallyIfRtl(layoutDirection == LayoutDirection.Rtl) {
            if (inactiveBackground != Color.Transparent) {
                for (index in placedTitles.firstIndex..placedTitles.lastIndex) {
                    drawTitleBackground(placedTitles.boundsOf(index), inactiveBackground, tabShape, layoutDirection, density)
                }
            }
            drawActiveTitleIndicator(
                animationType = animationType,
                color = activeBackground,
                density = density,
                layoutDirection = layoutDirection,
                placedTitles = placedTitles,
                shape = tabShape,
                state = state,
            )
        }
    }
}

private fun DrawScope.drawActiveTitleIndicator(
    animationType: DivTabs.TabTitleStyle.AnimationType,
    color: Color,
    density: Density,
    layoutDirection: LayoutDirection,
    placedTitles: PlacedTitles,
    shape: Shape,
    state: DivTabsState,
) {
    when (animationType) {
        DivTabs.TabTitleStyle.AnimationType.NONE -> {
            drawTitleBackground(placedTitles.boundsOf(state.selectedIndex), color, shape, layoutDirection, density)
        }

        DivTabs.TabTitleStyle.AnimationType.SLIDE -> {
            val from = placedTitles.boundsOf(state.indicatorFromIndex)
            if (!from.isPlaced) return
            val to = placedTitles.boundsOf(state.indicatorToIndex)
            val target = if (to.isPlaced) to else from
            val progress = state.indicatorProgress
            drawTitleBackground(
                start = lerp(from.start, target.start, progress),
                end = lerp(from.end, target.end, progress),
                color = color,
                shape = shape,
                layoutDirection = layoutDirection,
                density = density,
            )
        }

        DivTabs.TabTitleStyle.AnimationType.FADE -> {
            val progress = state.indicatorProgress
            val from = placedTitles.boundsOf(state.indicatorFromIndex)
            drawTitleBackground(from, color, shape, layoutDirection, density, alpha = 1f - progress)
            if (progress > 0f) {
                val to = placedTitles.boundsOf(state.indicatorToIndex)
                drawTitleBackground(to, color, shape, layoutDirection, density, alpha = progress)
            }
        }
    }
}

private fun DrawScope.drawTitleBackground(
    bounds: TitleBounds,
    color: Color,
    shape: Shape,
    layoutDirection: LayoutDirection,
    density: Density,
    alpha: Float = 1f,
) {
    if (!bounds.isPlaced) return
    drawTitleBackground(bounds.start, bounds.end, color, shape, layoutDirection, density, alpha)
}

private fun DrawScope.drawTitleBackground(
    start: Int,
    end: Int,
    color: Color,
    shape: Shape,
    layoutDirection: LayoutDirection,
    density: Density,
    alpha: Float = 1f,
) {
    val outline = shape.createOutline(Size((end - start).toFloat(), size.height), layoutDirection, density)
    translate(left = start.toFloat()) {
        drawOutline(outline, color = color, alpha = alpha)
    }
}

private interface PlacedTitles {
    val firstIndex: Int
    val lastIndex: Int

    fun boundsOf(index: Int): TitleBounds
}

private class LazyRowTitles(
    private val listState: LazyListState,
    private val titleStartOffsetPx: Int,
) : PlacedTitles {
    override val firstIndex: Int
        get() = listState.layoutInfo.visibleItemsInfo.let { if (it.isEmpty()) 0 else it.first().index }

    override val lastIndex: Int
        get() = listState.layoutInfo.visibleItemsInfo.let { if (it.isEmpty()) -1 else it.last().index }

    override fun boundsOf(index: Int): TitleBounds {
        val item = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return TitleBounds.NotPlaced
        return TitleBounds(
            start = item.offset + titleStartOffset(index, titleStartOffsetPx, item.size),
            end = item.offset + item.size,
        )
    }
}

private class RowTitles(
    private val itemBounds: SnapshotStateMap<Int, TitleBounds>,
    private val count: Int,
    private val titleStartOffsetPx: Int,
) : PlacedTitles {
    override val firstIndex: Int = 0
    override val lastIndex: Int = count - 1

    override fun boundsOf(index: Int): TitleBounds {
        val item = itemBounds[index] ?: return TitleBounds.NotPlaced
        return TitleBounds(
            start = item.start + titleStartOffset(index, titleStartOffsetPx, item.end - item.start),
            end = item.end,
        )
    }
}

@JvmInline
private value class TitleBounds(private val packed: Long) {
    constructor(start: Int, end: Int) : this((start.toLong() shl 32) or (end.toLong() and 0xFFFFFFFFL))

    val start: Int
        get() = (packed shr 32).toInt()

    val end: Int
        get() = packed.toInt()

    val isPlaced: Boolean
        get() = this != NotPlaced

    companion object {
        val NotPlaced = TitleBounds(Long.MIN_VALUE)
    }
}

private fun LayoutCoordinates.boundsFromRowStart(isRtl: Boolean): TitleBounds? {
    val rowWidth = parentLayoutCoordinates?.size?.width ?: return null
    val left = positionInParent().x.roundToInt()
    val width = size.width
    val start = if (isRtl) rowWidth - left - width else left
    return TitleBounds(start = start, end = start + width)
}

@Composable
private fun DivTabs.TabTitleDelimiter.observeTitleStartOffsetPx(itemSpacing: Dp): Int {
    val width = width.observedValue()
    return with(LocalDensity.current) { (width + itemSpacing).roundToPx() }
}

private fun titleStartOffset(index: Int, titleStartOffsetPx: Int, itemSize: Int): Int =
    if (index == 0) 0 else titleStartOffsetPx.coerceIn(0, itemSize)
