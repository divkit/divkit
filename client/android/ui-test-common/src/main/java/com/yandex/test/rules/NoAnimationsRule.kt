package com.yandex.test.rules

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import org.junit.rules.ExternalResource

class NoAnimationsRule : ExternalResource() {

    private val device = UiDevice.getInstance(getInstrumentation())

    override fun before() = toggleAnimations(false)

    override fun after() = toggleAnimations(true)

    private fun toggleAnimations(isEnabled: Boolean) {
        AnimationType.entries.forEach { type ->
            toggleAnimationsByType(type, isEnabled)
        }
    }

    private fun toggleAnimationsByType(type: AnimationType, enabled: Boolean) {
        device.executeShellCommand("settings put global ${type.value} ${if (enabled) "1.0" else "0.0"}")
    }

    private enum class AnimationType(val value: String) {
        WINDOW_ANIMATION_SCALE("window_animation_scale"),
        TRANSITION_ANIMATION_SCALE("transition_animation_scale"),
        ANIMATOR_DURATION_SCALE("animator_duration_scale")
    }
}
