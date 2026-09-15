package com.yandex.div.compose.views.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivInput

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
    val options = keyboardOptions(
        keyboardType, data.enterKeyType.observedValue(), data.autocapitalization.observedValue()
    )
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

    Box(modifier = modifier, contentAlignment = contentAlignment) {
        content(singleLine, maxLines, enabled, options, decorator)
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
