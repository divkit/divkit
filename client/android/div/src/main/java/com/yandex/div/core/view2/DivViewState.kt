package com.yandex.div.core.view2

import com.yandex.div.core.player.DivVideoPlaybackState

internal sealed interface DivViewState

internal data class DivVideoViewState(
    val playbackState: DivVideoPlaybackState,
) : DivViewState
