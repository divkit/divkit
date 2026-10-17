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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.text.input.ImeAction
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask

/** The keyboard of an input and what its Enter key does. */
@OptIn(ExperimentalComposeUiApi::class)
internal class InputKeyboard(
    val options: KeyboardOptions,
    val onKeyboardAction: KeyboardActionHandler?,
    val interceptor: PlatformTextInputInterceptor,
    /** Whether the Enter key of a multi-line input is consumed instead of inserting a newline. */
    val consumeEnter: Boolean,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun DivInput.rememberInputKeyboard(keyboardType: DivInput.KeyboardType): InputKeyboard {
    val enterKeyType = enterKeyType.observedValue()
    val autocapitalization = autocapitalization.observedValue()
    val hasDigitsMask = mask is DivInputMask.Currency || mask is DivInputMask.Phone

    val options = remember(keyboardType, enterKeyType, autocapitalization) {
        keyboardOptions(keyboardType, enterKeyType, autocapitalization).copy(autoCorrectEnabled = false)
    }
    val onKeyboardAction = remember(options.imeAction) {
        // A specific Enter key does nothing, as in the View renderer.
        if (options.imeAction == ImeAction.Default) null else KeyboardActionHandler {}
    }
    val multiline = keyboardType == DivInput.KeyboardType.MULTI_LINE_TEXT && !hasDigitsMask
    val consumeEnter = multiline && options.imeAction != ImeAction.Default
    val interceptor = remember(keyboardType, hasDigitsMask, consumeEnter) {
        inputKeyboardInterceptor(
            signedDecimal = keyboardType == DivInput.KeyboardType.NUMBER && !hasDigitsMask,
            multiline = multiline,
            consumeEnter = consumeEnter,
        )
    }
    return remember(options, onKeyboardAction, interceptor, consumeEnter) {
        InputKeyboard(options, onKeyboardAction, interceptor, consumeEnter)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun inputKeyboardInterceptor(
    signedDecimal: Boolean,
    multiline: Boolean,
    consumeEnter: Boolean,
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
            if (consumeEnter) EnterInputConnection(connection) else connection
        }
    }
}

private class EnterInputConnection(connection: InputConnection) : InputConnectionWrapper(connection, false) {
    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        return text == "\n" || super.commitText(text, newCursorPosition)
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        return event.isEnterDown() || super.sendKeyEvent(event)
    }
}

internal fun KeyEvent.isEnterDown(): Boolean {
    return action == KeyEvent.ACTION_DOWN &&
        (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
}
