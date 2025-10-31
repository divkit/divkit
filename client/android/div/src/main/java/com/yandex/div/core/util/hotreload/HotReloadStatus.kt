package com.yandex.div.core.util.hotreload

internal sealed interface HotReloadStatus {
    object Reloading : HotReloadStatus
    object Applied : HotReloadStatus
    object Skipped : HotReloadStatus

    class Failure(val error: Throwable) : HotReloadStatus
}
