package com.yandex.div.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.yandex.div.compose.di.DivContextScope
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.render.common.animation.SystemAnimationConfiguration
import dev.zacsweers.metro.Inject

@DivContextScope
@Inject
internal class AnimationConfiguration(
    private val systemConfiguration: SystemAnimationConfiguration,
    private val provider: DivAnimationsEnabledProvider,
) {
    val isEnabled: Boolean
        get() = systemConfiguration.isEnabled && provider.isAnimationsEnabled()

    @Composable
    fun isEnabledAsState(): Boolean {
        val enabled by provider.animationsEnabled.collectAsState()
        return systemConfiguration.isEnabled && enabled
    }
}
