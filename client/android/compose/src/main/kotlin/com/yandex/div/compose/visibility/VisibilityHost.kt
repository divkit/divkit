package com.yandex.div.compose.visibility

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect

/**
 * Supplies host visibility bounds and lifecycle notifications without platform-specific types.
 * On unbind, notify [Callback] listeners before content-unbound callbacks.
 */
internal interface VisibilityHost {
    fun getVisibleRectInWindow(): Rect?

    fun addCallback(callback: Callback)
    fun removeCallback(callback: Callback)

    fun addContentUnboundCallback(callback: () -> Unit)
    fun removeContentUnboundCallback(callback: () -> Unit)

    interface Callback {
        fun checkVisibility()
        fun onContentUnbound()
    }
}

internal val LocalVisibilityHost = staticCompositionLocalOf<VisibilityHost?> { null }
