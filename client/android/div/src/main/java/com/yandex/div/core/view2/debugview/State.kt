package com.yandex.div.core.view2.debugview

import com.yandex.div.core.util.hotreload.HotReloadStatus

internal data class State(
    val showDetails: Boolean = false,
    val hotReloadActive: Boolean = false,
    val hotReloadStatus: HotReloadStatus? = null,
    val errors: List<Throwable> = emptyList(),
    val warnings: List<Throwable> = emptyList(),
)
