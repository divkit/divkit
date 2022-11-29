package com.yandex.div.internal.util

import android.os.Build
import android.text.Layout
import android.widget.TextView
import androidx.annotation.ChecksSdkIntAtLeast

val TextView.fontHeight: Int
    get() = paint.getFontMetricsInt(null)

// Rendering of Yandex Sans font with soft hyphens is broken on lower APIs.
// See MORDAANDROID-767
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun checkHyphenationSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun TextView.isHyphenationEnabled(): Boolean {
    return checkHyphenationSupported() && hyphenationFrequency != Layout.HYPHENATION_FREQUENCY_NONE
}
