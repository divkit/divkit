package com.yandex.div.compose.host

internal interface CheckVisibilityCallback {
    fun checkVisibility()

    fun onContentUnbound()
}
