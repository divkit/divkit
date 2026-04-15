package com.yandex.div.compose.views.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.observeBaseTextStyle
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivInput

@Composable
internal fun DivInputView(
    modifier: Modifier,
    data: DivInput,
) {
    // TODO: Make bind between Compose state and div variables.
    val binding = remember { mutableStateOf("") }

    val fontSize = data.fontSize.observedIntValue()
    val textAlignmentHorizontal = data.textAlignmentHorizontal.observedValue()
    val textAlignmentVertical = data.textAlignmentVertical.observedValue()
    val keyboardType = data.keyboardType.observedValue()
    val enterKeyType = data.enterKeyType.observedValue()
    val autocapitalization = data.autocapitalization.observedValue()
    val isEnabled = data.isEnabled.observedValue()

    val singleLine = keyboardType != DivInput.KeyboardType.MULTI_LINE_TEXT
    val maxLines = if (singleLine) 1 else {
        data.maxVisibleLines?.observedIntValue()?.coerceAtLeast(1) ?: Int.MAX_VALUE
    }

    val textStyle = observeBaseTextStyle(
        fontSize = fontSize,
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

    val hintText = data.hintText?.observedValue()
    val hintColor = data.hintColor.observedColorValue()

    Box(modifier = modifier, contentAlignment = contentAlignment) {
        BasicTextField(
            value = binding.value,
            onValueChange = { binding.value = it },
            textStyle = textStyle,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = keyboardType.toVisualTransformation(),
            keyboardOptions = keyboardOptions(keyboardType, enterKeyType, autocapitalization),
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxWidth(), contentAlignment = textAlignmentHorizontal.toTextAlignment()) {
                    if (binding.value.isEmpty() && hintText != null) {
                        BasicText(
                            text = hintText,
                            style = textStyle.copy(color = hintColor),
                            maxLines = maxLines,
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
