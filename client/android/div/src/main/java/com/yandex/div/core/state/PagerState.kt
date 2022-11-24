package com.yandex.div.core.state

import com.yandex.div.core.state.DivViewState.BlockState

internal data class PagerState(
    val currentPageIndex: Int
) : BlockState
