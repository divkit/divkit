package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.pager.rememberAndStoreState
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.scroll.IntrinsicSizeBarrier
import com.yandex.div.compose.variables.mutableStateFromIntegerVariable
import com.yandex.div.compose.views.modifiers.constrainUnboundedMax
import com.yandex.div.compose.views.modifiers.onMeasureConstraints
import com.yandex.div.compose.views.transition.slideTransitionParent
import com.yandex.div2.Div
import com.yandex.div2.DivPager

@Composable
internal fun DivPagerView(
    modifier: Modifier,
    data: DivPager,
) {
    val collectionItems = data.observeItems()
    val collection = collectionItems.observeVisible()
    val items = collection.items

    data.itemCountVariable?.let { variableName ->
        val countState = mutableStateFromIntegerVariable(variableName)
        SideEffect {
            val count = items.size.toLong()
            if (countState != null && countState.value != count) {
                countState.value = count
            }
        }
    }

    if (items.isEmpty()) {
        LocalDivViewContext.current.pagerStateStorage.rememberAndStoreState(
            pager = data,
            pageCount = 0,
            listState = null,
            snapPosition = SnapPosition.Start,
            initialPage = 0,
            infiniteScroll = false,
        )
        BindPagerScrollActions(id = data.id, items = items)
        Box(modifier = modifier)
        return
    }

    PagerView(
        modifier = modifier.slideTransitionParent(),
        data = data,
        collection = collection,
        allItems = collectionItems.items,
    )
}

@Composable
private fun PagerView(
    modifier: Modifier,
    data: DivPager,
    collection: VisibleCollectionItems,
    allItems: List<Div>,
) {
    val items = collection.items

    val viewportSizePx = remember { mutableIntStateOf(0) }
    val crossAxisBounded = remember { mutableStateOf(true) }

    val isHorizontal = data.orientation.observedValue() == DivPager.Orientation.HORIZONTAL
    val defaultItem = data.defaultItem.observedIntValue().coerceIn(0, items.size - 1)
    val infiniteScroll = data.infiniteScroll.observedValue()
    val stateStorage = LocalDivViewContext.current.pagerStateStorage

    stateStorage.rememberAndStoreState(
        pager = data,
        pageCount = items.size,
        listState = null,
        snapPosition = SnapPosition.Start,
        initialPage = defaultItem,
        infiniteScroll = infiniteScroll,
    )

    Box(
        modifier = modifier
            .then(IntrinsicSizeBarrier)
            .constrainUnboundedMax(isWidth = isHorizontal)
            .onMeasureConstraints { constraints ->
                viewportSizePx.intValue = if (isHorizontal) {
                    constraints.maxWidth
                } else {
                    constraints.maxHeight
                }
                crossAxisBounded.value = if (isHorizontal) {
                    constraints.hasBoundedHeight
                } else {
                    constraints.hasBoundedWidth
                }
            }
    ) {
        if (viewportSizePx.intValue <= 0) return@Box

        val density = LocalDensity.current
        val viewportSize = with(density) { viewportSizePx.intValue.toDp() }

        PagerContent(
            pager = data,
            collection = collection,
            allItems = allItems,
            isHorizontal = isHorizontal,
            itemSpacing = data.itemSpacing.observedValue(),
            paddings = data.paddings.observeInsets(),
            layoutMode = data.layoutMode,
            scrollAxisAlignment = data.scrollAxisAlignment.observedValue(),
            crossAxisAlignment = data.crossAxisAlignment.observedValue(),
            layoutDirection = LocalLayoutDirection.current,
            defaultItem = defaultItem,
            infiniteScroll = infiniteScroll,
            pageTransformation = data.pageTransformation,
            viewportSize = viewportSize,
            crossAxisBounded = crossAxisBounded.value,
            stateStorage = stateStorage
        )
    }
}
