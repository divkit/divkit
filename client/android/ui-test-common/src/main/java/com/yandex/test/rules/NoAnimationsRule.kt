package com.yandex.test.rules

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NoAnimationsRule : TestRule {

    private val device = UiDevice.getInstance(getInstrumentation())

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            try {
                disableAnimations()
                base.evaluate()
            } finally {
                enableAnimations()
            }
        }
    }

    private fun disableAnimations() {
        toggleAnimations(false)
    }

    private fun enableAnimations() {
        toggleAnimations(true)
    }

    private fun toggleAnimations(isEnabled: Boolean) {
        AnimationType.values().forEach { type ->
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
