package com.yandex.test.rules

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import org.junit.rules.ExternalResource

/**
 * Stops showing the last typed password character, so it does not get into snapshots.
 */
class NoPasswordPreviewRule : ExternalResource() {

    private val device = UiDevice.getInstance(getInstrumentation())

    override fun before() {
        device.executeShellCommand("settings put system show_password 0")
    }

    override fun after() {
        device.executeShellCommand("settings put system show_password 1")
    }
}
