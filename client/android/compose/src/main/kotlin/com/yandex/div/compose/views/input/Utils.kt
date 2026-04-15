package com.yandex.div.compose.views.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivInput

internal fun DivAlignmentHorizontal.toTextAlignment(): Alignment {
    return when (this) {
        DivAlignmentHorizontal.CENTER -> Alignment.TopCenter
        DivAlignmentHorizontal.RIGHT, DivAlignmentHorizontal.END -> Alignment.TopEnd
        DivAlignmentHorizontal.LEFT, DivAlignmentHorizontal.START -> Alignment.TopStart
    }
}

internal fun DivInput.KeyboardType.toVisualTransformation(): VisualTransformation {
    return when (this) {
        DivInput.KeyboardType.PASSWORD -> PasswordVisualTransformation()
        DivInput.KeyboardType.SINGLE_LINE_TEXT,
        DivInput.KeyboardType.MULTI_LINE_TEXT,
        DivInput.KeyboardType.URI,
        DivInput.KeyboardType.NUMBER,
        DivInput.KeyboardType.EMAIL,
        DivInput.KeyboardType.PHONE -> VisualTransformation.None
    }
}

internal fun keyboardOptions(
    keyboardType: DivInput.KeyboardType,
    enterKeyType: DivInput.EnterKeyType,
    autocapitalization: DivInput.Autocapitalization,
): KeyboardOptions {
    val type = when (keyboardType) {
        DivInput.KeyboardType.NUMBER -> KeyboardType.Number
        DivInput.KeyboardType.EMAIL -> KeyboardType.Email
        DivInput.KeyboardType.URI -> KeyboardType.Uri
        DivInput.KeyboardType.PHONE -> KeyboardType.Phone
        DivInput.KeyboardType.PASSWORD -> KeyboardType.Password
        DivInput.KeyboardType.MULTI_LINE_TEXT, DivInput.KeyboardType.SINGLE_LINE_TEXT -> KeyboardType.Text
    }

    val action = when (enterKeyType) {
        DivInput.EnterKeyType.SEND -> ImeAction.Send
        DivInput.EnterKeyType.DONE -> ImeAction.Done
        DivInput.EnterKeyType.SEARCH -> ImeAction.Search
        DivInput.EnterKeyType.GO -> ImeAction.Go
        DivInput.EnterKeyType.DEFAULT -> ImeAction.Default
    }

    val capitalization = when (autocapitalization) {
        DivInput.Autocapitalization.SENTENCES -> KeyboardCapitalization.Sentences
        DivInput.Autocapitalization.WORDS -> KeyboardCapitalization.Words
        DivInput.Autocapitalization.ALL_CHARACTERS -> KeyboardCapitalization.Characters
        DivInput.Autocapitalization.AUTO,
        DivInput.Autocapitalization.NONE -> KeyboardCapitalization.None
    }

    return KeyboardOptions(
        keyboardType = type,
        imeAction = action,
        capitalization = capitalization,
    )
}
