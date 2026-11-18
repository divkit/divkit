package com.yandex.div.compose.views.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.platform.PlatformTextInputMethodRequest
import androidx.compose.ui.platform.PlatformTextInputSession
import android.view.KeyEvent as AndroidKeyEvent

@OptIn(ExperimentalComposeUiApi::class)
internal class EnterInputInterceptor(
    private val signedDecimal: Boolean,
    private val onMultilineEnter: (() -> Boolean)?,
) : PlatformTextInputInterceptor {

    override suspend fun interceptStartInputMethod(
        request: PlatformTextInputMethodRequest,
        nextHandler: PlatformTextInputSession,
    ): Nothing {
        nextHandler.startInputMethod { outAttributes ->
            val connection = request.createInputConnection(outAttributes)
            if (signedDecimal) {
                outAttributes.inputType = outAttributes.inputType or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
            if (onMultilineEnter == null) {
                connection
            } else {
                outAttributes.imeOptions = outAttributes.imeOptions or
                        EditorInfo.IME_FLAG_NO_ENTER_ACTION
                EnterInputConnection(connection, onMultilineEnter)
            }
        }
    }
}

/**
 * Performs [onEnter] in place of the newline the keyboard commits for the Enter key, when it
 * handles the key.
 */
private class EnterInputConnection(
    connection: InputConnection,
    private val onEnter: () -> Boolean,
) : InputConnectionWrapper(connection, false) {

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        return text == "\n" && onEnter() || super.commitText(text, newCursorPosition)
    }

    override fun sendKeyEvent(event: AndroidKeyEvent): Boolean {
        return KeyEvent(event).isEnterDown() && onEnter() || super.sendKeyEvent(event)
    }
}
