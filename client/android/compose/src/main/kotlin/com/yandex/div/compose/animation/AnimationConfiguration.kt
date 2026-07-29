package com.yandex.div.compose.animation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.util.isSystemAnimationsEnabled
import javax.inject.Inject

@DivContextScope
internal class AnimationConfiguration @Inject constructor(
    private val context: Context,
    private val provider: DivAnimationsEnabledProvider,
) {
    val isEnabled: Boolean
        get() = context.isSystemAnimationsEnabled() && provider.isAnimationsEnabled()

    @Composable
    fun isEnabledAsState(): Boolean {
        val enabled by provider.animationsEnabled.collectAsState()
        return context.isSystemAnimationsEnabled() && enabled
    }
}
