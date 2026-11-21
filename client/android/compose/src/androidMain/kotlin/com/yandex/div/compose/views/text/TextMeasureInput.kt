package com.yandex.div.compose.views.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

@Immutable
internal data class TextMeasureInput(
    val text: AnnotatedText,
    val style: TextStyle,
    val inlineContent: Map<String, InlineTextContent>,
)
