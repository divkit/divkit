package com.yandex.div.internal.util

import android.os.Build
import android.text.Layout
import android.widget.TextView
import androidx.annotation.ChecksSdkIntAtLeast
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public val TextView.fontHeight: Float
    get() = paint.getFontMetrics(null)

// Rendering of Yandex Sans font with soft hyphens is broken on lower APIs.
// See MORDAANDROID-767
@InternalApi
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
public fun checkHyphenationSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@InternalApi
public fun TextView.isHyphenationEnabled(): Boolean {
    return checkHyphenationSupported() && hyphenationFrequency != Layout.HYPHENATION_FREQUENCY_NONE
}
