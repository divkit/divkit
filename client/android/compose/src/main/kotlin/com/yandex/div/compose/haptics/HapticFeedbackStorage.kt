package com.yandex.div.compose.haptics

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

@DivViewScope
internal class HapticFeedbackStorage @Inject constructor() {

    private var hapticFeedback: HapticFeedback? = null

    fun register(hapticFeedback: HapticFeedback) {
        this.hapticFeedback = hapticFeedback
    }

    fun unregister() {
        hapticFeedback = null
    }

    fun get(): HapticFeedback? {
        return hapticFeedback
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun HapticFeedbackStorage.bind() {
    val hapticFeedback = LocalHapticFeedback.current
    DisposableEffect(this, hapticFeedback) {
        register(hapticFeedback)
        onDispose { unregister() }
    }
}
