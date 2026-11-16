package com.yandex.div.render.common.animation

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public class SystemAnimationConfiguration(
    private val context: Context,
) {
    public val isEnabled: Boolean
        get() {
            val scale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ValueAnimator.getDurationScale()
            } else {
                @Suppress("DEPRECATION")
                Settings.Global.getFloat(
                    context.contentResolver,
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1f,
                )
            }
            return scale != 0f
        }
}
