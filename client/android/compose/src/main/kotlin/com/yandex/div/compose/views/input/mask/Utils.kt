package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

internal fun buildMaskedText(
    raw: String,
    pattern: String,
    isDynamic: (Char) -> Boolean,
    placeholderForPosition: ((Int) -> Char?)? = null,
): TransformedText {
    val out = StringBuilder()
    val rawToFormatted = IntArray(raw.length + 1)
    val formattedToRaw = ArrayList<Int>(pattern.length + raw.length + 1)
    formattedToRaw.add(0)

    var rawIdx = 0
    var patIdx = 0
    rawToFormatted[0] = 0

    while (patIdx < pattern.length) {
        val patChar = pattern[patIdx]
        if (!isDynamic(patChar)) {
            out.append(patChar)
            formattedToRaw.add(rawIdx)
            patIdx++
        } else if (rawIdx < raw.length) {
            out.append(raw[rawIdx])
            rawIdx++
            patIdx++
            rawToFormatted[rawIdx] = out.length
            formattedToRaw.add(rawIdx)
        } else {
            val placeholder = placeholderForPosition?.invoke(patIdx)
            if (placeholder != null) {
                out.append(placeholder)
                formattedToRaw.add(rawIdx)
                patIdx++
            } else {
                break
            }
        }
    }

    while (rawIdx < raw.length) {
        out.append(raw[rawIdx])
        rawIdx++
        rawToFormatted[rawIdx] = out.length
        formattedToRaw.add(rawIdx)
    }

    val formatted = out.toString()
    return TransformedText(
        text = AnnotatedString(formatted),
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                rawToFormatted[offset.coerceIn(0, raw.length)]

            override fun transformedToOriginal(offset: Int): Int =
                formattedToRaw[offset.coerceIn(0, formatted.length)]
        }
    )
}

@Composable
internal fun SyncMaskedRaw(
    raw: MutableState<String>,
    display: MutableState<String>,
    sanitize: (String) -> String,
    format: (String) -> String,
) {
    LaunchedEffect(raw.value, sanitize, format) {
        val sanitized = sanitize(raw.value)
        if (raw.value != sanitized) {
            raw.value = sanitized
        } else {
            val formatted = format(raw.value)
            if (display.value != formatted) display.value = formatted
        }
    }
}
