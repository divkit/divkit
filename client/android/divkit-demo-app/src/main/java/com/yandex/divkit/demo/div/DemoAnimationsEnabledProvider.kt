package com.yandex.divkit.demo.div

import com.yandex.div.core.DivAnimationsEnabledProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DemoAnimationsEnabledProvider(
    private val enabled: () -> Boolean,
) : DivAnimationsEnabledProvider {

    private val state by lazy { MutableStateFlow(enabled()) }

    override val animationsEnabled: StateFlow<Boolean>
        get() = state

    fun notifyChanged() {
        state.value = enabled()
    }
}
