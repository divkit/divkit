package com.yandex.test.util

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.SearchCondition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import java.util.regex.Pattern
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * UiAutomator actions
 * */
object UiAutomator {

    private const val DEFAULT_TIMEOUT = 5_000L
    private val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun pressBackOnDevice() = device.pressBack()

    fun waitUntilIdGone(targetId: String, timeout: Long? = null) {
        device.findObject(
            UiSelector()
                .resourceId("$packageName:id/$targetId")
        ).waitUntilGone(timeout ?: DEFAULT_TIMEOUT)
    }

    fun waitForIdWithEmptyOrBlankTextGone(targetId: String, timeout: Long? = null) {
        waitForCondition(
            "$targetId and empty or blank text",
            {
                Until.gone(
                    By.res("$packageName:id/$targetId")
                        .text(Pattern.compile("\\s*"))
                )
            },
            timeout
        )
    }

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

    fun waitForId(targetId: String, timeout: Long? = null) {
        waitForCondition(
            targetId,
            {
                Until.hasObject(
                    By.res(packageName, targetId)
                )
            },
            timeout
        )
    }

    fun waitForIdAndText(targetId: String, text: String, timeout: Long? = null) {
        waitForCondition(
            targetId,
            {
                Until.hasObject(
                    By.res(packageName, targetId).text(text)
                )
            },
            timeout
        )
    }

    fun waitForIdAndTextStartsWith(targetId: String, text: String, timeout: Long? = null) {
        waitForCondition(
            targetId,
            {
                Until.hasObject(
                    By.res(packageName, targetId).textStartsWith(text)
                )
            },
            timeout
        )
    }

    fun waitForViewWithContentDesc(contentDesc: String, timeout: Long? = null) {
        waitForCondition(
            contentDesc,
            {
                Until.hasObject(
                    By.descContains(contentDesc)
                )
            },
            timeout
        )
    }

    fun checkIdIsPresent(targetId: String, timeout: Long? = null): Boolean {
        return device.wait(
            Until.hasObject(
                By.res(packageName, targetId)
            ),
            timeout ?: DEFAULT_TIMEOUT
        )
    }

    fun setTextById(targetId: String, text: String, timeout: Long? = null) {
        waitForId(targetId, timeout)
        device.findObject(
            UiSelector()
                .resourceId("$packageName:id/$targetId")
        ).text = text
    }

    fun clickOnId(targetId: String, timeout: Long? = null) {
        waitForId(targetId, timeout)
        device.findObject(
            UiSelector()
                .resourceId("$packageName:id/$targetId")
        ).click()
    }

    fun clickOnIdAndText(targetId: String, targetText: String, timeout: Long? = null) {
        waitForId(targetId, timeout)
        device.findObject(
            UiSelector()
                .resourceId("$packageName:id/$targetId")
                .textMatches("(?i:$targetText)")
        ).click()
    }

    fun clickOnText(targetText: String, timeout: Long? = null) {
        waitForText(targetText, timeout)
        device.findObject(
            UiSelector()
                .textMatches("(?i:$targetText)")
        ).click()
    }

    fun scrollToText(text: String, timeout: Long = DEFAULT_TIMEOUT): UiObject {
        device.wait(Until.hasObject(By.scrollable(true)), timeout)
        val parent = UiScrollable(UiSelector().scrollable(true))
        parent.scrollIntoView(UiSelector().text(text))
        return device.findObject(UiSelector().text(text))
    }

    fun clickOnObjectWithContentDescr(contentDesc: String, timeout: Long? = null) {
        waitForViewWithContentDesc(contentDesc, timeout)
        device.findObject(
            UiSelector()
                .description(contentDesc)
        ).click()
    }

    fun clickOnNotification(appName: String, text: String, timeout: Long = DEFAULT_TIMEOUT) {
        try {
            device.openNotification()
            device.wait(Until.hasObject(By.textContains(appName)), timeout)
            device.wait(Until.hasObject(By.textContains(text)), timeout)
            device.findObject(By.textContains(text)).click()
        } finally {
            closeNotifications()
        }
    }

    fun closeNotifications() {
        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        InstrumentationRegistry.getInstrumentation().targetContext.sendBroadcast(it)
    }

    fun turnOffInternet() {
        device.executeShellCommand("svc wifi disable")
        device.executeShellCommand("svc data disable")
    }

    fun slowDownInternet() {
        device.executeShellCommand("svc wifi disable")
        device.executeShellCommand("svc data enable")
    }

    fun restoreInternet() {
        device.executeShellCommand("svc wifi enable")
        device.executeShellCommand("svc data enable")
    }

    fun changeOrientationLeft() {
        device.setOrientationLeft()
    }

    fun changeOrientationRight() {
        device.setOrientationRight()
    }

    fun restoreOrientation() {
        device.setOrientationNatural()
    }

    var showTouches: Boolean by BooleanSystemSetting("show_touches")
    var showPointerLocation: Boolean by BooleanSystemSetting("pointer_location")

    private fun setSystemSetting(name: String, value: String?) {
        if (value?.contains(' ') == true) {
            throw IllegalStateException()
        }
        device.executeShellCommand("settings put system $name $value")
    }

    private fun getSystemSetting(name: String): String? {
        val text = device.executeShellCommand("settings get system $name")
        return text.takeUnless { text == "null" || text.isBlank() }
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

    class BooleanSystemSetting(
        val name: String,
        val default: Boolean = false,
    ) : ReadWriteProperty<Any, Boolean> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return when (getSystemSetting(name)) {
                "1" -> true
                "0" -> false
                else -> default
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            setSystemSetting(name, if (value) "1" else "0")
        }

    }
}
