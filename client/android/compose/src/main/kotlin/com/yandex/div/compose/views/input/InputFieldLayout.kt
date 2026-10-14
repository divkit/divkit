package com.yandex.div.compose.views.input

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.focus.trackInputFocus
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun InputFieldLayout(
    modifier: Modifier,
    data: DivInput,
    contentAlignment: Alignment,
    textStyle: TextStyle,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    keyboardType: DivInput.KeyboardType,
    showHint: Boolean,
    content: @Composable (
        singleLine: Boolean,
        maxLines: Int,
        enabled: Boolean,
        keyboardOptions: KeyboardOptions,
        onKeyboardAction: (() -> Unit)?,
        decorator: TextFieldDecorator,
    ) -> Unit,
) {
    val singleLine = keyboardType != DivInput.KeyboardType.MULTI_LINE_TEXT
    val maxLines = if (singleLine) 1 else {
        data.maxVisibleLines?.observedIntValue()?.coerceAtLeast(1) ?: Int.MAX_VALUE
    }
    val enabled = data.isEnabled.observedValue()
    val enterKeyType = data.enterKeyType.observedValue()
    val autocapitalization = data.autocapitalization.observedValue()
    val options = remember(keyboardType, enterKeyType, autocapitalization) {
        keyboardOptions(keyboardType, enterKeyType, autocapitalization).copy(autoCorrectEnabled = false)
    }
    val onKeyboardAction = remember(options.imeAction) {
        if (options.imeAction == ImeAction.Default) {
            null
        } else {
            {}
        }
    }
    val mask = data.mask
    val hasDigitsMask = mask is DivInputMask.Currency || mask is DivInputMask.Phone
    val isMultilineKeyboard = !singleLine && !hasDigitsMask
    val consumeEnter = isMultilineKeyboard && options.imeAction != ImeAction.Default
    val keyboardInterceptor = remember(keyboardType, mask, enterKeyType) {
        inputKeyboardInterceptor(
            signedDecimal = keyboardType == DivInput.KeyboardType.NUMBER && !hasDigitsMask,
            multiline = isMultilineKeyboard,
            consumeEnter = consumeEnter,
        )
    }
    val hintText = data.hintText?.observedValue()
    val hintColor = data.hintColor.observedColorValue()
    val decorator = TextFieldDecorator { innerTextField ->
        DecorationBox(
            innerTextField = innerTextField,
            textAlignmentHorizontal = textAlignmentHorizontal,
            showHint = showHint,
            hintText = hintText,
            hintColor = hintColor,
            textStyle = textStyle,
            maxLines = maxLines,
        )
    }

    Box(
        modifier = modifier
            .trackInputFocus()
            .onPreviewKeyEvent { consumeEnter && it.nativeKeyEvent.isEnterDown() },
        contentAlignment = contentAlignment,
    ) {
        InterceptPlatformTextInput(keyboardInterceptor) {
            content(singleLine, maxLines, enabled, options, onKeyboardAction, decorator)
        }
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

private fun KeyEvent.isEnterDown(): Boolean {
    return action == KeyEvent.ACTION_DOWN &&
        (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
}

@Composable
private fun DecorationBox(
    innerTextField: @Composable () -> Unit,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    showHint: Boolean,
    hintText: String?,
    hintColor: Color,
    textStyle: TextStyle,
    maxLines: Int,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = toAlignment(textAlignmentHorizontal, DivAlignmentVertical.TOP),
    ) {
        if (showHint && hintText != null) {
            BasicText(
                text = hintText,
                style = textStyle.copy(color = hintColor),
                maxLines = maxLines,
            )
        }
        innerTextField()
    }
}
