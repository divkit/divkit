package com.yandex.div.compose.pager

import androidx.compose.runtime.mutableStateMapOf
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
