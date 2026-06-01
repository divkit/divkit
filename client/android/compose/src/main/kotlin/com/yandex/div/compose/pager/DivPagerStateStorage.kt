package com.yandex.div.compose.pager

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

@DivViewScope
internal class DivPagerStateStorage @Inject constructor() {

    private val states: SnapshotStateMap<String, DivPagerState> = mutableStateMapOf()

    fun get(id: String): DivPagerState? = states[id]

    fun put(id: String, state: DivPagerState) {
        states[id] = state
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun DivPagerStateStorage.rememberAndStoreState(
    id: String?,
    pageCount: Int,
    listState: LazyListState?,
    snapPosition: SnapPosition,
    initialPage: Int,
) {
    if (id == null) return

    val pagerState = remember(id, pageCount, listState, snapPosition, initialPage) {
        DivPagerState(pageCount, listState, snapPosition, initialPage)
    }

    put(id, pagerState)
}
