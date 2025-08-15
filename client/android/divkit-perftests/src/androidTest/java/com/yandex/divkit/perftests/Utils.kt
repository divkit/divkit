package com.yandex.divkit.perftests

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.SearchCondition
import com.yandex.perftests.runner.PerfTestUtils
import com.yandex.perftests.runner.ProcessReporter

internal fun PerfTestUtils.grantPermission(packageName: String, permission: String) {
    device.executeShellCommand("pm grant $packageName $permission")
}

internal fun PerfTestUtils.startActivity(
    packageName: String,
    activityClass: String,
    extras: Bundle = Bundle.EMPTY,
    waitCondition: SearchCondition<*>? = null,
    launchTimeout: Long = 30_000L
) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent().apply {
        component = ComponentName(packageName, activityClass)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtras(extras)
    }
    context.startActivity(intent)

    if (waitCondition == null) {
        device.waitForWindowUpdate(null, launchTimeout)
    } else {
        device.wait(waitCondition, launchTimeout)
            ?: throw RuntimeException("Wait condition was not satisfied! ")
    }
}

internal fun PerfTestUtils.pressBack(delay: Long = 1000L) {
    device.pressBack()
    Thread.sleep(delay)
}

internal inline fun PerfTestUtils.report(packageName: String, tag: String, action: () -> Unit) {
    val reporter = ProcessReporter(packageName, device)
    action()
    reporter.report(tag)
}
