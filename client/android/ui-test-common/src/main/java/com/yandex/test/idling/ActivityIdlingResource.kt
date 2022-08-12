package com.yandex.test.idling

import android.app.Activity
import com.yandex.test.util.getCurrentActivity

class ActivityIdlingResource<A : Activity>(private val activityClass: Class<A>) : SimpleIdlingResource() {
    override fun checkIdle(): Boolean {
        val activity = getCurrentActivity() ?: return false
        return activity::class.java == activityClass
    }

    override fun getName() = "ActivityIdlingResource"
}
