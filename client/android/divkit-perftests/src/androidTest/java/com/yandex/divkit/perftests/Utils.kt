package com.yandex.divkit.perftests

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.SearchCondition
import com.yandex.perftests.runner.PerfTestUtils
import com.yandex.perftests.runner.ProcessReporter

internal fun PerfTestUtils.startActivity(
    activityClass: String,
    extras: Bundle = Bundle.EMPTY,
    waitCondition: SearchCondition<*>? = null
) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent().apply {
        component = ComponentName(PACKAGE_NAME, activityClass)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtras(extras)
    }
    context.startActivity(intent)

    if (waitCondition == null) {
        device.waitForWindowUpdate(null, TIMEOUT)
    } else {
        device.wait(waitCondition, TIMEOUT)
            ?: throw RuntimeException("Wait condition was not satisfied! ")
    }
}

internal fun PerfTestUtils.pressBack(delay: Long = 1000L) {
    device.pressBack()
    Thread.sleep(delay)
}

internal inline fun PerfTestUtils.report(tag: String, action: () -> Unit) {
    val reporter = ProcessReporter(PACKAGE_NAME, device)
    action()
    reporter.report(tag)
}

private const val TIMEOUT: Long = 30_000L
