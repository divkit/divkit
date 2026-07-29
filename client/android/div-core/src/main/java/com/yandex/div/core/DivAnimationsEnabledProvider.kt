package com.yandex.div.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public interface DivAnimationsEnabledProvider {

    public val animationsEnabled: StateFlow<Boolean>

    public fun isAnimationsEnabled(): Boolean = animationsEnabled.value

    public companion object {

        @JvmField
        public val DEFAULT: DivAnimationsEnabledProvider = object : DivAnimationsEnabledProvider {
            override val animationsEnabled: StateFlow<Boolean> = MutableStateFlow(true)
        }
    }
}
