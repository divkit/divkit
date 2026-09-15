package com.yandex.div.compose.views.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.text.observeBaseTextStyle
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivInput

@Composable
internal fun DivInputView(modifier: Modifier, data: DivInput) {
    val textAlignmentHorizontal = data.textAlignmentHorizontal.observedValue()
    val textAlignmentVertical = data.textAlignmentVertical.observedValue()

    val textStyle = observeBaseTextStyle(
        fontSize = data.fontSize.observedIntValue(),
        fontSizeUnit = data.fontSizeUnit.observedValue(),
        textColor = data.textColor,
        fontWeight = data.fontWeight,
        fontWeightValue = data.fontWeightValue,
        fontFamily = data.fontFamily,
        letterSpacing = data.letterSpacing,
        lineHeight = data.lineHeight?.observedIntValue(),
        textAlignmentHorizontal = textAlignmentHorizontal,
        fontVariationSettings = data.fontVariationSettings
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
    val keyboardType = data.keyboardType.observedValue()
    val maxLength = data.maxLength
    // Masked inputs still need max_length support for the formatted text.
    if (data.mask == null && maxLength != null) {
        LengthLimitedInputView(
            modifier, data, contentAlignment, textStyle, textAlignmentHorizontal,
            keyboardType,
            maxLength.observedValue().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        )
        return
    }

    val state = data.rememberDivInputState()

    data.validators?.validate(state.text.text)

    val visualTransformation = state.rememberVisualTransformation(keyboardType)
    val rendersOnEmptyInput = remember(visualTransformation) {
        visualTransformation.filter(AnnotatedString("")).text.text.isNotEmpty()
    }

    InputFieldLayout(
        modifier = modifier,
        data = data,
        contentAlignment = contentAlignment,
        textStyle = textStyle,
        textAlignmentHorizontal = textAlignmentHorizontal,
        keyboardType = keyboardType,
        showHint = state.text.text.isEmpty() && !rendersOnEmptyInput,
    ) { singleLine, maxLines, enabled, options, decorator ->
        BasicTextField(
            value = state.text,
            onValueChange = state.onValueChange,
            textStyle = textStyle,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            keyboardOptions = options,
            decorationBox = { decorator.Decoration(it) },
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
