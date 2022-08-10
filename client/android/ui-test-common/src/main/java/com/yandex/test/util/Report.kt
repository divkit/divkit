package com.yandex.test.util

import android.util.Log
import ru.tinkoff.allure.Step
import ru.tinkoff.allure.android.Screenshot

object Report {

    private const val TAG = "UiTestReportUtils"

    fun <T : Any?> step(description: String, block: () -> T): T {
        Log.i(TAG, "starting step: $description")
        val result = Step.step(description, block = block)
        Log.i(TAG, "finishing step: $description")
        return result
    }

    fun takeDeviceScreenshot(name: String): Unit = Screenshot.deviceScreenshot(name)
}
