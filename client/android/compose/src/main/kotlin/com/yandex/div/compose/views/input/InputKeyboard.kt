package com.yandex.div.compose.views.input

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.text.input.ImeAction
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.observedEnabledActions
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask

/** The keyboard of an input and what its Enter key does. */
@OptIn(ExperimentalComposeUiApi::class)
internal class InputKeyboard(
    val options: KeyboardOptions,
    val onKeyboardAction: KeyboardActionHandler?,
    val interceptor: PlatformTextInputInterceptor,
    /** Performs the action of the Enter key of a multi-line input; false when the key inserts a newline. */
    val onEnter: () -> Boolean,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun DivInput.rememberInputKeyboard(keyboardType: DivInput.KeyboardType): InputKeyboard {
    val enterKeyType = enterKeyType.observedValue()
    val autocapitalization = autocapitalization.observedValue()
    val enterKeyActions = enterKeyActions.observedEnabledActions()
    val hasDigitsMask = mask is DivInputMask.Currency || mask is DivInputMask.Phone
    val component = LocalComponent.current

    val options = remember(keyboardType, enterKeyType, autocapitalization) {
        keyboardOptions(keyboardType, enterKeyType, autocapitalization).copy(autoCorrectEnabled = false)
    }
    val onKeyboardAction = remember(enterKeyActions, options.imeAction) {
        when {
            enterKeyActions.isNotEmpty() -> KeyboardActionHandler {
                component.handleActions(enterKeyActions, DivActionSource.KEYBOARD)
            }
            options.imeAction == ImeAction.Default -> null
            // A specific Enter key without actions does nothing, as in the View renderer.
            else -> KeyboardActionHandler {}
        }
    }
    val multiline = keyboardType == DivInput.KeyboardType.MULTI_LINE_TEXT && !hasDigitsMask
    // Enter key actions replace the newline, as the schema defines them, even for the default Enter key.
    val consumeEnter = multiline && (options.imeAction != ImeAction.Default || enterKeyActions.isNotEmpty())
    val onEnter = remember(consumeEnter, onKeyboardAction) {
        {
            if (consumeEnter) onKeyboardAction?.onKeyboardAction {}
            consumeEnter
        }
    }
    // The interceptor restarts the keyboard session when it changes: it reads the current Enter handling instead.
    val currentOnEnter = rememberUpdatedState(onEnter)
    val interceptor = remember(keyboardType, hasDigitsMask) {
        inputKeyboardInterceptor(
            signedDecimal = keyboardType == DivInput.KeyboardType.NUMBER && !hasDigitsMask,
            multiline = multiline,
            onEnter = if (multiline) { { currentOnEnter.value() } } else null,
        )
    }
    return remember(options, onKeyboardAction, interceptor, onEnter) {
        InputKeyboard(options, onKeyboardAction, interceptor, onEnter)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun inputKeyboardInterceptor(
    signedDecimal: Boolean,
    multiline: Boolean,
    onEnter: (() -> Boolean)?,
): PlatformTextInputInterceptor {
    return PlatformTextInputInterceptor { request, nextHandler ->
        nextHandler.startInputMethod { outAttributes ->
            val connection = request.createInputConnection(outAttributes)
            if (signedDecimal) {
                outAttributes.inputType = outAttributes.inputType or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
            if (multiline) {
                outAttributes.imeOptions = outAttributes.imeOptions or EditorInfo.IME_FLAG_NO_ENTER_ACTION
            }
            if (onEnter == null) connection else EnterInputConnection(connection, onEnter)
        }
    }
}

/** Performs [onEnter] in place of the newline the keyboard commits for the Enter key, when it handles the key. */
private class EnterInputConnection(
    connection: InputConnection,
    private val onEnter: () -> Boolean,
) : InputConnectionWrapper(connection, false) {
    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        return text == "\n" && onEnter() || super.commitText(text, newCursorPosition)
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        return event.isEnterDown() && onEnter() || super.sendKeyEvent(event)
    }
}

internal fun KeyEvent.isEnterDown(): Boolean {
    return action == KeyEvent.ACTION_DOWN &&
        (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
}
