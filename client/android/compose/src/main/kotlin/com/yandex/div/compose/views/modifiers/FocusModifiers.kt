package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.focusable
import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div.compose.views.inheritFocusVisibility
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivFocus

@Composable
internal fun Modifier.bindRootFocus(storage: FocusBindingStorage): Modifier {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val binding = remember { FocusBindingStorage.Binding() }
    DisposableEffect(storage, focusManager, keyboardController) {
        storage.register(binding, focusManager, keyboardController)
        onDispose { storage.unregister(binding) }
    }
    return Modifier
        .onFocusChanged { binding.hasFocus = it.hasFocus }
        .focusProperties { onEnter = { if (storage.isClearing) cancelFocusChange() } }
        .focusGroup()
        .then(this)
}

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

    // Inputs bind their text field as the focus target themselves.
    val targetId = data.value().id.takeUnless { data is Div.Input }
    val modifier = observeFocusChanges(data, state)
        .inheritFocusVisibility()
        .nextFocusIds(focus?.nextFocusIds)
        .let { if (targetId == null) it else it.bindFocusTarget(targetId) }
    return when {
        hasActions || data is Div.Select -> modifier.focusProperties { canFocus = focus != null }
        focus == null || data.isFocusable -> modifier
        else -> modifier.focusable()
    }
}

@Composable
private fun Modifier.nextFocusIds(ids: DivFocus.NextFocusIds?): Modifier {
    if (ids == null) return this

    val storage = LocalDivViewContext.current.component.focusBindingStorage
    val forward = ids.forward?.observedValue()
    val right = ids.right?.observedValue()
    val down = ids.down?.observedValue()
    val left = ids.left?.observedValue()
    val up = ids.up?.observedValue()
    return focusProperties {
        next = forward?.let(storage::getFocusRequester) ?: FocusRequester.Default
        this.right = right?.let(storage::getFocusRequester) ?: FocusRequester.Default
        this.down = down?.let(storage::getFocusRequester) ?: FocusRequester.Default
        this.left = left?.let(storage::getFocusRequester) ?: FocusRequester.Default
        this.up = up?.let(storage::getFocusRequester) ?: FocusRequester.Default
    }
}

@Composable
internal fun Modifier.bindFocusTarget(
    id: String?,
    requester: FocusRequester = remember { FocusRequester() },
): Modifier {
    if (id == null) return this.focusRequester(requester)
    val storage = LocalDivViewContext.current.component.focusBindingStorage
    DisposableEffect(storage, id, requester) {
        storage.register(id, requester)
        onDispose { storage.unregister(id) }
    }
    return this.focusRequester(requester)
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
