package com.yandex.div.compose.views.input

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.views.input.mask.InputMask
import com.yandex.div2.DivInput

@Composable
internal fun DivInput.rememberInputConfig(mask: InputMask?): InputConfig {
    val maxLength = maxLength?.observedIntValue()
    // The View renderer applies the filters to unmasked inputs only.
    val filter = if (mask == null) filters?.observeFilter() else null
    return remember(mask, maxLength, filter) { InputConfig(mask, maxLength, filter) }
}

/** The restrictions of the text an input holds. */
internal class InputConfig(
    val mask: InputMask?,
    private val maxLength: Int?,
    private val filter: ((String) -> Boolean)?,
) {
    /** The restrictions applied to the edits of the user. */
    val inputTransformation: InputTransformation? = listOfNotNull(
        mask?.let(::MaskInputTransformation),
        if (mask == null) maxLength?.let(::MaxLengthInputTransformation) else null,
        filter?.let(::FilterInputTransformation),
    ).reduceOrNull(InputTransformation::then)

    /** Whether the field shows the placeholders of the mask while it is empty, in place of the hint. */
    val showsPlaceholders: Boolean = mask != null && mask.format("").isNotEmpty()

    fun accepts(text: String): Boolean = filter?.invoke(text) ?: true

    /** The text the field stores for the [value] of its variable. */
    fun storedText(value: String): String {
        if (mask != null) return mask.sanitize(value)
        return if (maxLength == null) value else value.limitLength(maxLength)
    }
}

/** Drops the characters [mask] cannot hold. */
@OptIn(ExperimentalFoundationApi::class)
private class MaskInputTransformation(private val mask: InputMask) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (changes.changeCount == 0) return
        val typed = changes.getRange(0)
        if (changes.changeCount == 1 && typed.length == 1 && changes.getOriginalRange(0).length == 0) {
            val char = charAt(typed.min)
            val stored = mask.typedChar(char)
            if (stored != char) replace(typed.min, typed.max, stored.toString())
        }
        replaceChanged(mask.sanitizeEdit(toString()))
    }
}

/** Replaces the part of the text that differs from [text], keeping the cursor of an edit in place. */
private fun TextFieldBuffer.replaceChanged(text: String) {
    val current = toString()
    if (current == text) return
    val prefix = current.commonPrefixWith(text).length
    val suffix = current.commonSuffixWith(text).length.coerceAtMost(minOf(current.length, text.length) - prefix)
    replace(prefix, current.length - suffix, text.substring(prefix, text.length - suffix))
}

private class FilterInputTransformation(private val filter: (String) -> Boolean) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().contentEquals(originalText) && !filter(toString())) {
            revertAllChanges()
        }
    }
}
