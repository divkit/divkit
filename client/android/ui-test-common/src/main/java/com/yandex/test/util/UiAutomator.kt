package com.yandex.test.util

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.SearchCondition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.Until
import java.util.regex.Pattern

/**
 * UiAutomator actions
 * */
object UiAutomator {

    private const val DEFAULT_TIMEOUT = 5_000L
    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun waitForText(targetText: String, timeout: Long? = null) {
        waitForCondition(
            targetText,
            {
                Until.hasObject(
                    By.text(Pattern.compile("(?i:$targetText)"))
                )
            },
            timeout
        )
    }

    private fun waitForCondition(
        target: String,
        condition: () -> SearchCondition<Boolean>,
        timeout: Long?
    ) {
        val viewIsDisplayed = device.wait(
            condition.invoke(),
            timeout ?: DEFAULT_TIMEOUT
        )
        if (!viewIsDisplayed) {
            throw UiObjectNotFoundException("View contains \"$target\" is not found")
        }
    }
}
