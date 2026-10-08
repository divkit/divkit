package com.yandex.div.compose.views.input

import android.text.InputType
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
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.text.TextStyle
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.focus.trackInputFocus
import com.yandex.div2.DivAlignmentHorizontal
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
    val mask = data.mask
    val keyboardInterceptor = remember(keyboardType, mask) { inputKeyboardInterceptor(keyboardType, mask) }
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

    Box(modifier = modifier.trackInputFocus(), contentAlignment = contentAlignment) {
        InterceptPlatformTextInput(keyboardInterceptor) {
            content(singleLine, maxLines, enabled, options, decorator)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun inputKeyboardInterceptor(
    keyboardType: DivInput.KeyboardType,
    mask: DivInputMask?,
): PlatformTextInputInterceptor {
    return PlatformTextInputInterceptor { request, nextHandler ->
        if (keyboardType != DivInput.KeyboardType.NUMBER ||
            mask is DivInputMask.Currency || mask is DivInputMask.Phone
        ) {
            nextHandler.startInputMethod(request)
        }
        nextHandler.startInputMethod { outAttributes ->
            request.createInputConnection(outAttributes).also {
                outAttributes.inputType = outAttributes.inputType or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
        }
    }
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
    Box(Modifier.fillMaxWidth(), contentAlignment = textAlignmentHorizontal.toTextAlignment()) {
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
