package com.yandex.div.core.util.hotreload

internal sealed interface HotReloadStatus {
    data object Reloading : HotReloadStatus
    data object Applied : HotReloadStatus
    data object Skipped : HotReloadStatus

    class Failure(val error: Throwable) : HotReloadStatus
}
