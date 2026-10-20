package com.yandex.div.compose.views.input

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.delete
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
        mask?.let { MaskInputTransformation(it, maxLength) },
        if (mask == null) maxLength?.let(::MaxLengthInputTransformation) else null,
        filter?.let(::FilterInputTransformation),
    ).reduceOrNull(InputTransformation::then)

    /** Whether the field shows the placeholders of the mask while it is empty, in place of the hint. */
    val showsPlaceholders: Boolean = mask != null && mask.format("").isNotEmpty()

    fun accepts(text: String): Boolean = filter?.invoke(text) ?: true

    /** The text the field stores for the [value] of its variable. */
    fun storedText(value: String): String {
        if (mask == null) {
            return if (maxLength == null) value else value.limitLength(maxLength)
        }
        val raw = mask.sanitize(value)
        if (maxLength == null || mask.filledLength(raw) <= maxLength) return raw
        val kept = largestFitting(raw.length - 1) { mask.filledLength(raw.take(it)) <= maxLength }
        return raw.take(raw.wholeCharacters(kept))
    }
}

/**
 * The largest count in `0..max` that [fits], or 0: the formatted text of a mask grows with its raw
 * text, so the counts that fit precede the ones that do not and a bisection finds the boundary.
 */
private inline fun largestFitting(max: Int, fits: (Int) -> Boolean): Int {
    var low = 0
    var high = max
    while (low < high) {
        val mid = (low + high + 1) / 2
        if (fits(mid)) low = mid else high = mid - 1
    }
    return low
}

/** [end] moved before a surrogate pair it would split. */
private fun CharSequence.wholeCharacters(end: Int): Int {
    return if (end > 0 && end < length && this[end - 1].isHighSurrogate()) end - 1 else end
}

/**
 * Drops the characters [mask] cannot hold and the entered characters whose formatted text exceeds
 * [maxLength], the latter from the end of the edit: the text entered before a decrease of the limit
 * stays, as with the `LengthFilter` of the View renderer.
 */
@OptIn(ExperimentalFoundationApi::class)
private class MaskInputTransformation(
    private val mask: InputMask,
    private val maxLength: Int?,
) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (changes.changeCount == 0) return
        val typed = changes.getRange(0)
        if (changes.changeCount == 1 && typed.length == 1 && changes.getOriginalRange(0).length == 0) {
            val char = charAt(typed.min)
            val stored = mask.typedChar(char)
            if (stored != char) replace(typed.min, typed.max, stored.toString())
        }
        if (maxLength != null) {
            for (index in changes.changeCount - 1 downTo 0) {
                val edit = changes.getRange(index)
                val text = toString()
                if (mask.filledLength(mask.sanitizeEdit(text)) <= maxLength) break
                val kept = largestFitting(edit.length - 1) { kept ->
                    mask.filledLength(mask.sanitizeEdit(text.removeRange(edit.min + kept, edit.max))) <= maxLength
                }
                delete(text.wholeCharacters(edit.min + kept).coerceAtLeast(edit.min), edit.max)
            }
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
