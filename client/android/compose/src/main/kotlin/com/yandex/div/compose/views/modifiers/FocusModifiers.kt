package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.inheritFocusVisibility
import com.yandex.div2.Div
import com.yandex.div2.DivBase

@Composable
internal fun rememberFocusState(data: DivBase): MutableState<Boolean>? {
    if (data.focus == null) return null
    return remember { mutableStateOf(false) }
}

@Composable
internal fun Modifier.focus(
    data: Div,
    state: MutableState<Boolean>?,
    hasActions: Boolean,
): Modifier {
    val focus = data.value().focus
    if (focus?.nextFocusIds != null) {
        reportError("focus.next_focus_ids not supported")
    }

    val modifier = observeFocusChanges(data, state).inheritFocusVisibility()
    return when {
        hasActions || data is Div.Select -> modifier.focusProperties { canFocus = focus != null }
        focus == null || data.isFocusable -> modifier
        else -> modifier.focusable()
    }
}

private val Div.isFocusable: Boolean
    get() = this is Div.Input || this is Div.Slider || this is Div.Switch

@Composable
private fun Modifier.observeFocusChanges(data: Div, state: MutableState<Boolean>?): Modifier {
    if (state == null) return this

    val focus = data.value().focus
    val component = LocalComponent.current
    val onFocusChanged: (Boolean) -> Unit = { isFocused ->
        if (state.value != isFocused) {
            state.value = isFocused
            val actions = if (isFocused) focus?.onFocus else focus?.onBlur
            actions?.let {
                component.handleActions(it, if (isFocused) DivActionSource.FOCUS else DivActionSource.BLUR)
            }
        }
    }
    val currentOnFocusChanged by rememberUpdatedState(onFocusChanged)
    DisposableEffect(state) {
        onDispose { currentOnFocusChanged(false) }
    }
    return this.onFocusChanged { onFocusChanged(it.isFocused) }
}
