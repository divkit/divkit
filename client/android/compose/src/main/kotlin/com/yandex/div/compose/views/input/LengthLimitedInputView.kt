package com.yandex.div.compose.views.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivInput

@Composable
internal fun LengthLimitedInputView(
    modifier: Modifier,
    data: DivInput,
    contentAlignment: Alignment,
    textStyle: TextStyle,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    keyboardType: DivInput.KeyboardType,
    maxLength: Int,
) {
    val state = data.rememberLengthLimitedInputState(maxLength)
    val text = state.field.text.toString()
    data.validators?.validate(text)

    val transformation = state.rememberInputTransformation(maxLength)

    InputFieldLayout(
        modifier = modifier,
        data = data,
        contentAlignment = contentAlignment,
        textStyle = textStyle,
        textAlignmentHorizontal = textAlignmentHorizontal,
        keyboardType = keyboardType,
        showHint = text.isEmpty(),
    ) { singleLine, maxLines, enabled, options, decorator ->
        val isPassword = keyboardType == DivInput.KeyboardType.PASSWORD
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        val focusedValue = remember(isPassword) {
            if (isFocused) state.field.text.toString() to state.field.selection else null
        }
        LaunchedEffect(isPassword) {
            if (focusedValue != null && enabled) {
                focusRequester.requestFocus()
                if (state.field.text.contentEquals(focusedValue.first)) {
                    state.field.edit { selection = focusedValue.second }
                }
            }
        }
        val fieldModifier = Modifier.fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
        if (isPassword) {
            // TextFieldState requires a secure field in place of PasswordVisualTransformation.
            BasicSecureTextField(
                state = state.field,
                modifier = fieldModifier,
                enabled = enabled,
                inputTransformation = transformation,
                textStyle = textStyle,
                keyboardOptions = options,
                decorator = decorator,
                textObfuscationMode = TextObfuscationMode.Hidden,
            )
        } else {
            BasicTextField(
                state = state.field,
                modifier = fieldModifier,
                enabled = enabled,
                inputTransformation = transformation,
                textStyle = textStyle,
                keyboardOptions = options,
                lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else {
                    TextFieldLineLimits.MultiLine(maxHeightInLines = maxLines)
                },
                decorator = decorator,
            )
        }
    }
}
