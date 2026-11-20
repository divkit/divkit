package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

/**
 * A [Popup] that keeps the [LocalContext] of the declaring composition.
 *
 * [Popup] provides the context of its own window. When a host supplies
 * [com.yandex.div.compose.DivContext] through [LocalContext], div content composed in a plain
 * [Popup] cannot resolve it. Use this function for every popup that composes div content.
 */
@Composable
internal fun DivPopup(
    popupPositionProvider: PopupPositionProvider,
    onDismissRequest: (() -> Unit)? = null,
    properties: PopupProperties = PopupProperties(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    Popup(
        popupPositionProvider = popupPositionProvider,
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        CompositionLocalProvider(LocalContext provides context, content = content)
    }
}
