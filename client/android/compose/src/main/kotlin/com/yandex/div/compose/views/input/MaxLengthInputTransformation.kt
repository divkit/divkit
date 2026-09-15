package com.yandex.div.compose.views.input

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.delete
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.maxTextLength

@OptIn(ExperimentalFoundationApi::class)
internal class MaxLengthInputTransformation(private val maxLength: Int) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        for (index in changes.changeCount - 1 downTo 0) {
            val range = changes.getRange(index)
            val available = (maxLength - (length - range.length)).coerceAtLeast(0)
            if (range.length <= available) continue

            var end = range.min + available
            if (end > range.min && charAt(end - 1).isHighSurrogate()) end--
            delete(end, range.max)
        }
    }

    override fun SemanticsPropertyReceiver.applySemantics() {
        maxTextLength = maxLength
    }
}

internal fun String.limitLength(maxLength: Int): String {
    if (length <= maxLength) return this
    val end = if (maxLength > 0 && this[maxLength - 1].isHighSurrogate()) maxLength - 1 else maxLength
    return substring(0, end)
}
