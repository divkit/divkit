package com.yandex.div.compose.tooltips

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.yandex.div.compose.utils.DivPopup
import com.yandex.div.internal.tooltip.TouchEventDispatcher

@Composable
internal fun PlatformTooltipPopup(
    positionProvider: PopupPositionProvider,
    isModal: Boolean,
    closeByTapOutside: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val underlyingView = LocalView.current.rootView
    val touchInput = remember(underlyingView, isModal) {
        if (isModal) null else androidTooltipInput(TouchEventDispatcher { underlyingView }::dispatch)
    }

    DivPopup(
        properties = PopupProperties(
            focusable = isModal,
            dismissOnBackPress = true,
            dismissOnClickOutside = closeByTapOutside || isModal,
            clippingEnabled = false,
        ),
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
    ) {
        CompositionLocalProvider(
            LocalTooltipInput provides touchInput,
            content = content,
        )
    }
}
