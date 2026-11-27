package com.yandex.div.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow

internal class AnimationConfiguration(
    private val systemAnimationsEnabled: () -> Boolean,
    private val provider: Provider,
) {
    val isEnabled: Boolean
        get() = systemAnimationsEnabled() && provider.isAnimationsEnabled()

    @Composable
    fun isEnabledAsState(): Boolean {
        val enabled by provider.animationsEnabled.collectAsState()
        return systemAnimationsEnabled() && enabled
    }

    interface Provider {
        val animationsEnabled: StateFlow<Boolean>

        fun isAnimationsEnabled(): Boolean = animationsEnabled.value
    }
}
