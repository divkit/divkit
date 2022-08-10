package com.yandex.test.util

import android.app.Activity
import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

internal fun getCurrentActivity(): Activity? {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        return getCurrentActivitySync()
    }
    var currentActivity: Activity? = null
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        currentActivity = getCurrentActivitySync()
    }
    return currentActivity
}

private fun getCurrentActivitySync(): Activity? {
    val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
        Stage.RESUMED
    )
    return resumedActivities.firstOrNull()
}
