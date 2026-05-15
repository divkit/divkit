package com.yandex.div.compose.views.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div.compose.utils.observeBaseTextStyle
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivInput

@Composable
internal fun DivInputView(modifier: Modifier, data: DivInput) {
    val textAlignmentHorizontal = data.textAlignmentHorizontal.observedValue()
    val textAlignmentVertical = data.textAlignmentVertical.observedValue()

    val textStyle = observeBaseTextStyle(
        fontSize = data.fontSize.observedIntValue(),
        fontSizeUnit = data.fontSizeUnit,
        textColor = data.textColor,
        fontWeight = data.fontWeight,
        fontWeightValue = data.fontWeightValue,
        fontFamily = data.fontFamily,
        letterSpacing = data.letterSpacing,
        lineHeight = data.lineHeight,
        textAlignmentHorizontal = textAlignmentHorizontal,
    )
    val contentAlignment = toAlignment(textAlignmentHorizontal, textAlignmentVertical)

    InputView(modifier, data, contentAlignment, textStyle, textAlignmentHorizontal)
}

@Composable
private fun InputView(
    modifier: Modifier,
    data: DivInput,
    contentAlignment: Alignment,
    textStyle: TextStyle,
    textAlignmentHorizontal: DivAlignmentHorizontal,
) {
    val state = data.rememberDivInputState()

    data.validators?.validate(state.text.text)

    val keyboardType = data.keyboardType.observedValue()
    val singleLine = keyboardType != DivInput.KeyboardType.MULTI_LINE_TEXT
    val maxLines = if (singleLine) 1 else {
        data.maxVisibleLines?.observedIntValue()?.coerceAtLeast(1) ?: Int.MAX_VALUE
    }

    val hintText = data.hintText?.observedValue()
    val hintColor = data.hintColor.observedColorValue()

    val visualTransformation = state.rememberVisualTransformation(keyboardType)
    val rendersOnEmptyInput = remember(visualTransformation) {
        visualTransformation.filter(AnnotatedString("")).text.text.isNotEmpty()
    }

    Box(modifier = modifier, contentAlignment = contentAlignment) {
        BasicTextField(
            value = state.text,
            onValueChange = state.onValueChange,
            textStyle = textStyle,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = data.isEnabled.observedValue(),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions(
                keyboardType,
                data.enterKeyType.observedValue(),
                data.autocapitalization.observedValue()
            ),
            decorationBox = { innerTextField ->
                DecorationBox(
                    innerTextField = innerTextField,
                    textAlignmentHorizontal = textAlignmentHorizontal,
                    showHint = state.text.text.isEmpty() && !rendersOnEmptyInput,
                    hintText = hintText,
                    hintColor = hintColor,
                    textStyle = textStyle,
                    maxLines = maxLines,
                )
            }
        )
    }
}

@Composable
private fun DivInputState.rememberVisualTransformation(
    keyboardType: DivInput.KeyboardType
): VisualTransformation {
    val passwordTransformation = remember { PasswordVisualTransformation() }
    return if (keyboardType == DivInput.KeyboardType.PASSWORD) {
        passwordTransformation
    } else {
        visualTransformation
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
