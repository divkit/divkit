package com.yandex.div.compose.views.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder

internal fun AnnotatedString.placeholders(
    inlineContent: Map<String, InlineTextContent>,
): List<AnnotatedString.Range<Placeholder>> {
    return getStringAnnotations(0, length)
        .mapNotNull { annotation ->
            val inlineTextContent = inlineContent[annotation.item] ?: return@mapNotNull null
            AnnotatedString.Range(
                item = inlineTextContent.placeholder,
                start = annotation.start,
                end = annotation.end,
            )
        }
}
