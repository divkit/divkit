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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.context.animationsEnabled
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
            data.selectedTab.evaluate(resolver).toInt()
                .coerceIn(0, (items.size - 1).coerceAtLeast(0))
        },
        tabCount = items.size,
    )

    val style = data.tabTitleStyle ?: DEFAULT_TAB_TITLE_STYLE
    val animationType = style.animationType.observedValue()
    val titleAnimation = if (animationsEnabled) {
        TabTitleAnimation(type = animationType, durationMillis = style.animationDuration.observedIntValue())
    } else {
        null
    }
    val externalSelected = data.selectedTab.observedIntValue().coerceIn(0, items.size - 1)
    LaunchedEffect(externalSelected) {
        if (state.currentIndex != externalSelected) {
            state.selectTab(externalSelected, titleAnimation)
        }
    }

    val scope = rememberCoroutineScope()
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
            state = state,
            style = style,
            animationType = animationType,
            titleDelimiter = data.tabTitleDelimiter,
            titlePaddings = titlePaddings,
            onTabSelected = { index ->
                scope.launch { state.selectTab(index, titleAnimation) }
            },
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
    val pageContents = remember(items) {
        arrayOfNulls<@Composable () -> Unit>(items.size)
    }
    SubcomposeLayout { constraints ->
        fun subcomposePage(index: Int): List<Measurable> {
            val content = pageContents[index] ?: run {
                val newContent: @Composable () -> Unit = { DivBlockView(items[index].div) }
                pageContents[index] = newContent
                newContent
            }
            return subcompose(slotId = index, content = content)
        }

        val measuredPages = arrayOfNulls<IntSize>(items.size)
        fun measurePage(index: Int): IntSize {
            measuredPages[index]?.let { return it }
            val measurable = subcomposePage(index).firstOrNull()
            val size = if (measurable == null) {
                IntSize.Zero
            } else {
                val pageConstraints = if (constraints.hasBoundedHeight) {
                    constraints
                } else {
                    constraints.copy(maxHeight = measurable.maxIntrinsicHeight(constraints.maxWidth))
                }
                measurable.measure(pageConstraints).let { IntSize(it.width, it.height) }
            }
            measuredPages[index] = size
            return size
        }

        val desiredHeight = computeDesiredHeight(
            isDynamicHeight = isDynamicHeight,
            logicalPosition = pagerState.logicalPosition,
            pageCount = items.size,
            pageHeight = { measurePage(it).height },
        )

        val width = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            // With unbounded width every page can affect the pager's size.
            items.indices.maxOf { measurePage(it).width }
        }

        // Keep previously requested compositions active without measuring unused pages.
        pageContents.indices.forEach { index ->
            if (pageContents[index] != null && measuredPages[index] == null) {
                subcomposePage(index)
            }
        }

        val pagerPlaceable = subcompose(TabsPagerSlot) {
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

private inline fun computeDesiredHeight(
    isDynamicHeight: Boolean,
    logicalPosition: Float,
    pageCount: Int,
    pageHeight: (Int) -> Int,
): Int {
    return if (isDynamicHeight) {
        val sourceIndex = logicalPosition.toInt()
        val fraction = logicalPosition - sourceIndex
        val sourceHeight = pageHeight(sourceIndex)
        if (fraction == 0f) {
            sourceHeight
        } else {
            lerp(sourceHeight, pageHeight(sourceIndex + 1), fraction)
        }
    } else if (logicalPosition == 0f) {
        // The first tab uses only its own height. Other pages need not be composed yet.
        pageHeight(0)
    } else {
        val maxHeight = (0 until pageCount).maxOf { pageHeight(it) }
        if (logicalPosition >= 1f) {
            maxHeight
        } else {
            lerp(pageHeight(0), maxHeight, logicalPosition)
        }
    }
}

private data object TabsPagerSlot

private val DEFAULT_TAB_TITLE_STYLE = DivTabs.TabTitleStyle()
