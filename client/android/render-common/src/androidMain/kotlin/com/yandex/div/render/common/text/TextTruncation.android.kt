package com.yandex.div.render.common.text

import android.os.Build
import androidx.annotation.RequiresApi
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val iterator = if (character) {
            android.icu.text.BreakIterator.getCharacterInstance(locale)
        } else {
            android.icu.text.BreakIterator.getLineInstance(locale)
        }
        iterator.setText(text)
        IcuTextBoundaryIterator(iterator)
    } else {
        val iterator = if (character) {
            BreakIterator.getCharacterInstance(locale)
        } else {
            BreakIterator.getLineInstance(locale)
        }
        iterator.setText(text)
        JavaTextBoundaryIterator(iterator)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private class IcuTextBoundaryIterator(
    private val iterator: android.icu.text.BreakIterator,
) : TextBoundaryIterator {
    override fun isBoundary(offset: Int): Boolean = iterator.isBoundary(offset)

    override fun preceding(offset: Int): Int = iterator.preceding(offset)
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
