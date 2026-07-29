@file:JvmName("DivSystemAnimationUtils")

package com.yandex.div.core.util

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public fun Context.isSystemAnimationsEnabled(): Boolean {
    val scale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ValueAnimator.getDurationScale()
    } else {
        @Suppress("DEPRECATION")
        Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )
    }
    return scale != 0f
}
