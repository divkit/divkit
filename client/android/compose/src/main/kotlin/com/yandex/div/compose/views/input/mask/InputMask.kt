package com.yandex.div.compose.views.input.mask

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.insert

/**
 * Formatting of a masked input: the characters the user entered fill the dynamic slots of a
 * pattern, the static characters of the pattern are shown between them.
 */
internal abstract class InputMask {

    /**
     * Drops the characters the mask cannot hold from the value of the raw variable. The result is
     * what the field stores.
     */
    abstract fun sanitize(raw: String): String

    /** Drops the characters the mask cannot hold from the [text] of an edit the user made. */
    open fun sanitizeEdit(text: String): String = sanitize(text)

    /** The character the field stores for a single [char] the user typed. */
    open fun typedChar(char: Char): Char = char

    /** The value the raw variable receives for the stored [raw] text. */
    open fun toVariableValue(raw: String): String = raw

    /** The pattern [raw] is formatted with; its dynamic characters are the slots of raw characters. */
    protected abstract fun pattern(raw: String): String

    protected abstract fun isDynamic(patternChar: Char): Boolean

    /** The placeholder of the empty slot at [patternIndex], or null to end the formatted text there. */
    protected open fun placeholder(patternIndex: Int): Char? = null

    /** The formatted text, as the View renderer writes it to `text_variable`. */
    fun format(raw: String): String = buildString {
        walk(raw) { char, _ -> append(char) }
    }

    /** Inserts the static characters of the pattern into the raw text of a field. */
    val outputTransformation = OutputTransformation {
        val statics = StringBuilder()
        var position = 0
        walk(toString()) { char, isRaw ->
            if (isRaw) {
                if (statics.isNotEmpty()) {
                    insert(position, statics.toString())
                    position += statics.length
                    statics.clear()
                }
                position++
            } else {
                statics.append(char)
            }
        }
        if (statics.isNotEmpty()) insert(position, statics.toString())
    }

    private inline fun walk(raw: String, emit: (char: Char, isRaw: Boolean) -> Unit) {
        val pattern = pattern(raw)
        var rawIndex = 0
        var patternIndex = 0
        while (patternIndex < pattern.length) {
            val patternChar = pattern[patternIndex]
            when {
                !isDynamic(patternChar) -> emit(patternChar, false)
                rawIndex < raw.length -> emit(raw[rawIndex++], true)
                else -> emit(placeholder(patternIndex) ?: break, false)
            }
            patternIndex++
        }
        while (rawIndex < raw.length) emit(raw[rawIndex++], true)
    }
}
