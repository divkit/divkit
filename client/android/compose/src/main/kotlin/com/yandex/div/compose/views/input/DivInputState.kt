package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
import com.yandex.div.compose.views.input.mask.rememberFixedLengthInputState
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask

@Composable
internal fun DivInput.rememberDivInputState(): DivInputState {
    return when (val mask = mask) {
        is DivInputMask.FixedLength -> mask.value.rememberFixedLengthInputState(textVariable)
        else -> rememberPlainInputState()
    }
}

internal class DivInputState(
    private val source: MutableState<String>,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    private val sanitize: (String) -> String = { it },
) {
    private var fieldValue by mutableStateOf(
        TextFieldValue(source.value, TextRange(source.value.length))
    )

    val text: TextFieldValue
        get() {
            val current = source.value
            if (fieldValue.text != current) {
                fieldValue = TextFieldValue(current, TextRange(current.length))
            }
            return fieldValue
        }

    val onValueChange: (TextFieldValue) -> Unit = { newValue ->
        val sanitized = sanitize(newValue.text)
        if (sanitized != source.value) source.value = sanitized
        fieldValue = if (source.value == newValue.text) {
            newValue
        } else {
            TextFieldValue(source.value, TextRange(source.value.length))
        }
    }
}

@Composable
private fun DivInput.rememberPlainInputState(): DivInputState {
    val source = mutableStateFromVariable(textVariable, defaultValue = "")
    val filtered = rememberFilteredState(source, filters)
    return remember(filtered) { DivInputState(filtered) }
}
