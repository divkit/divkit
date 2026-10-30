package com.yandex.div.compose.visibility

internal interface CheckVisibilityCallback {
    fun checkVisibility()
    fun onContentUnbound()
}
