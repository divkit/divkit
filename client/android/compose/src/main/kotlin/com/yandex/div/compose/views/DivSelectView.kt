package com.yandex.div.compose.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.yandex.div.compose.utils.observeBaseTextStyle
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivSelect

@Composable
internal fun DivSelectView(
    modifier: Modifier,
    data: DivSelect,
) {
    val valueState = mutableStateFromVariable(data.valueVariable, defaultValue = "")
    val options = data.options.observeOptions()

    val textStyle = observeBaseTextStyle(
        fontSize = data.fontSize.observedIntValue(),
        textAlignmentHorizontal = DivAlignmentHorizontal.START,
        fontSizeUnit = data.fontSizeUnit,
        textColor = data.textColor,
        fontWeight = data.fontWeight,
        fontWeightValue = data.fontWeightValue,
        fontFamily = data.fontFamily,
        letterSpacing = data.letterSpacing,
        lineHeight = data.lineHeight,
    )
    val displayText = options.firstOrNull { it.value == valueState.value }?.displayText

    val hintText = data.hintText?.observedValue()
    val hintColor = data.hintColor.observedColorValue()

    val showHint = displayText.isNullOrEmpty() && !hintText.isNullOrEmpty()
    val text = if (showHint) hintText.orEmpty() else displayText.orEmpty()
    val color = if (showHint) hintColor else textStyle.color

    SelectView(
        modifier = modifier,
        valueState = valueState,
        text = text,
        textStyle = textStyle.copy(color = color),
        options = options
    )
}

@Composable
private fun SelectView(
    modifier: Modifier,
    valueState: MutableState<String>,
    text: String,
    textStyle: TextStyle,
    options: List<Option>
) {
    var expanded by remember { mutableStateOf(false) }

    val modifier = modifier.clickable(indication = null, interactionSource = null) {
        expanded = true
    }

    Box(modifier) {
        BasicText(
            text = text,
            style = textStyle
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayText) },
                    onClick = {
                        valueState.value = option.value
                        expanded = false
                    }
                )
            }
        }
    }
}

private data class Option(val value: String, val displayText: String)

@Composable
private fun List<DivSelect.Option>.observeOptions(): List<Option> {
    return map {
        val optionValue = it.value.observedValue()
        val optionText = it.text?.observedValue() ?: optionValue
        Option(value = optionValue, displayText = optionText)
    }
}
