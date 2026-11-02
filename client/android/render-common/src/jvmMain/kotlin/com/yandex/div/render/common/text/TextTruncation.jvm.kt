package com.yandex.div.render.common.text

import com.yandex.div.core.annotations.InternalApi
import java.text.BreakIterator
import java.util.Locale

/**
 * Creates truncation using the platform's locale-aware grapheme and line boundaries.
 */
@InternalApi
public fun TextTruncation(
    text: String,
    locale: Locale,
): TextTruncation = TextTruncation(text) { character ->
    val iterator = if (character) {
        BreakIterator.getCharacterInstance(locale)
    } else {
        BreakIterator.getLineInstance(locale)
    }
    iterator.setText(text)
    JavaTextBoundaryIterator(iterator)
}

internal actual fun isTrimmableWhitespace(codePoint: Int): Boolean =
    Character.isWhitespace(codePoint)

internal actual fun isCombiningMark(codePoint: Int): Boolean = when (Character.getType(codePoint)) {
    Character.NON_SPACING_MARK.toInt(),
    Character.COMBINING_SPACING_MARK.toInt(),
    Character.ENCLOSING_MARK.toInt() -> true
    else -> false
}

private class JavaTextBoundaryIterator(
    private val iterator: BreakIterator,
) : TextBoundaryIterator {
    override fun isBoundary(offset: Int): Boolean = iterator.isBoundary(offset)

    override fun preceding(offset: Int): Int = iterator.preceding(offset)
}
