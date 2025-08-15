package com.yandex.div.core.state

import com.yandex.div.core.state.DivViewState.BlockState

internal data class GalleryState(
    val visibleItemIndex: Int,
    val scrollOffset: Int
) : BlockState
