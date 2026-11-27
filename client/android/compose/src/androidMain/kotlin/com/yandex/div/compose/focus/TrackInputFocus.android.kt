package com.yandex.div.compose.focus

import androidx.compose.ui.Modifier
import com.yandex.div.compose.LocalDivContext

private val inputFocusModifier = Modifier.trackInputFocus { node ->
    LocalDivContext.currentValueOf(node).component.inputFocus
}

internal fun Modifier.trackInputFocus(): Modifier = then(inputFocusModifier)
