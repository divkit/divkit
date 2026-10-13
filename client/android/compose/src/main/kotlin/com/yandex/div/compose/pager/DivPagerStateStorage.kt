package com.yandex.div.compose.pager

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.internal.util.NearestItemSearch
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivPager
import javax.inject.Inject

@DivViewScope
internal class DivPagerStateStorage @Inject constructor() {

    private val states: SnapshotStateMap<DivPager, DivPagerState> = mutableStateMapOf()

    fun get(pager: DivPager): DivPagerState? = states[pager]

    fun put(pager: DivPager, state: DivPagerState) {
        states[pager] = state
    }

    fun retain(divs: Set<DivBase>) {
        states.keys.retainAll(divs)
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun DivPagerStateStorage.rememberAndStoreState(
    pager: DivPager,
    pageCount: Int,
    listState: LazyListState?,
    snapPosition: SnapPosition,
    initialPage: Int,
    infiniteScroll: Boolean,
) {
    val pagerState = remember(pager, pageCount, listState, snapPosition, initialPage, infiniteScroll) {
        DivPagerState(pageCount, listState, snapPosition, initialPage, infiniteScroll)
    }

    put(pager, pagerState)
}

internal fun findNearestPager(root: Div, indicator: DivIndicator): DivPager? {
    val search = NearestItemSearch<DivPager>()
    root.visitPagers(indicator, search)
    return search.nearestItems().firstOrNull()
}

private fun Div.visitPagers(indicator: DivIndicator, search: NearestItemSearch<DivPager>) {
    val children = when (this) {
        is Div.Container -> value.items.orEmpty()
        is Div.Custom -> value.items.orEmpty()
        is Div.Gallery -> value.items.orEmpty()
        is Div.Grid -> value.items.orEmpty()
        is Div.Pager -> value.items.orEmpty()
        is Div.State -> value.states.mapNotNull { it.div }
        is Div.Tabs -> value.items.map { it.div }
        is Div.GifImage, is Div.Image, is Div.Indicator, is Div.Input,
        is Div.Select, is Div.Separator, is Div.Slider, is Div.Switch,
        is Div.Text, is Div.Video -> null
    }

    if (children != null) search.onEnter()
    if (value() === indicator) search.onSeeker()
    if (this is Div.Pager && (indicator.pagerId == null || value.id == indicator.pagerId)) {
        search.onItem(value)
    }
    if (children != null) {
        children.forEach { it.visitPagers(indicator, search) }
        search.onLeave()
    }
}
