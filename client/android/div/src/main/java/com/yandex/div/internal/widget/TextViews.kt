package com.yandex.div.internal.widget

import android.os.Build
import android.text.Layout
import android.widget.TextView
import androidx.annotation.ChecksSdkIntAtLeast
import com.yandex.div.core.annotations.InternalApi

internal val TextView.fontHeight: Float
    get() = paint.getFontMetrics(null)

internal val TextView.fontHeightInt: Int
    get() = paint.getFontMetricsInt(null)

/**
 * Calculates height of text for given first lines.
 * @param lines number of first lines of text that will be taken into account to calculate the height.
 *  To get full text height pass negative value.
 * @return the height of text. If [TextView] haven't text layout result will be 0.
 */
@InternalApi
fun TextView.textHeight(lines: Int = -1): Int {
    return when {
        layout == null -> 0
        lines <= 0 -> layout.height
        lines > layout.lineCount -> layout.height
        else -> layout.getLineTop(lines) - layout.getLineTop(0)
    }
}

internal fun TextView.lineAt(vertical: Int): Int {
    return when {
        layout == null -> 0
        else -> layout.getLineForVertical(vertical)
    }
}

/**
 * Some fonts (e.g., Yandex Sans) has rendering issues of text with soft hyphens at lower APIs.
 * @return whether soft hyphenation usage is safe.
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
internal fun checkHyphenationSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

internal fun TextView.isHyphenationEnabled(): Boolean {
    return checkHyphenationSupported() && hyphenationFrequency != Layout.HYPHENATION_FREQUENCY_NONE
}
