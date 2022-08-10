package com.yandex.test.rules

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import org.junit.rules.ExternalResource

/**
 * Close pop-ups from other applications before tests running
 * Closes pop-ups with texts: "ok", "Close app" etc
 *
 * inspired by https://nda.ya.ru/3UFnJm
 */
class ClosePopupsRule : ExternalResource() {
    companion object {
        private const val TAG = "ClosePopupsRule"
        private val texts = listOf("OK", "Закрыть", "ОК", "Close", "Restart", "Перезапустить", "GOT IT")
    }

    private val device: UiDevice by lazy { UiDevice.getInstance(getInstrumentation()) }

    override fun before() {
        closePopups()
    }

    private fun closePopups() {
        for (text in texts) {
            tryClick(findButton(text))
        }
    }

    private fun findButton(text: String): UiObject {
        return device.findObject(
            UiSelector()
                .textStartsWith(text)
                .className("android.widget.Button")
        )
    }

    private fun tryClick(view: UiObject) {
        try {
            if (view.exists() && view.isEnabled && view.packageName != getInstrumentation().targetContext.packageName) {
                Log.i(TAG, "closing popup with button text '${view.text}'")
                view.click()
            }
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }
}
